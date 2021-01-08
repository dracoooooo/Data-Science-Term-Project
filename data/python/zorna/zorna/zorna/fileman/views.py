# Create your views here.
import os
import stat
import mimetypes
import shutil
import posixpath
import urllib
from django.conf import settings
from django.utils.translation import gettext
from django.db.models import Q
from datetime import datetime
from django.contrib.auth.models import User
from django.utils.translation import ugettext_lazy as _
from django.template.defaultfilters import slugify
from django.shortcuts import render_to_response
from django.template import RequestContext, loader
from django.http import HttpResponseRedirect, HttpResponseForbidden
from django.core.urlresolvers import reverse
from django.utils import simplejson
from django.http import HttpResponse
from django.forms.formsets import formset_factory
from django.contrib.auth.decorators import login_required
from django.template.loader import render_to_string
from haystack.query import SearchQuerySet

from zorna.acl.models import get_allowed_objects, get_acl_by_object

from zorna.site.models import SiteOptions
from zorna.site.email import ZornaEmail
from zorna.communities.models import Community
from zorna.communities.api import get_communities
from zorna.utilit import get_upload_library, get_upload_user
from zorna.fileman.models import ZornaFile, ZornaFolder
from zorna.fileman.forms import ZornaFileForm, ZornaFileAddForm, ZornaFolderForm, ZornaFileUploadForm
from zorna.fileman.api import recent_files, split_file_name, get_allowed_folders, \
        get_allowed_shared_folders, get_path_components, get_user_access_to_path,get_files_from_query


def get_url_folder_content(path):
    if path[0] == 'U':
        return reverse('personal_folder_content')
    elif path[0] == 'C':
        return reverse('communities_folder_content')
    elif path[0] == 'F':
        return reverse('shared_folder_content')
    else:
        return ''


@login_required()
def admin_list_folders(request):
    if request.user.is_superuser:
        ob_list = ZornaFolder.objects.all()
        extra_context = {}
        extra_context['folders_list'] = ob_list
        context = RequestContext(request)
        return render_to_response('fileman/list_folders.html', extra_context, context_instance=context)
    else:
        return HttpResponseRedirect('/')


@login_required()
def admin_add_folder(request):
    if request.user.is_superuser:
        if request.method == 'POST':
            form = ZornaFolderForm(request.POST)
            if form.is_valid():
                category = form.save(commit=False)
                category.owner = request.user
                category.save()
                return HttpResponseRedirect(reverse('admin_list_folders'))
            else:
                form = ZornaFolderForm(request.POST)
        else:
            form = ZornaFolderForm()

        context = RequestContext(request)
        extra_context = {'form': form, 'curfolder': False}
        return render_to_response('fileman/edit_folder.html', extra_context, context_instance=context)
    else:
        return HttpResponseRedirect('/')


@login_required()
def admin_edit_folder(request, folder):
    if request.user.is_superuser:
        c = ZornaFolder.objects.get(pk=folder)
        if request.method == 'POST':
            form = ZornaFolderForm(request.POST, instance=c)
            if request.POST.has_key('bdelete'):
                fullpath = u"%s/%s" % (get_upload_library(), u"F%s" % c.pk)
                bexist = os.path.isdir(fullpath)
                if not bexist or not os.listdir(fullpath):
                    if bexist:
                        os.rmdir(fullpath)
                    c.delete()
                    return HttpResponseRedirect(reverse('admin_list_folders'))

            if form.is_valid():
                form.save()
                return HttpResponseRedirect(reverse('admin_list_folders'))
        else:
            form = ZornaFolderForm(instance=c)

        fullpath = u"%s/%s" % (get_upload_library(), u"F%s" % c.pk)
        if not os.path.isdir(fullpath) or not os.listdir(fullpath):
            bdelete = True
        else:
            bdelete = False

        context = RequestContext(request)
        extra_context = {'form': form, 'curfolder': c, 'bdelete': bdelete}
        return render_to_response('fileman/edit_folder.html', extra_context, context_instance=context)
    else:
        return HttpResponseRedirect('/')


def get_shared_folder(path):
    dirs = path.split('/')
    if dirs[0][0] == "U" or dirs[0][0] == "C":
        return None
    else:
        try:
            folder = ZornaFolder.objects.get(pk=int(dirs[0][1:]))
            return folder
        except:
            return None


