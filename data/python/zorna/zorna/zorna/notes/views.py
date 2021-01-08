import shutil
import os
from django.utils.translation import gettext
from django.http import HttpResponse
from django.http import HttpResponseForbidden
from django.shortcuts import render_to_response
from django.template import RequestContext, loader
from django.utils import simplejson
from django.forms.formsets import formset_factory
from django.core.urlresolvers import reverse
from django.db.models import Q
from django.template.defaultfilters import slugify

from tagging.models import Tag

from zorna.notes.models import ZornaNoteCategory, ZornaNote, ZornaNoteFile
from zorna.notes.forms import ZornaNoteForm, ZornaNoteFileForm
from zorna.acl.models import get_acl_for_model
from zorna.acl.models import get_allowed_objects
from zorna.utilit import get_upload_notes_attachments
from zorna.site.models import SiteOptions


def notes_home_view(request):
    if request.user.is_authenticated:
        b_notes = SiteOptions.objects.is_access_valid(
            request.user, 'zorna_personal_notes')
        if not b_notes:
            return HttpResponseForbidden()
        extra_context = {}
        extra_context['file_type'] = request.GET.get('file_type', 'file')
        extra_context['dir'] = request.GET.get('dir', '')
        context = RequestContext(request)
        return render_to_response('notes/notes_home.html', extra_context, context_instance=context)
    else:
        return HttpResponseForbidden()


def dirlist_categories(request):
    r = ['<ul class="jqueryFileTree" style="display: none;">']
    category = request.GET.get('dir', '')
    try:
        category = category.rstrip('/')
        c = category.split('/')
        category = ZornaNoteCategory.objects.get(
            pk=int(c[-1]), owner=request.user)
    except:
        category = None

    if category:
        ob_list = ZornaNoteCategory.objects.filter(
            owner=request.user, parent=category)
        pcat = "%s/" % category.pk
    else:
        ob_list = ZornaNoteCategory.objects.filter(
            owner=request.user, parent__isnull=True)
        pcat = ''

    for cat in ob_list:
        r.append('<li class="directory collapsed"><a href="#" id="%s" rel="%s%s/">%s</a></li>' %
                 (cat.pk, pcat, cat.pk, cat.name))
    r.append('</ul>')
    return HttpResponse(''.join(r))


def dirlist_shared(request):
    r = ['<ul class="jqueryFileTree" style="display: none;">']
    category = request.GET.get('dir', '')
    ob = get_allowed_objects(request.user, ZornaNoteCategory, 'viewer')
    try:
        category = category.rstrip('/')
        c = category.split('/')
        category = ZornaNoteCategory.objects.get(pk=int(c[-1]))
    except:
        category = None

    if category:
        ob_list = ZornaNoteCategory.objects.filter(pk__in=ob, parent=category)
        pcat = "%s/" % category.pk
    else:
        ob_list = ZornaNoteCategory.objects.filter(
            pk__in=ob, parent__isnull=True)
        pcat = ''

    for cat in ob_list:
        r.append('<li class="directory collapsed"><a href="#" id="%s" rel="%s%s/">%s</a></li>' %
                 (cat.pk, pcat, cat.pk, cat.name))
    r.append('</ul>')
    return HttpResponse(''.join(r))


