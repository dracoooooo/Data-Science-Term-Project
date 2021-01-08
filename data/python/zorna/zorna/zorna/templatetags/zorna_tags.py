import os
try:
    from PIL import Image
except ImportError:
    import Image
from datetime import date
from django.template import TemplateSyntaxError
from django import template
from django.template import Variable
from django.utils.translation import ugettext_lazy as _
from django.core.urlresolvers import reverse
import feedparser
import re
import dateutil.parser

from zorna.acl.models import get_allowed_objects
from zorna.communities.models import Community
from zorna.articles.models import ArticleCategory
from zorna.faq.models import Faq
from zorna.forms.forms import FormsWorkspace
from zorna.fileman.models import ZornaFolder
from zorna.site.models import SiteOptions
from zorna.calendars.api import get_user_calendars
from zorna.utilit import resize_image

register = template.Library()


class natural_date(template.Node):

    def __init__(self, value, var_name):
        self.var_name = var_name
        self.value = Variable(value)

    def render(self, context):
        """
        For date values that are tomorrow, today or yesterday compared to
        present day returns representing string. Otherwise, returns an empty string.
        """
        value = self.value.resolve(context)
        try:
            value = date(value.year, value.month, value.day)
        except AttributeError:
            # Passed value wasn't a date object
            ret = ''
        except ValueError:
            # Date arguments out of range
            ret = ''
        delta = value - date.today()
        if delta.days == 0:
            ret = 'today'
        elif delta.days == 1:
            ret = 'tomorrow'
        elif delta.days == -1:
            ret = 'yesterday'
        else:
            ret = ''
        context[self.var_name] = ret
        return ''


@register.tag(name="get_natural_date")
def get_natural_date(parser, token):
    bits = token.split_contents()
    if 4 != len(bits):
        raise TemplateSyntaxError('%r expects 3 arguments' % bits[0])
    if bits[-2] != 'as':
        raise TemplateSyntaxError(
            '%r expects "as" as the second argument' % bits[0])
    varname = bits[-1]
    value = bits[-3]
    return natural_date(value, varname)


class user_menus(template.Node):

    def __init__(self, var_name):
        self.var_name = var_name

    def render(self, context):
        request = context['request']
        ret = []
        if get_allowed_objects(request.user, Community, 'member') or get_allowed_objects(request.user, Community, 'manage'):
            bcom_access1 = True
        else:
            bcom_access1 = False
            bcom_access2 = Community.objects.filter(status__in=[0, 1]).count()
        if bcom_access1 or bcom_access2:
            p = {'url': reverse('list_communities'), 'text': _(u'Communities')}
            ret.append(p)
        bpersonal_documents = SiteOptions.objects.is_access_valid(
            request.user, 'zorna_personal_documents')
        if bpersonal_documents or bcom_access1:
            p = {'url': reverse('documents'), 'text': _(u'Documents')}
            ret.append(p)
        else:
            ao = get_allowed_objects(
                request.user, ZornaFolder, ['writer', 'reader', 'manager'])
            if ao:
                p = {'url': reverse('documents'), 'text': _(u'Documents')}
                ret.append(p)

        ao = get_allowed_objects(request.user, ArticleCategory, 'writer')
        if ao:
            p = {
                'url': reverse('writer_stories_list'), 'text': _(u'Publication')}
            ret.append(p)

        b_pages_manager = SiteOptions.objects.is_access_valid(
            request.user, 'zorna_pages_pages')
        b_templates_manager = SiteOptions.objects.is_access_valid(
            request.user, 'zorna_pages_templates')

        if b_pages_manager or b_templates_manager:
            p = {'url': reverse('fmbrowser_home'), 'text': _(u'Pages')}
            ret.append(p)

        b_menus_manager = SiteOptions.objects.is_access_valid(
            request.user, 'zorna_menus')

        if b_menus_manager:
            p = {'url': reverse('menus_home_view'), 'text': _(u'Menus')}
            ret.append(p)

        cals = get_user_calendars(
            request.user, ['viewer', 'manager', 'creator'])
        if cals:
            p = {'url': reverse('view_calendar'), 'text': _(u'Calendar')}
            ret.append(p)
        ao = get_allowed_objects(request.user, Faq, ['manager', 'reader'])
        if ao:
            p = {'url': reverse('manager_list_faqs'), 'text': _(u'Faqs')}
            ret.append(p)
        ao = get_allowed_objects(request.user, FormsWorkspace, 'manager')
        if ao:
            fw = FormsWorkspace.objects.get(pk=ao[0])
            p = {
                'url': reverse('forms_home', args=[fw.slug]), 'text': _(u'Forms')}
            ret.append(p)
        b_notes = SiteOptions.objects.is_access_valid(
            request.user, 'zorna_personal_notes')
        if b_notes:
            p = {'url': reverse('notes_home_view'), 'text': _(u'Notes')}
            ret.append(p)
        p = {'url': reverse('user_settings'), 'text': _(u'Settings')}
        ret.append(p)

        context[self.var_name] = ret
        return ''