def notify_users(request, folder, files, upload=True):
    if folder.inherit_permissions:
        parents = folder.get_ancestors()
        for f in parents:
            if f.inherit_permissions is False:
                folder = f
                break
    users_list = get_acl_by_object(folder, 'reader')
    email = ZornaEmail()
    url = request.build_absolute_uri(reverse(
        'documents', args=[])) + '?dir=F%s' % folder.pk
    ec = {"folder": folder, "files": files, 'url':
          url, 'upload': upload, 'user': request.user}
    body_text = render_to_string('fileman/email_notification_text.html', ec)
    body_html = render_to_string('fileman/email_notification_html.html', ec)
    if upload:
        subject = _(u'A new file has been uploaded')
    else:
        subject = _(u'A file has been updated')
    step = getattr(settings, "ZORNA_MAIL_MAXPERPACKET", 25)
    users_email = [user.email for user in users_list]
    for n in range(0, len(users_email) / step + 1):
        email.append(subject, body_text, body_html, settings.DEFAULT_FROM_EMAIL, bcc=users_email[
                     n * step:(n + 1) * step])
    email.send()


@login_required()
def documents(request):
    if request.user.is_authenticated:
        extra_context = {}
        ppath = request.GET.get('path', '')
        ppath = urllib.unquote(ppath.rstrip('/'))
        path = clean_path(ppath)
        if path != ppath:
            path = ''

        extra_context['path'] = path

        if request.user.is_anonymous():
            extra_context['bpersonal_folder'] = False
        else:
            extra_context['bpersonal_folder'] = SiteOptions.objects.is_access_valid(
                request.user, 'zorna_personal_documents')
            extra_context['bpersonal_dir'] = 'U%s/' % request.user.pk
            get_upload_user(request.user)
        communities = get_communities(request.user)
        ao = [com.pk for com in communities if com.enable_documents]
        if ao:
            extra_context['bcommunities_folder'] = True
        else:
            extra_context['bcommunities_folder'] = False
        ao = get_allowed_shared_folders(
            request.user, ['reader', 'manager', 'writer'])
        if ao:
            extra_context['bshared_folder'] = True
        else:
            extra_context['bshared_folder'] = False

        if path and path[0] == 'U':
            extra_context['dir_url'] = reverse('personal_folder_content')
            extra_context['bshared_folder'] = False
            extra_context['bcommunities_folder'] = False
        elif path and path[0] == 'F':
            extra_context['dir_url'] = reverse('shared_folder_content')
            extra_context['bpersonal_folder'] = False
            extra_context['bcommunities_folder'] = False
        elif path and path[0] == 'C':
            extra_context['dir_url'] = reverse('communities_folder_content')
            extra_context['bshared_folder'] = False
            extra_context['bpersonal_folder'] = False
        else:
            extra_context['dir_url'] = ''

        if extra_context['bpersonal_folder'] or extra_context['bcommunities_folder'] or extra_context['bshared_folder']:
            context = RequestContext(request)
            return render_to_response('fileman/fm_home.html', extra_context, context_instance=context)
        else:
            return HttpResponseForbidden()
    else:
        return HttpResponseForbidden()


def clean_path(path):
    path = posixpath.normpath(urllib.unquote(path))
    path = path.lstrip('/')
    newpath = ''
    for part in path.split('/'):
        if not part:
            # Strip empty path components.
            continue
        drive, part = os.path.splitdrive(part)
        head, part = os.path.split(part)
        if part in (os.curdir, os.pardir):
            # Strip '.' and '..' in path.
            continue
        newpath = os.path.join(newpath, part).replace('\\', '/')
    return newpath


def get_sub_folders(dir_path, user=None):
    ret = []
    root_path = get_upload_library()
    d = urllib.unquote(dir_path)
    dirs = d.split('/')
    if dirs[0][0] == 'F' and len(dirs) == 1:
        allowed_objects = get_allowed_shared_folders(
            user, ['reader', 'manager', 'writer'])
        ob_list = ZornaFolder.objects.filter(Q(pk__in=allowed_objects) | Q(
            inherit_permissions=True), parent__pk=dirs[0][1:])
        for f in ob_list:
            fullpath = u"%s/%s" % (root_path, u"F%s" % f.pk)
            if not os.path.isdir(fullpath):
                os.makedirs(fullpath)
            ret.append({'name': f.name, 'rel': 'F%s' % f.pk})

    path = u"%s/%s" % (root_path, d)
    for f in os.listdir(path):
        ff = os.path.join(path, f)
        if os.path.isdir(ff):
            relpath = u"%s/%s" % (d, f)
            ret.append({'name': f, 'rel': relpath})
    return ret