def notes_get_content(request, extra={}, category=None):
    if request.user.is_authenticated():
        extra_context = dict(**extra)
        extra_context['category'] = category
        extra_context['search_text'] = request.GET.get('search_text', '')
        extra_context['search_tag'] = request.GET.get('search_tag', '')
        if category:
            try:
                category = ZornaNoteCategory.objects.get(pk=category)
                check = get_acl_for_model(ZornaNoteCategory)
                extra_context['owner'] = category.owner == request.user
                if extra_context['owner'] or check.viewer_zornanotecategory(category, request.user):
                    extra_context['category'] = category
                    extra_context[
                        'category_ancestors'] = category.get_ancestors()
                    notes = category.zornanote_set.all()
                    if extra_context['search_text']:
                        notes = notes.filter(Q(title__icontains=extra_context[
                                             'search_text']) | Q(content__icontains=extra_context['search_text']))
                    if extra_context['search_tag']:
                        notes = notes.filter(Q(
                            tags__icontains=extra_context['search_tag']))
                    for n in notes:
                        n.attachments = []
                        for f in n.zornanotefile_set.all():
                            n.attachments.append({'file_name': os.path.basename(
                                f.file.name), 'pk': f.pk})
                    extra_context['notes'] = notes
            except Exception as e:
                return '%s' % e
        else:
            ob = get_allowed_objects(request.user, ZornaNoteCategory, 'viewer')
            extra_context['owner'] = False
            extra_context['category'] = None
            extra_context['category_ancestors'] = []
            notes = ZornaNote.objects.filter(Q(
                owner=request.user) | Q(category__in=ob))
            if extra_context['search_text']:
                notes = notes.filter(Q(title__icontains=extra_context[
                                     'search_text']) | Q(content__icontains=extra_context['search_text']))
            if extra_context['search_tag']:
                notes = notes.filter(Q(
                    tags__icontains=extra_context['search_tag']))
            for n in notes:
                n.attachments = []
                for f in n.zornanotefile_set.all():
                    n.attachments.append({'file_name': os.path.basename(
                        f.file.name), 'pk': f.pk})
            extra_context['notes'] = notes

        t = loader.get_template('notes/notes_view_notes.html')
        c = RequestContext(request, extra_context)
        return t.render(c)
    else:
        return 'Access denied'


def dirlist_category(request):
    if request.user.is_authenticated:
        category = request.GET.get('dir', '')
        try:
            category = category.rstrip('/')
            c = category.split('/')
            category = int(c[-1])
        except:
            category = None
        return HttpResponse(notes_get_content(request, {}, category))
    else:
        return HttpResponse('')


def notes_add_category(request):
    ret = {}
    if request.user.is_authenticated():
        parent = request.POST.get("category", None)
        new_category = request.POST.get("new_category", '').strip()
        if new_category:
            try:
                parent = ZornaNoteCategory.objects.get(
                    pk=parent, owner=request.user)
            except:
                parent = None
            category = ZornaNoteCategory(name=new_category, slug=slugify(
                new_category), owner=request.user)
            if parent:
                category.parent = parent
            category.save()
            ret['status'] = 'success'
            ret['message'] = gettext(u'Category created successfully')
            parents = [str(c.pk) for c in category.get_ancestors()]
            rel = '/'.join(parents) + '/' + str(category.pk) + '/'
            ret['url'] = '<li class="directory collapsed"><a href="#" id="%s" rel="%s">%s</a></li>' % (
                category.pk, rel, new_category)
        else:
            ret['status'] = 'error'
            ret['message'] = gettext(u'You must give a valid name')
    else:
        ret['status'] = 'error'
        ret['message'] = gettext(u'Access denied')
    return HttpResponse(simplejson.dumps(ret))


def notes_rename_category(request):
    ret = {}
    if request.user.is_authenticated():
        category = request.POST.get("category", 0)
        new_category = request.POST.get("new_category", '').strip()
        try:
            category = ZornaNoteCategory.objects.get(
                pk=category, owner=request.user)
        except:
            category = None
        if new_category and category:
            category.name = new_category
            category.slug = slugify(new_category)
            category.modifier = request.user
            category.save()
            ret['status'] = 'success'
            ret['message'] = gettext(u'Category renamed successfully')
        else:
            ret['status'] = 'error'
            ret['message'] = gettext(u'You must give a valid name')
    else:
        ret['status'] = 'error'
        ret['message'] = gettext(u'Access denied')
    return HttpResponse(simplejson.dumps(ret))


