import os
from django import template
from django.contrib.contenttypes.models import ContentType
from django.contrib.auth.models import User
from django.utils import simplejson
from django.template import TemplateSyntaxError
from django.template import Variable

from zorna.communities.models import Community, MessageCommunity, EventCommunity, PageCommunity
from zorna.acl.models import ACLPermission
from zorna import defines
from zorna.utilit import get_upload_path, get_upload_communities
from zorna.acl.models import get_acl_for_model, get_allowed_objects, get_acl_by_object
from zorna.communities.views import get_all_messages, get_messages_extra_by_content_type

register = template.Library()


def auto_completion_search_members(context, input_suggest):
    """
    Render an auto completion to search users.

    Override ``account/ac_search_users.html`` if you want to change the
    design.
    """
    request = context['request']
    input_suggest = input_suggest

    data = []
    ao = set([])
    ao_member = get_allowed_objects(request.user, Community, 'member')
    ao = ao.union(set(ao_member))
    ao_manage = get_allowed_objects(request.user, Community, 'manage')
    ao = ao.union(set(ao_manage))

    communities = Community.objects.filter(id__in=ao).order_by('name')
    objects = set([])
    for com in communities:
        objects = objects.union(set(get_acl_by_object(com, 'member')))
        objects = objects.union(set(get_acl_by_object(com, 'manage')))
        data.append([com.name, ("g-%s" % str(com.id))])

    data.extend([("%s %s" % (x.last_name, x.first_name), ("u-%s" % str(x.id)))
                for x in objects])
    members_data = simplejson.dumps(data)
    return locals()

auto_completion_search_members = register.inclusion_tag(
    'communities/ac_search_users.html',
    takes_context=True
)(auto_completion_search_members)


class user_communities(template.Node):

    def __init__(self, var_name):
        self.var_name = var_name

    def render(self, context):
        request = context['request']
        check = get_acl_for_model(Community)
        allowed_objects = get_allowed_objects(
            request.user, Community, 'member')
        communities = Community.objects.filter(
            id__in=allowed_objects).order_by('name')
        for com in communities:
            com.manager = check.manage_community(com, request.user)
            com.member = True
        context[self.var_name] = communities
        return ''


@register.tag(name="get_user_communities")
def get_user_communities(parser, token):
    bits = token.split_contents()
    if 3 != len(bits):
        raise TemplateSyntaxError('%r expects 3 arguments' % bits[0])
    if bits[-2] != 'as':
        raise TemplateSyntaxError(
            '%r expects "as" as the second argument' % bits[0])
    varname = bits[-1]
    return user_communities(varname)


class community_messages(template.Node):

    def __init__(self, community, var_name):
        self.var_name = var_name
        self.community = community

    def render(self, context):
        messages = MessageCommunity.objects.select_related().filter(
            communities__exact=self.community)
        context[self.var_name] = messages
        return ''


@register.tag(name="get_community_messages")
def get_community_messages(parser, token):
    """
    
    """
    bits = token.split_contents()
    if 4 != len(bits):
        raise TemplateSyntaxError('%r expects 4 arguments' % bits[0])
    if bits[-2] != 'as':
        raise TemplateSyntaxError(
            '%r expects "as" as the second argument' % bits[0])
    community = bits[1]
    varname = bits[-1]
    return community_messages(community, varname)


class attachments_message(template.Node):

    def __init__(self, message, var_name):
        self.var_name = var_name
        self.message = message

    def render(self, context):
        path = "%s/%s" % (get_upload_path(), self.message)
        try:
            context[self.var_name] = [f for f in os.listdir(path)]
        except:
            context[self.var_name] = []
        return ''


@register.tag(name="get_attachments_message")
def get_attachments_message(parser, token):
    bits = token.split_contents()
    if 4 != len(bits):
        raise TemplateSyntaxError('%r expects 4 arguments' % bits[0])
    if bits[-2] != 'as':
        raise TemplateSyntaxError(
            '%r expects "as" as the second argument' % bits[0])
    message = bits[1]
    varname = bits[-1]
    return attachments_message(message, varname)