def dirlist_personal_folders(request):
    r = ['<ul class="jqueryFileTree" style="display: none;">']
    root = 'U%s' % request.user.pk
    root_name = u"My Documents"
    ppath = request.GET.get('dir', '')
    ppath = urllib.unquote(ppath.rstrip('/'))
    path = clean_path(ppath)
    if path != ppath:
        path = ''

    if not path:
        ppath = request.GET.get('path', None)
        if ppath:
            ppath = urllib.unquote(ppath.rstrip('/'))
            folder_dest = clean_path(ppath)
            buser, bmanager = get_user_access_to_path(
                request.user, folder_dest)
            if buser:
                ret = get_sub_folders(folder_dest, request.user)
            else:
                ret = []
        else:
            ret = [{'name': root_name, 'rel': root}]
    else:
        buser, bmanager = get_user_access_to_path(request.user, path)
        if buser:
            ret = get_sub_folders(path)
        else:
            ret = []

    for d in ret:
        r.append(
            '<li class="directory collapsed"><a href="#" rel="%s/" title="%s/">%s</a></li>' %
            (d['rel'], d['rel'], d['name']))

    r.append('</ul>')
    return HttpResponse(''.join(r))


def dirlist_communities_folders(request):
    r = ['<ul class="jqueryFileTree" style="display: none;">']
    ppath = request.GET.get('dir', '')
    ppath = urllib.unquote(ppath.rstrip('/'))
    path = clean_path(ppath)
    if path != ppath:
        path = ''
    if path:
        buser, bmanager = get_user_access_to_path(request.user, path)
        if buser:
            ret = get_sub_folders(path)
        else:
            ret = []
    else:
        ppath = request.GET.get('path', None)
        if ppath:
            ppath = urllib.unquote(ppath.rstrip('/'))
            folder_dest = clean_path(ppath)
            buser, bmanager = get_user_access_to_path(
                request.user, folder_dest)
            if buser:
                ret = get_sub_folders(folder_dest, request.user)
            else:
                ret = []
        else:
            allowed_objects = set([])
            for perm in ['member', 'manage']:
                objects = get_allowed_objects(request.user, Community, perm)
                allowed_objects = allowed_objects.union(set(objects))
            root_path = get_upload_library()
            ret = []
            com = Community.objects.filter(
                pk__in=allowed_objects, enable_documents=True)
            for c in com:
                path = u"C%s" % c.pk
                fullpath = u"%s/%s" % (root_path, path)
                if not os.path.isdir(fullpath):
                    os.makedirs(fullpath)
                pathx = c.name
                ret.append({'name': pathx, 'rel': path})

    for d in ret:
        r.append(
            '<li class="directory collapsed"><a href="#" rel="%s/" title="%s/">%s</a></li>' %
            (d['rel'], d['rel'], d['name']))
    r.append('</ul>')
    return HttpResponse(''.join(r))


def dirlist_shared_folders(request):
    r = ['<ul class="jqueryFileTree" style="display: none;">']
    pdir = request.GET.get('dir', '')
    pdir = urllib.unquote(pdir.rstrip('/'))
    path = clean_path(pdir)
    if path != pdir:
        path = ''

    if path:
        buser, bmanager = get_user_access_to_path(request.user, path)
        if buser:
            ret = get_sub_folders(path, request.user)
        else:
            ret = []
    else:
        ppath = request.GET.get('path', None)
        if ppath:
            ppath = urllib.unquote(ppath.rstrip('/'))
            folder_dest = clean_path(ppath)
            buser, bmanager = get_user_access_to_path(
                request.user, folder_dest)
            if buser:
                ret = get_sub_folders(folder_dest, request.user)
            else:
                ret = []
        else:
            allowed_objects = get_allowed_shared_folders(
                request.user, ['reader', 'manager', 'writer'])
            ret = []
            ob_list = ZornaFolder.objects.filter(
                pk__in=allowed_objects, parent__isnull=True)
            root_path = get_upload_library()
            for f in ob_list:
                path = u"F%s" % f.pk
                fullpath = u"%s/%s" % (root_path, path)
                if not os.path.isdir(fullpath):
                    os.makedirs(fullpath)
                ret.append({'name': f.name, 'rel': path})

    for d in ret:
        r.append(
            '<li class="directory collapsed"><a href="#" rel="%s/" title="%s/">%s</a></li>' %
            (d['rel'], d['rel'], d['name']))
    r.append('</ul>')
    return HttpResponse(''.join(r))