def notes_add_note(request):
    ret = {}
    if request.user.is_authenticated():
        category = request.REQUEST.get("category", 0)
        try:
            category = ZornaNoteCategory.objects.get(
                pk=category, owner=request.user)
        except:
            ret['status'] = 'error'
            ret['message'] = gettext(u'Invalid category note')
            return HttpResponse(simplejson.dumps(ret))

        if request.method == 'POST':
            fa_set = formset_factory(ZornaNoteFileForm, extra=2)
            form_attachments_set = fa_set(request.POST, request.FILES)
            form_note = ZornaNoteForm(
                request, request.POST, initial={'category': category})
            if form_note.is_valid() and form_attachments_set.is_valid():
                note = form_note.save(commit=False)
                note.owner = request.user
                note.save()
                bupload = False
                for i in range(0, form_attachments_set.total_form_count()):
                    form = form_attachments_set.forms[i]
                    try:
                        note_file = request.FILES['form-' + str(i) + '-file']
                        attachment = ZornaNoteFile(description=form.cleaned_data[
                                                   'description'], mimetype=note_file.content_type)
                        attachment.note = note
                        attachment.save()
                        attachment.file.save(note_file.name, note_file)
                    except:
                        pass
                ret['status'] = 'success'
                ret['message'] = gettext(u'Note created successfully')
                ret['data'] = notes_get_content(request, {}, category.pk)
                return HttpResponse('<textarea>' + simplejson.dumps(ret) + '</textarea>')
            else:
                ret['status'] = 'error'
                ret['message'] = gettext(u'Invalid form')
                extra_context = {"form_note":
                                 form_note, 'form_attachments': form_attachments_set}
                extra_context['category'] = category
                extra_context['category_ancestors'] = category.get_ancestors()
                extra_context['url_action'] = reverse('notes_add_note')
                t = loader.get_template('notes/notes_edit_note.html')
                c = RequestContext(request, extra_context)
                ret['data'] = t.render(c)
                return HttpResponse('<textarea>' + simplejson.dumps(ret) + '</textarea>')
        else:
            fa_set = formset_factory(ZornaNoteFileForm, extra=2)
            form_attachments_set = fa_set()
            form_note = ZornaNoteForm(request, initial={'category': category})
            ret['status'] = 'success'

        extra_context = {"form_note":
                         form_note, 'form_attachments': form_attachments_set}
        extra_context['category'] = category
        extra_context['category_ancestors'] = category.get_ancestors()
        extra_context['url_action'] = reverse('notes_add_note')
        t = loader.get_template('notes/notes_edit_note.html')
        c = RequestContext(request, extra_context)
        ret['data'] = t.render(c)
    else:
        ret['status'] = 'error'
        ret['message'] = gettext(u'Access denied')
    return HttpResponse(simplejson.dumps(ret))