class last_communities_attachments(template.Node):

    def __init__(self, community, number, var_name):
        self.community = Variable(community)
        self.var_name = var_name
        self.number = number

    def render(self, context):
        request = context['request']
        community = self.community.resolve(context)
        messages = get_all_messages(request, community.pk if community else 0)
        messages = messages.order_by('-time_updated')

        context[self.var_name] = []
        nbfiles = self.number
        for msg in messages:
            if nbfiles == 0:
                break
            path = "%s/%s" % (get_upload_communities(), msg.pk)
            try:
                files = []
                for f in os.listdir(path):
                    files.append(f)
                    nbfiles -= 1
                    if nbfiles == 0:
                        break
                context[self.var_name].append({'message': msg, 'files': files})
            except:
                pass
        return ''


@register.tag(name="get_last_communities_attachments")
def get_last_communities_attachments(parser, token):
    """
    Return the the nb_files last attachments for a community
    {% get_last_communities_attachments community nb_files as attachments %}
    """
    bits = token.split_contents()
    if 5 != len(bits):
        raise TemplateSyntaxError('%r expects 5 arguments' % bits[0])
    if bits[-2] != 'as':
        raise TemplateSyntaxError(
            '%r expects "as" as the second argument' % bits[0])
    community = bits[1]
    number = int(bits[2])
    varname = bits[-1]
    return last_communities_attachments(community, number, varname)


class last_communities_events(template.Node):

    def __init__(self, community, number, var_name):
        self.community = Variable(community)
        self.var_name = var_name
        self.number = number

    def render(self, context):
        request = context['request']
        community = self.community.resolve(context)
        ct = ContentType.objects.get_for_model(EventCommunity)
        messages = get_messages_extra_by_content_type(
            request, ct, community.pk if community else 0)
        messages = messages.order_by('-message__time_updated')
        extra = {}
        for m in messages:
            extra[m.object_id] = m

        m = EventCommunity.objects.filter(
            pk__in=extra.keys()).order_by('-event__start')[0:self.number]
        for c in m:
            c.message = extra[c.pk].message

        context[self.var_name] = m
        return ''


@register.tag(name="get_last_communities_events")
def get_last_communities_events(parser, token):
    """
    Return the nb last events for a community
    {% get_last_communities_events community nb as attachments %}
    """
    bits = token.split_contents()
    if 5 != len(bits):
        raise TemplateSyntaxError('%r expects 5 arguments' % bits[0])
    if bits[-2] != 'as':
        raise TemplateSyntaxError(
            '%r expects "as" as the second argument' % bits[0])
    community = bits[1]
    number = int(bits[2])
    varname = bits[-1]
    return last_communities_events(community, number, varname)


class last_communities_pages(template.Node):

    def __init__(self, community, number, var_name):
        self.community = Variable(community)
        self.var_name = var_name
        self.number = number

    def render(self, context):
        request = context['request']
        community = self.community.resolve(context)
        ct = ContentType.objects.get_for_model(PageCommunity)
        messages = get_messages_extra_by_content_type(
            request, ct, community.pk if community else 0)
        messages = messages.order_by('-message__time_updated')
        extra = {}
        for m in messages:
            extra[m.object_id] = m

        m = PageCommunity.objects.filter(
            pk__in=extra.keys())[0:self.number]
        for c in m:
            c.message = extra[c.pk].message

        context[self.var_name] = m
        return ''


@register.tag(name="get_last_communities_pages")
def get_last_communities_pages(parser, token):
    """
    Return the nb last events for a community
    {% get_last_communities_pages community nb as attachments %}
    """
    bits = token.split_contents()
    if 5 != len(bits):
        raise TemplateSyntaxError('%r expects 5 arguments' % bits[0])
    if bits[-2] != 'as':
        raise TemplateSyntaxError(
            '%r expects "as" as the second argument' % bits[0])
    community = bits[1]
    number = int(bits[2])
    varname = bits[-1]
    return last_communities_pages(community, number, varname)