def get_home_page_files(request, search_tag, search_text):
    if not search_tag and not search_text:
        return recent_files(request, 'all', 10)
    else:
        if search_tag and search_text:
            query = search_tag + " OR " + search_text
        else:
            query = search_tag if search_tag else search_text
        folders = get_allowed_folders(request)
        results_files = SearchQuerySet().filter(
            content=query).filter(folder__in=folders).models(ZornaFile)
        files = []
        for fic in results_files:
            files.append(int(fic.pk))
        files = ZornaFile.objects.filter(pk__in=files)
        return get_files_from_query(request, files)


def get_files(request, path, search_tag, search_text):
    root_path = get_upload_library()
    buser, bmanager = get_user_access_to_path(request.user, path)
    fullpath = u"%s/%s" % (root_path, urllib.unquote(path))
    fileList = {}
    files_id = []
    search_text_files = []
    dirs = []
    import re
    for f in os.listdir(fullpath):
        ff = os.path.join(fullpath, f)
        if os.path.isdir(ff):
            dirs.append(f)
            continue
        pk, fname = split_file_name(f)
        if pk is False:
            continue
        else:
            files_id.append(int(pk))

        statinfo = os.stat(ff)
        fileList[pk] = {'name': fname,
                        'realname': f,
                        'size': statinfo[stat.ST_SIZE],
                        'creation': datetime.fromtimestamp(statinfo[stat.ST_CTIME]),
                        'modification': datetime.fromtimestamp(statinfo[stat.ST_MTIME]),
                        'ext': os.path.splitext(f)[1][1:],
                        'path': path,
                        }

        if search_text and re.search(search_text, fname, re.IGNORECASE):
            search_text_files.append(pk)

    filterreq = None
    if search_tag:
        filterreq = Q(tags__icontains=search_tag)
    if search_text:
        if filterreq:
            filterreq = filterreq & Q(description__icontains=search_text)
        else:
            filterreq = Q(description__icontains=search_text) | Q(
                tags__icontains=search_text)

    if search_text_files:
        if filterreq:
            filterreq = filterreq | Q(pk__in=search_text_files)
        else:
            filterreq = Q(pk__in=search_text_files)

    files = ZornaFile.objects.filter(pk__in=files_id)
    if filterreq:
        files = files.filter(filterreq)
    for f in files:
        f.file_name = fileList[str(f.pk)]['name']
        f.file_info = fileList[str(f.pk)]

    import operator
    files = list(files)
    files.sort(key=operator.attrgetter('file_name'))
    return files, dirs


def get_folder_content(request):
    search_tag = request.GET.get('search_tag', '')
    search_text = request.GET.get('search_text', '')
    ppath = request.GET.get('dir', '')
    ppath = urllib.unquote(ppath.rstrip('/'))
    path = clean_path(ppath)
    if path != ppath:
        path = ''

    default_text = _(u'Recent files')
    bdelete = False
    if path == '':
        files = get_home_page_files(request, search_tag, search_text)
        if search_tag or search_text:
            default_text = _(u'Search results')
        bmanager = False
        folder_item_template = 'fileman/fm_folder_item_home.html'
    else:
        buser, bmanager = get_user_access_to_path(request.user, path)
        if not buser:
            return HttpResponse('')
        else:
            files, dirs = get_files(request, path, search_tag, search_text)
            folder_item_template = 'fileman/fm_folder_item.html'
            if dirs or files:
                bdelete = False
            else:
                bdelete = True

    parts = path.split('/')
    if len(parts) == 1:
        brename = False
        bdelete = False
    else:
        brename = True
    cdir_components = get_path_components(path, default_text)
    extra_context = {'cdir_components': cdir_components,
                     'files': files,
                     'search_text': search_text,
                     'search_tag': search_tag,
                     'manager': bmanager,
                     'brename': brename,
                     'bdelete': bdelete
                     }
    extra_context['folder_item_template'] = folder_item_template
    context = RequestContext(request)
    return render_to_response('fileman/fm_folder_content.html', extra_context, context_instance=context)


def recent_files_content(request):
    return get_folder_content(request)


def personal_folder_content(request):
    return get_folder_content(request)


def communities_folder_content(request):
    return get_folder_content(request)


def shared_folder_content(request):
    return get_folder_content(request)