def notes_edit_note(request):
    ret = {}
    if request.user.is_authenticated():
        note = request.REQUEST.get("note", 0)
        try:
            note = ZornaNote.objects.get(pk=note, owner=request.user)
        except:
            ret['status'] = 'error'
            ret['message'] = gettext(u'Invalid note')
            return HttpResponse(simplejson.dumps(ret))

        attachments = note.zornanotefile_set.all()
        nbinitialfiles = len(attachments)
        if request.method == 'POST':
            if request.POST.has_key('bdelnote'):
                category = note.category.pk
                try:
                    shutil.rmtree(u"%s/u%s" % (
                        get_upload_notes_attachments(), note.pk))
                except:
                    pass
                note.zornanotefile_set.all().delete()
                Tag.objects.update_tags(note, None)
                note.delete()
                ret['status'] = 'success'
                ret['message'] = gettext(u'Note deleted successfully')
                ret['data'] = notes_get_content(request, {}, category)
                return HttpResponse(simplejson.dumps(ret))

            fa_set = formset_factory(
                ZornaNoteFileForm, extra=2 - nbinitialfiles)
            form_attachments_set = fa_set(request.POST, request.FILES)
            form_note = ZornaNoteForm(request, request.POST, instance=note)
            if form_note.is_valid() and form_attachments_set.is_valid():
                if nbinitialfiles and request.POST.has_key('selected_attachments'):
                    att = request.POST.getlist('selected_attachments')
                    for f in att:
                        ZornaNoteFile.objects.get(pk=f).delete()

                note = form_note.save(commit=False)
                note.owner = request.user
                note.save()
                bupload = False
                for i in range(0, form_attachments_set.total_form_count()):
                    form = form_attachments_set.forms[i]
                    try:
                        note_file = request.FILES['form-' + str(i) + '-file']
                        attachment = ZornaNoteFile(description=form.cleaned_data[
                                                   'description'], mimetype=note_file.content_type)
                        attachment.note = note
                        attachment.save()
                        attachment.file.save(note_file.name, note_file)
                    except:
                        pass
                ret['status'] = 'success'
                ret['message'] = gettext(u'Note updated successfully')
                ret['data'] = notes_get_content(request, {}, note.category.pk)
                return HttpResponse('<textarea>' + simplejson.dumps(ret) + '</textarea>')
            else:
                extra_context = {"form_note": form_note, 'form_attachments':
                                 form_attachments_set, 'attachments': attachments, }
                extra_context['category'] = note.category
                extra_context[
                    'category_ancestors'] = note.category.get_ancestors()
                extra_context['url_action'] = reverse(
                    'notes_edit_note') + '?note=' + str(note.pk)
                ret['status'] = 'error'
                t = loader.get_template('notes/notes_edit_note.html')
                c = RequestContext(request, extra_context)
                ret['data'] = t.render(c)
                return HttpResponse('<textarea>' + simplejson.dumps(ret) + '</textarea>')
        else:
            fa_set = formset_factory(
                ZornaNoteFileForm, extra=2 - nbinitialfiles)
            form_attachments_set = fa_set()
            form_note = ZornaNoteForm(
                request, instance=note, initial={'tags': note.tags})

        extra_context = {"form_note": form_note, 'form_attachments':
                         form_attachments_set, 'attachments': attachments, }
        extra_context['category'] = note.category
        extra_context['category_ancestors'] = note.category.get_ancestors()
        extra_context['url_action'] = reverse(
            'notes_edit_note') + '?note=' + str(note.pk)
        t = loader.get_template('notes/notes_edit_note.html')
        c = RequestContext(request, extra_context)
        ret['data'] = t.render(c)
        ret['status'] = 'success'
    else:
        ret['status'] = 'error'
        ret['message'] = gettext(u'Access denied')
    return HttpResponse(simplejson.dumps(ret))


def get_note_attachment(request, file_id):
    if request.user.is_authenticated():
        try:
            note_file = ZornaNoteFile.objects.get(pk=file_id)
            note = note_file.note
            if note.owner != request.user:
                check = get_acl_for_model(ZornaNoteCategory)
                if not check.viewer_zornanotecategory(note.category, request.user):
                    return HttpResponseForbidden()
        except:
            return HttpResponseForbidden()

        path = "%s/%s" % (get_upload_notes_attachments(), note_file.file.name)
        fp = open(path, 'rb')
        content_type = note_file.mimetype
        response = HttpResponse(fp.read(), content_type=content_type)
        response['Content-Length'] = os.path.getsize(path)
        response['Content-Disposition'] = "attachment; filename=%s" % os.path.basename(
            note_file.file.name)
        return response
    else:
        return HttpResponseForbidden()


def notes_share_category(request, category):
    if request.user.is_authenticated():
        category = ZornaNoteCategory.objects.get(
            pk=category, owner=request.user)
        check = get_acl_for_model(category)
        return check.get_acl_users_forms(request, category.pk)
    else:
        return HttpResponseForbidden()
