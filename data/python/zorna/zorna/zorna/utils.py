# Create your views here.
import os
import re
import posixpath
import urllib
from django.conf import settings
from django.utils.translation import ugettext_lazy as _
from django.http import HttpResponse, HttpResponseRedirect
from django.template import loader, Template
from django.template import RequestContext
from django.contrib.auth.models import User
from django.shortcuts import get_object_or_404
from django.contrib.auth import SESSION_KEY
from django import http
from django.contrib.auth.decorators import user_passes_test

from zorna.articles.models import ArticleCategory
from zorna.articles.views import view_category, view_category_archive


@user_passes_test(lambda u: u.is_superuser)
def su(request, username, redirect_url='/'):
    su_user = get_object_or_404(User, username=username, is_active=True)
    request.session[SESSION_KEY] = su_user.id
    return http.HttpResponseRedirect(redirect_url)


def get_context_text(pathfile):
    """
    Parse file an return context ( yaml ) and text.
    Context is between "{% zorna" tag and "%}" tag
    """
    start = re.compile(r'.*?{%\s*zorna\s+(.*?)(%}|$)')
    end = re.compile(r'(.*?)(%})')
    try:
        fin = open(pathfile, 'r')
    except IOError:
        return '', ''
    started = False
    context = ''
    text = ''
    matcher = start
    for line in fin:
        match = matcher.match(line)
        if match:
            context = context + match.group(1)
            if started:
                break
            else:
                matcher = end
                started = True
        elif started:
            context = context + line
        else:
            text = text + line
    for line in fin:
        text = text + line
    # tag_re = re.compile(r'(%s\s*block(.*?)\s*%s)(.*?)(%s\s*endblock.*?\s*%s)' % (re.escape('{%'), re.escape('%}'),re.escape('{%'), re.escape('%}')), re.M|re.DOTALL)
    # tag_re2 = re.compile(r'(%s\s*zorna)(.*?)(\s*%s)' % (re.escape('{%'), re.escape('%}')), re.M|re.DOTALL)
    # for match in tag_re.finditer(text):
    #    g = match.groups()
    return context, text


def render_page(request, path):
    path = posixpath.normpath(urllib.unquote(path))
    path = path.lstrip('/')
    newpath = ''
    for part in path.split('/'):
        if not part:
            # Strip empty path components.
            continue
        if part in (os.curdir, os.pardir):
            # Strip '.' and '..' in path.
            continue
        newpath = os.path.join(newpath, part).replace('\\', '/')

    if newpath == '':
        return ''
    else:
        if newpath[-1:] == "/":
            newpath = newpath[0:-1]
        folders = newpath.split('/')
        filename = folders.pop() + ".html"
        newpath = "/".join(folders)

    if newpath:
        pathfile = newpath + '/' + filename
    else:
        pathfile = filename
    context, text = get_context_text(os.path.join(
        settings.PROJECT_PATH, settings.ZORNA_CONTENT, pathfile))
    if context:
        import yaml
        context = yaml.load(context)
        context['zorna_title_page'] = context['title']
        if 'keywords' in context:
            context['zorna_keywords_page'] = context['keywords']
        else:
            context['zorna_keywords_page'] = ''
        if 'description' in context:
            context['zorna_description_page'] = context['description']
        else:
            context['zorna_description_page'] = ''

        try:
            context['author'] = User.objects.get(pk=int(context['author']))
        except:
            pass
    else:
        context = {}
    try:
        t = Template(text)
        c = RequestContext(request, context)
        return t.render(c)
    except Exception as e:
        import sys
        print >> sys.stderr, '%s' % e
        return ''


def view_content(request, path):
    page = render_page(request, path)
    if page:
        return HttpResponse(page)
    else:
        return HttpResponseRedirect('/')


def load_home_page(request):
    if request.user.is_anonymous():
        url = getattr(settings, 'ZORNA_PUBLIC_REDIRECT_URL', None)
        if url:
            return HttpResponseRedirect(url)
        t = loader.get_template('public.html')
        title = _(u'Intranet')
    else:
        url = getattr(settings, 'ZORNA_PRIVATE_REDIRECT_URL', None)
        if url:
            return HttpResponseRedirect(url)
        t = loader.get_template('private.html')
        title = _(u'Intranet')

    c = RequestContext(request, {'zorna_title_page': title})
    return HttpResponse(t.render(c))


def zorna_category(request, path=None):
    obj = ArticleCategory.objects.from_path(path)
    if obj is None:
        return load_home_page(request)  # raise Http404
    return view_category(request, obj)


def zorna_stories_archives(request, path, year, month):
    try:
        obj = ArticleCategory.objects.from_path(path)
        return view_category_archive(request, obj, year, month)
    except:
        return HttpResponseRedirect('/')