def fm_delete_folder(request):
    ret = {'data': ''}
    if request.method == 'POST':
        ppath = request.POST.get('path_rel', '')
        ppath = urllib.unquote(ppath.rstrip('/'))
        folder_dest = clean_path(ppath)
        buser, bmanager = get_user_access_to_path(request.user, folder_dest)
        parts = folder_dest.split('/')
        if folder_dest and bmanager and len(parts) > 1:
            root_path = get_upload_library()
            try:
                path_src = u"%s/%s" % (root_path, urllib.unquote(folder_dest))
                os.rmdir(path_src)
                ret['status'] = 'success'
                ret['message'] = gettext(u'Folder deleted successfully')
                ret['data'] = u"%s/" % '/'.join(parts[0:-1])
            except Exception as e:
                ret['status'] = 'error'
                ret['message'] = 'Error: %s' % str(e)
        else:
            ret['status'] = 'error'
            ret['message'] = gettext(u'Access denied')
    return HttpResponse(simplejson.dumps(ret))


def fm_manage_folder(request):
    ret = {'data': ''}
    if request.method == 'POST':
        what = request.POST.get('what', '')
        ppath = request.POST.get('path_rel', '')
        ppath = urllib.unquote(ppath.rstrip('/'))
        folder_dest = clean_path(ppath)
        buser, bmanager = get_user_access_to_path(request.user, folder_dest)
        if folder_dest and bmanager:
            root_path = get_upload_library()
            path_dest = clean_path(folder_dest)
            parts = path_dest.split('/')
            if what == 'rename' and len(parts) > 1:
                new_folder = request.POST.get('new_folder', '').strip()
                n_folder = clean_path(new_folder)
                if new_folder == n_folder:
                    try:
                        n_folder = urllib.unquote(n_folder)
                        path_src = u"%s/%s" % (
                            root_path, urllib.unquote(path_dest))
                        dest = parts[:-1]
                        dest = '/'.join(dest)
                        path_dest = u"%s/%s/%s" % (
                            root_path, urllib.unquote(dest), n_folder)
                        os.rename(path_src, path_dest)
                        ret['status'] = 'success'
                        ret['message'] = gettext(
                            u'Folder renamed successfully')
                        ret['data'] = u"%s/%s/" % (
                            urllib.unquote(dest), n_folder)
                    except Exception as e:
                        ret['status'] = 'error'
                        ret['message'] = e
                else:
                    ret['status'] = 'error'
                    ret['message'] = gettext(u'Invalid folder name')
            elif what == 'create':
                new_folder = request.POST.get('new_folder', '').strip()
                n_folder = clean_path(new_folder)
                if new_folder == n_folder:
                    try:
                        n_folder = urllib.unquote(n_folder)
                        rel = '%s/%s' % (urllib.unquote(path_dest), n_folder)
                        path = u"%s/%s" % (root_path, rel)
                        os.mkdir(path)
                        ret['status'] = 'success'
                        ret['message'] = gettext(
                            u'Folder created successfully')
                        ret['data'] = '<li class="directory collapsed"><a href="#" rel="%s/">%s</a></li>' % (
                            rel, n_folder)
                    except Exception as e:
                        ret['status'] = 'error'
                        ret['message'] = e
                else:
                    ret['status'] = 'error'
                    ret['message'] = gettext(u'Invalid folder name')
        else:
            ret['status'] = 'error'
            ret['message'] = gettext(u'Access denied')
    return HttpResponse(simplejson.dumps(ret))


def get_file(request):

    pathfile = request.GET.get('file', None)
    inline = request.GET.get('inline', None)
    if pathfile:
        path = clean_path(pathfile)
        if path != pathfile:
            return HttpResponseForbidden()
        buser, bmanager = get_user_access_to_path(request.user, path)
        if buser is False:
            return HttpResponseForbidden()

        f = os.path.basename(path)
        path = "%s/%s" % (get_upload_library(), path)
        fp = open(path, 'rb')
        content_type = mimetypes.guess_type(f)[0]
        response = HttpResponse(fp.read(), content_type=content_type)
        response['Content-Length'] = os.path.getsize(path)
        pk, filename = split_file_name(f)
        disposition = 'inline' if inline else 'attachment'
        response['Content-Disposition'] = "%s; filename=%s" % (disposition, filename)
        return response
    else:
        return HttpResponseForbidden()


