from django.conf import settings
from django.db import models
from django.utils.translation import ugettext_lazy as _
from django.utils.translation import ugettext_noop
from django.contrib.auth.models import User
from django.contrib.contenttypes import generic
from django.contrib.contenttypes.models import ContentType

from schedule.models.events import Event

from zorna.models import ZornaEntity
from zorna.calendars.models import ZornaCalendar


COMMUNITY_STATES = (
    (0, 'Private'),
    (1, 'Public'),
    (2, 'Hidden'),
)


class Community(ZornaEntity):
    name = models.CharField(_('name'), max_length=255)
    description = models.TextField(_('description'), blank=True)
    status = models.IntegerField(choices=COMMUNITY_STATES, default=0,
                                  help_text=_('Option of making your community Public, Private, or Hidden'))
    enable_calendar = models.BooleanField(_('enable calendar'), default=False, editable=True,
                                  help_text=_('Enable this option to let members access calendar community'))
    enable_documents = models.BooleanField(_('enable documents'), default=False, editable=True,
                                  help_text=_('Enable this option to allow members to access files in the Document Manager'))
    email_notification = models.BooleanField(_('email notification'), default=False, editable=True,
                                  help_text=_('Users will receive email notification when a new message is posted'))
    bgcolor = models.CharField(_('background color'), max_length=6)

    class Meta:
        verbose_name = _('community')
        verbose_name_plural = _('communities')
        db_table = settings.TABLE_PREFIX + "communities"

    def __unicode__(self):
        return self.name

    def get_calendar(self):
        return ZornaCalendar.objects.get_or_create_calendar_for_object(self.owner, self, distinction='owner', name=self.name)


    def get_acl_permissions():
        return {
            'member': ugettext_noop(u'Who can join this community'),
            'manage': ugettext_noop(u'Who can manage this community'),
            }
    get_acl_permissions = staticmethod(get_acl_permissions)

class MessageCommunity(ZornaEntity):
    message = models.TextField(_('message'))
    communities = models.ManyToManyField(Community, verbose_name=_('communities'), blank=True, editable=False)
    users = models.ManyToManyField(User, verbose_name=_('users'), blank=True, editable=False, related_name='message_users')
    reply = models.ForeignKey('self', null=True, editable=False)
    followers = models.ManyToManyField(User, verbose_name=_('followers'), blank=True, editable=False, related_name='message_followers')

    class Meta:
        verbose_name = _('community message')
        verbose_name_plural = _('community messages')
        db_table = settings.TABLE_PREFIX + "community_messages"

    def __unicode__(self):
        return self.message

class MessageCommunityExtra(models.Model):
    message = models.ForeignKey(MessageCommunity, null=True, editable=False)
    content_type = models.ForeignKey(ContentType, editable=False)
    object_id = models.IntegerField(editable=False)
    content_object = generic.GenericForeignKey('content_type', 'object_id')

    class Meta:
        verbose_name = _('community message extra info')
        verbose_name_plural = _('community messages extra info')
        db_table = settings.TABLE_PREFIX + "community_messages_extra"

    def __unicode__(self):
        return u'%s [%s]' % (self.content_object, self.message)

class EventCommunity(models.Model):
    event = models.ForeignKey(Event, verbose_name=_("event"), editable=False)

    class Meta:
        verbose_name = _('community event')
        verbose_name_plural = _('community events')
        db_table = settings.TABLE_PREFIX + "community_messages_events"

    def __unicode__(self):
        return self.event.title

    def update_message(self, message, *args, **kwargs):
        self.event.description = message.message
        self.event.save()

    def delete(self):
        self.event.delete()

class PollCommunity(ZornaEntity):
    question = models.TextField(_('question'))

    class Meta:
        verbose_name = _('poll')
        verbose_name_plural = _('polls')
        db_table = settings.TABLE_PREFIX + "community_polls"

    def __unicode__(self):
        return self.question

class PollCommunityChoice(models.Model):
    choice = models.CharField(_('choice'), max_length=255)
    question = models.ForeignKey(PollCommunity, editable=False)
    user = models.ManyToManyField(User, verbose_name=_('users'), blank=True, editable=False)

    class Meta:
        verbose_name = _('poll choice')
        verbose_name_plural = _('poll choices')
        db_table = settings.TABLE_PREFIX + "community_poll_choices"

    def __unicode__(self):
        return self.choice

class UrlCommunity(ZornaEntity):
    url = models.TextField(_('url'))
    title = models.CharField(_('title'), max_length=255, editable=False)
    excerpt = models.TextField(_('excerpt'), editable=False)

    class Meta:
        verbose_name = _('url')
        verbose_name_plural = _('urls')
        db_table = settings.TABLE_PREFIX + "community_urls"

    def __unicode__(self):
        return self.url

class PageCommunity(ZornaEntity):
    title = models.CharField(_('title'), max_length=255)
    body = models.TextField(_('body'))

    class Meta:
        verbose_name = _('page')
        verbose_name_plural = _('pages')
        db_table = settings.TABLE_PREFIX + "community_pages"

    def __unicode__(self):
        return self.title