@register.tag(name="get_user_menus")
def get_user_menus(parser, token):
    bits = token.split_contents()
    if 3 != len(bits):
        raise TemplateSyntaxError('%r expects 3 arguments' % bits[0])
    if bits[-2] != 'as':
        raise TemplateSyntaxError(
            '%r expects "as" as the second argument' % bits[0])
    varname = bits[-1]
    return user_menus(varname)


class admin_menus(template.Node):

    def __init__(self, var_name):
        self.var_name = var_name

    def render(self, context):
        request = context['request']
        ret = []
        bvalidator = SiteOptions.objects.is_access_valid(
            request.user, 'zorna_validate_registration')
        if request.user.is_superuser:
            ret.append(
                {'url': reverse('admin_list_categories'), 'text': _(u'Articles')})
            ret.append(
                {'url': reverse('admin_list_communities'), 'text': _(u'Communities')})
            ret.append(
                {'url': reverse('admin_list_calendars'), 'text': _(u'Calendars')})
            ret.append({'url': reverse('list_faqs'), 'text': _(u'Faqs')})
            ret.append(
                {'url': reverse('admin_list_folders'), 'text': _(u'Folders')})
            ret.append(
                {'url': reverse('list_workspaces'), 'text': _(u'Forms')})
            ret.append(
                {'url': reverse('admin_list_alerts'), 'text': _(u'Site')})
            ret.append({'url': reverse('list_groups'), 'text': _(u'Groups')})
        if request.user.is_superuser or bvalidator:
            ret.append({'url': reverse('list_users'), 'text': _(u'Users')})

        context[self.var_name] = ret
        return ''


@register.tag(name="get_administrator_menus")
def get_administrator_menus(parser, token):
    bits = token.split_contents()
    if 3 != len(bits):
        raise TemplateSyntaxError('%r expects 3 arguments' % bits[0])
    if bits[-2] != 'as':
        raise TemplateSyntaxError(
            '%r expects "as" as the second argument' % bits[0])
    varname = bits[-1]
    return admin_menus(varname)


# http://djangosnippets.org/snippets/852/
class RssParserNode(template.Node):

    def __init__(self, var_name, url=None, url_var_name=None):
        self.url = url
        self.url_var_name = url_var_name
        self.var_name = var_name

    def render(self, context):
        if self.url:
            context[self.var_name] = feedparser.parse(self.url)
        else:
            try:
                context[self.var_name] = feedparser.parse(
                    context[self.url_var_name])
            except KeyError:
                raise template.TemplateSyntaxError, "the variable \"%s\" can't be found in the context" % self.url_var_name
        for entry in context[self.var_name]['entries']:
            date_published = entry.get('published', entry.get('updated'))
            date_published = dateutil.parser.parse(date_published)
            entry['date_published'] = date_published
        return ''


@register.tag(name="get_rss")
def get_rss(parser, token):
    # This version uses a regular expression to parse tag contents.
    try:
        # Splitting by None == splitting by spaces.
        tag_name, arg = token.contents.split(None, 1)
    except ValueError:
        raise template.TemplateSyntaxError, "%r tag requires arguments" % token.contents.split()[
            0]

    m = re.search(r'(.*?) as (\w+)', arg)
    if not m:
        raise template.TemplateSyntaxError, "%r tag had invalid arguments" % tag_name
    url, var_name = m.groups()

    if url[0] == url[-1] and url[0] in ('"', "'"):
        return RssParserNode(var_name, url=url[1:-1])
    else:
        return RssParserNode(var_name, url_var_name=url)

"""
example usage:

{% load cache %}
{% load rss %}

{% cache 500 rss_display %}
    {% get_rss "http://www.freesound.org/blog/?feed=rss2" as rss %}
    {% for entry in rss.entries %}
        <h1>{{entry.title}}</h1>
        <p>
            {{entry.summary|safe}}
        </p>
        <p>
            <a href="{{entry.link}}">read more...</a>
        </p>
    {% endfor %}
{% endcache %}
"""


@register.filter
def imageresize(file, size=None):
    miniature = resize_image(file.path, size)
    split = file.url.rsplit('/', 1)
    return '%s/%s' % (split[0], miniature)