def fm_rename_file(request):
    pathfile = request.POST.get('dir', None)
    newfile = request.POST.get('new_name', None)
    ret = {}
    if pathfile and newfile:
        path = clean_path(pathfile)
        if path != pathfile:
            ret['message'] = gettext(u"Incorrect path")
            ret['status'] = 'error'
        else:
            buser, bmanager = get_user_access_to_path(request.user, path)
            if bmanager is False:
                ret['message'] = gettext(u"Access denied")
                ret['status'] = 'error'
            else:
                try:
                    oldfile = os.path.basename(path)
                    tab = oldfile.split(',')
                    path = os.path.dirname(path)
                    s = os.path.splitext(newfile)
                    fname = slugify(s[0])
                    fext = s[1]
                    dest = u"%s/%s/%s" % (
                        get_upload_library(), path, u"%s,%s%s" % (tab[0], fname, fext))
                    src = u"%s/%s/%s" % (get_upload_library(), path, oldfile)
                    os.rename(src, dest)
                    file = ZornaFile.objects.get(pk=int(tab[0]))
                    file.modifier = request.user
                    file.save()
                    ret['message'] = gettext(u"File renamed successfully")
                    ret['status'] = 'success'
                except Exception as e:
                    ret['message'] = gettext(u"Invalid file path")
                    ret['status'] = 'error'
    if ret['status'] == 'error':
        ret['new_name'] = os.path.basename(pathfile)
    else:
        ret['new_name'] = u"%s%s" % (fname, fext)

    json_data = simplejson.dumps(ret)
    return HttpResponse(json_data)


def fm_delete_file(request):
    pathfile = request.POST.get('dir', None)
    ret = {}
    if pathfile:
        path = clean_path(pathfile)
        if path != pathfile:
            ret['message'] = gettext(u"Incorrect path")
            ret['status'] = 'error'
        else:
            buser, bmanager = get_user_access_to_path(request.user, path)
            if bmanager is False:
                ret['message'] = gettext(u"Access denied")
                ret['status'] = 'error'
            else:
                try:
                    fname = os.path.basename(pathfile)
                    pk, filename = split_file_name(fname)
                    os.remove(get_upload_library() + '/' + path)
                    ZornaFile.objects.get(pk=int(pk)).delete()
                    ret['message'] = gettext(u"File deleted successfully")
                    ret['status'] = 'success'
                except:
                    ret['message'] = gettext(u"Invalid file name")
                    ret['status'] = 'error'
    else:
        ret['message'] = gettext(u"Invalid file path")
        ret['status'] = 'error'

    json_data = simplejson.dumps(ret)
    return HttpResponse(json_data)


def fm_upload_view(request):
    pathfile = request.REQUEST.get('dir', None)
    ret = {}
    if pathfile:
        ppath = urllib.unquote(pathfile.rstrip('/'))
        path_dest = clean_path(ppath)
        if path_dest != ppath:
            ret['message'] = gettext(u"Incorrect path")
            ret['status'] = 'error'
        else:
            buser, bmanager = get_user_access_to_path(request.user, path_dest)
            folder = get_shared_folder(path_dest)
            if bmanager is False:
                ret['message'] = gettext(u"Access denied")
                ret['status'] = 'error'
            else:
                cdir_components = get_path_components(path_dest)
                if request.method == 'POST':
                    root_path = get_upload_library()
                    fa_set = formset_factory(ZornaFileAddForm, extra=2)
                    form_set = fa_set(request.POST, request.FILES)
                    if form_set.is_valid():
                        upload_files = {}
                        ifiles = []
                        for i in range(0, form_set.total_form_count()):
                            form = form_set.forms[i]
                            try:
                                f = request.FILES['form-' + str(i) + '-file']
                                s = os.path.splitext(f.name)
                                fname = u"%s%s" % (slugify(s[0]), s[1])
                                upload_files[fname] = {'pk': '', 'file': f, 'description':
                                                       form.cleaned_data['description'], 'tags': form.cleaned_data['tags']}
                            except:
                                continue
                        path = u"%s/%s" % (
                            root_path, urllib.unquote(path_dest))
                        bupload = False
                        for f, info in upload_files.iteritems():
                            file = ZornaFile(owner=request.user, modifier=request.user, description=info[
                                             'description'], tags=info['tags'], folder=path_dest.split('/')[0])
                            file.save()
                            destination = open(u"%s/%s,%s" % (
                                path, file.pk, f), 'wb+')
                            bupload = True
                            for chunk in info['file'].chunks():
                                destination.write(chunk)
                            destination.close()
                            ifiles.append({'name': f, 'description': info[
                                          'description']})
                        if bupload:
                            ret['message'] = gettext(
                                u"Files uploaded successfully")
                            # notify users
                            bnotify = request.POST.get('notify_users', 0)
                            if folder and folder.email_notification and (folder.email_notification == 1 or bnotify):
                                notify_users(request, folder, ifiles, True)
                        else:
                            ret['message'] = gettext(u"No file uploaded")
                        ret['status'] = 'success'
                        json_data = simplejson.dumps(ret)
                        return HttpResponse('<textarea>' + json_data + '</textarea>')
                    else:
                        ret['message'] = gettext(u"Invalid form")
                        ret['status'] = 'error'
                        t = loader.get_template(
                            'fileman/upload_documents.html')
                        c = RequestContext(request, {'form_set':
                                           form_set, 'cdir_components': cdir_components, 'folder_dest': pathfile, 'folder_content_url': get_url_folder_content(pathfile)})
                        ret['data'] = t.render(c)
                        json_data = simplejson.dumps(ret)
                        return HttpResponse('<textarea>' + json_data + '</textarea>')
                else:
                    fa_set = formset_factory(ZornaFileAddForm, extra=2)
                    form_set = fa_set()
                    ret['status'] = 'success'

                t = loader.get_template('fileman/upload_documents.html')
                c = RequestContext(request, {'form_set': form_set,
                                             'cdir_components': cdir_components,
                                             'folder_dest': pathfile,
                                             'folder_content_url': get_url_folder_content(pathfile),
                                             'folder': folder,
                                             'manager': bmanager, })
                ret['data'] = t.render(c)
    else:
        ret['message'] = gettext(u"Invalid file path")
        ret['status'] = 'error'

    json_data = simplejson.dumps(ret)
    return HttpResponse(json_data)


def fm_multiple_upload_view(request):
    pathfile = request.REQUEST.get('dir', None)
    ret = {}
    if pathfile:
        ppath = urllib.unquote(pathfile.rstrip('/'))
        path_dest = clean_path(ppath)
        if path_dest != ppath:
            ret['message'] = gettext(u"Incorrect path")
            ret['status'] = 'error'
        else:
            buser, bmanager = get_user_access_to_path(request.user, path_dest)
            folder = get_shared_folder(path_dest)
            if bmanager is False:
                ret['message'] = gettext(u"Access denied")
                ret['status'] = 'error'
            else:
                cdir_components = get_path_components(path_dest)
                form = ZornaFileUploadForm(request.POST, request.FILES)
                if request.method == 'POST':
                    root_path = get_upload_library()
                    if form.is_valid():
                        path = u"%s/%s" % (
                            root_path, urllib.unquote(path_dest))
                        uploaded_file = request.FILES['file']
                        chunk = request.REQUEST.get('chunk', '0')
                        chunks = request.REQUEST.get('chunks', '0')
                        name = uploaded_file.name

                        temp_file = '%s/%s.part' % (path, name)
                        with open(temp_file, ('wb' if chunk == '0' else 'ab')) as f:
                            for content in uploaded_file.chunks():
                                f.write(content)

                        if int(chunk) + 1 >= int(chunks):
                            s = os.path.splitext(name)
                            file = ZornaFile(
                                owner=request.user, modifier=request.user, description='', folder=path_dest.split('/')[0])
                            file.save()
                            destination = u"%s/%s,%s" % (path, file.pk, name)
                            shutil.move(temp_file, destination)
                        if request.is_ajax():
                            response = HttpResponse(
                                '{"jsonrpc" : "2.0", "result" : null, "id" : "id"}')
                            response[
                                'Expires'] = 'Mon, 1 Jan 2000 01:00:00 GMT'
                            response[
                                'Cache-Control'] = 'no-store, no-cache, must-revalidate, post-check=0, pre-check=0'
                            response['Pragma'] = 'no-cache'
                            return response
                        else:
                            ret['status'] = 'success'
                else:
                    ret['status'] = 'success'
                t = loader.get_template(
                    'fileman/upload_multiple_documents.html')
                c = RequestContext(request, {
                                   'form': form,
                                   'cdir_components': cdir_components,
                                   'folder_dest': pathfile,
                                   'folder_content_url': get_url_folder_content(pathfile),
                                   'folder': folder,
                                   'language': settings.LANGUAGE_CODE[0:2]})
                ret['data'] = t.render(c)
    else:
        ret['message'] = gettext(u"Invalid file path")
        ret['status'] = 'error'

    json_data = simplejson.dumps(ret)
    return HttpResponse(json_data)


def fm_properties_view(request):
    pathfile = request.REQUEST.get('dir', None)
    file_name = request.REQUEST.get('file_name', None)
    file_id = request.REQUEST.get('file_id', 0)
    ret = {}
    if pathfile:
        ppath = urllib.unquote(pathfile.rstrip('/'))
        path_dest = clean_path(ppath)
        if path_dest != ppath:
            ret['message'] = gettext(u"Incorrect path")
            ret['status'] = 'error'
        else:
            buser, bmanager = get_user_access_to_path(request.user, path_dest)
            if bmanager is False:
                ret['message'] = gettext(u"Access denied")
                ret['status'] = 'error'
            else:
                folder = get_shared_folder(path_dest)
                try:
                    fo = ZornaFile.objects.get(pk=int(file_id))
                except:
                    ret['message'] = gettext(u"Access denied")
                    ret['status'] = 'error'
                else:
                    cdir_components = get_path_components(path_dest)
                    if request.method == 'POST':
                        root_path = get_upload_library()
                        form = ZornaFileForm(request.POST, instance=fo)
                        if form.is_valid():
                            fo = form.save(commit=False)
                            fo.modifier = request.user
                            fo.save()
                            if request.FILES:
                                f = request.FILES['file']
                                s = os.path.splitext(f.name)
                                fname = u"%s%s" % (slugify(s[0]), s[1])
                                os.remove(u"%s/%s/%s,%s" % (
                                    root_path, path_dest, file_id, file_name))
                                destination = open(u"%s/%s/%s,%s" % (
                                    root_path, path_dest, file_id, fname), 'wb+')
                                for chunk in f.chunks():
                                    destination.write(chunk)
                                destination.close()
                                # notify users
                                bnotify = request.POST.get('notify_users', 0)
                                if folder and folder.email_notification and (folder.email_notification == 1 or bnotify):
                                    notify_users(request, folder, [{
                                                 'name': fname, 'description': fo.description}], False)

                            ret['message'] = gettext(
                                u"File updated successfully")
                            ret['status'] = 'success'
                            json_data = simplejson.dumps(ret)
                            return HttpResponse('<textarea>' + json_data + '</textarea>')
                        else:
                            ret['message'] = gettext(u"Invalid form")
                            ret['status'] = 'error'
                    else:
                        form = ZornaFileForm(instance=fo)
                        ret['status'] = 'success'

                    t = loader.get_template('fileman/file_properties.html')
                    ec = {
                        'file': fo, 'form': form, 'file_path': pathfile, 'file_name': file_name,
                        'file_id': file_id, 'cdir_components': cdir_components,
                        'folder_content_url': get_url_folder_content(pathfile), 'folder': folder}
                    c = RequestContext(request, ec)
                    ret['data'] = t.render(c)
    else:
        ret['message'] = gettext(u"Invalid file path")
        ret['status'] = 'error'

    json_data = simplejson.dumps(ret)
    return HttpResponse(json_data)


def add_files(root_folder, user, path):
    ret = []
    for f in os.listdir(path):
        p = os.path.join(path, f)
        if os.path.isdir(p):
            ret.extend(add_files(root_folder, user, p))
            continue

        pk, fname = split_file_name(f)
        if pk is False:
            try:
                file = ZornaFile(
                    description='', owner=user, modifier=user, folder=root_folder)
                file.save()
                dest = os.path.join(path, u"%s,%s" % (file.pk, f))
                os.rename(p, dest)
                ret.append(p)
            except:
                continue
    return ret


@login_required()
def add_files_to_database(request):
    if request.user.is_superuser:
        ret = []
        root_path = get_upload_library()
        for f in os.listdir(root_path):
            path = os.path.join(root_path, f)
            user = request.user
            try:
                if f[0] in ['U', 'C', 'F'] and int(f[1:]) and os.path.isdir(path):
                    if f[0] == 'U':
                        try:
                            user = User.objects.get(pk=int(f[1:]))
                        except:
                            pass
                    ret.extend(add_files(f, user, path))
            except Exception as e:
                continue
        extra_context = {'files': ret, }
        context = RequestContext(request)
        return render_to_response('fileman/log_files.html', extra_context, context_instance=context)
    else:
        return HttpResponseForbidden()
