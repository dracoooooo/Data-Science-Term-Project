import os
import re
import shutil
from itertools import chain
from django.conf import settings
from django.forms import ModelForm
from django.template.defaultfilters import slugify
from django import forms
from django.utils.translation import ugettext_lazy as _
from django.contrib.auth.models import User
from django.template.loader import render_to_string
from django.core.urlresolvers import reverse

from schedule.models.events import Event
from schedule.models.calendars import Calendar
from ckeditor.widgets import CKEditorWidget

from zorna.site.email import ZornaEmail
from zorna.site.models import SiteOptions
from zorna.communities.models import Community, MessageCommunity, EventCommunity, PollCommunityChoice, PollCommunity, UrlCommunity, PageCommunity
from zorna.acl.models import get_allowed_objects, get_acl_by_object
from zorna.utilit import get_upload_library, get_upload_communities
from zorna.fileman.models import ZornaFile
from zorna.calendars.models import EventDetails
from zorna.calendars.api import get_personal_calendar
from zorna.communities.api import get_community_calendar

EMBEDLY_RE = re.compile(
    "((http://(www\.flickr\.com/photos/.*|flic\.kr/.*|picasaweb\.google\.com.*/.*/.*#.*|picasaweb\.google\.com.*/lh/photo/.*|picasaweb\.google\.com.*/.*/.*|twitter\.com/.*/status/.*|twitter\.com/.*/statuses/.*|www\.twitter\.com/.*/status/.*|www\.twitter\.com/.*/statuses/.*|mobile\.twitter\.com/.*/status/.*|mobile\.twitter\.com/.*/statuses/.*|www\.slideshare\.net/.*/.*|www\.slideshare\.net/mobile/.*/.*|slidesha\.re/.*|maps\.google\.com/maps\?.*|maps\.google\.com/\?.*|maps\.google\.com/maps/ms\?.*|www\.wikipedia\.org/wiki/.*|.*youtube\.com/watch.*|.*\.youtube\.com/v/.*|youtu\.be/.*|.*\.youtube\.com/user/.*|.*\.youtube\.com/.*#.*/.*|m\.youtube\.com/watch.*|m\.youtube\.com/index.*|.*\.youtube\.com/profile.*|.*\.youtube\.com/view_play_list.*|.*\.youtube\.com/playlist.*|www\.ustream\.tv/recorded/.*|www\.ustream\.tv/channel/.*|www\.ustream\.tv/.*|.*\.dailymotion\.com/video/.*|.*\.dailymotion\.com/.*/video/.*))|(https://(twitter\.com/.*/status/.*|twitter\.com/.*/statuses/.*|www\.twitter\.com/.*/status/.*|www\.twitter\.com/.*/statuses/.*|mobile\.twitter\.com/.*/status/.*|mobile\.twitter\.com/.*/statuses/.*|.*youtube\.com/watch.*|.*\.youtube\.com/v/.*)))", re.I)


class CommunityAdminAddForm(ModelForm):
    name = forms.CharField(label=_(
        u'Name'), widget=forms.TextInput(attrs={'size': '80'}))
    description = forms.CharField(label=_(
        u'Description'), widget=forms.Textarea(attrs={'rows': '5', 'cols': '80'}))

    class Meta:
        model = Community


class MessageCommunityForm(forms.Form):
    message = forms.CharField(widget=forms.Textarea(
        attrs={'rows': '5', 'cols': '40'}))
    send_to = forms.CharField(widget=forms.TextInput(attrs={
                              'class': 'com_recipients'}), label=_(u'Recipients'))
    attachments = forms.CharField(label=_(u'Attachments'), widget=forms.FileInput(
        attrs={'size': '20', 'class': 'multi', 'maxlength': '2'}), required=False)

    def save(self, request):
        message = self.cleaned_data['message']
        send_to = self.cleaned_data['send_to']
        upload_to = []
        calendar_owners = []
        dest = []
        ao = get_allowed_objects(request.user, Community, ['manage', 'member'])
        if send_to:
            send_to = send_to.split(',')
            for item in send_to:
                item = item.split('-')
                if item[0] == 'u':  # user
                    user = User.objects.get(pk=item[1])
                    # if user recipient member of any current user communities
                    ao_member_user = get_allowed_objects(
                        user, Community, ['member', 'manage'])
                    inter = [k for k in ao if k in ao_member_user]
                    if len(inter):
                        dest.append(user)
                        calendar_owners.append(user)
                        upload_to.append(u"U%s" % user.pk)
                else:
                    community = Community.objects.get(pk=item[1])
                    if community.pk in ao:
                        dest.append(community)
                        calendar_owners.append(community)
                        upload_to.append(u"C%s" % community.pk)
            users_emails = []
            if len(dest):
                m = MessageCommunity(message=message)
                m.owner = m.modifier = request.user
                m.save()
                for k in dest:
                    if isinstance(k, User):
                        m.users.add(k)
                        users_emails.append(k.email)
                    else:
                        m.communities.add(k)
                        if k.email_notification:
                            users = list(chain(get_acl_by_object(
                                k, 'member'), get_acl_by_object(k, 'manage')))
                            users_emails.extend([u.email for u in users])
            else:
                return None

            files = request.FILES.getlist("attachments")
            if len(upload_to) and len(files):
                try:
                    path_library = get_upload_library()
                    path = os.path.join(get_upload_communities(), "%s" % m.pk)
                    if not os.path.isdir(path):
                        os.makedirs(path)
                    for f in request.FILES.getlist("attachments"):
                        s = os.path.splitext(f.name)
                        fname = slugify(s[0])
                        fext = s[1]
                        destination = open(u"%s/%s" % (
                            path, u"%s%s" % (fname, fext)), 'wb+')
                        for chunk in f.chunks():
                            destination.write(chunk)
                        destination.close()
                        for d in upload_to:
                            destpath = os.path.join(path_library, "%s" % d)
                            if not os.path.isdir(destpath):
                                os.makedirs(destpath)
                            try:
                                libfile = ZornaFile(
                                    owner=request.user, modifier=request.user)
                                libfile.save()
                                fsrc = u"%s/%s/%s,%s%s" % (
                                    path_library, d, str(libfile.pk), fname, fext)
                                shutil.copy2(u"%s/%s" % (
                                    path, u"%s%s" % (fname, fext)), fsrc)
                            except Exception as e:
                                print(e)
                except Exception as e:
                    pass
            # send email notification
            if len(users_emails):
                users_emails = list(set(users_emails))
                if users_emails:
                    email = ZornaEmail()
                    url = request.build_absolute_uri(reverse(
                        'communities_home_page', args=[])) + '?all_msg=message&message_id=%s' % m.pk
                    ec = {"message": m, 'url': url, 'user': request.user}
                    body_text = render_to_string(
                        'communities/email_notification_text.html', ec)
                    body_html = render_to_string(
                        'communities/email_notification_html.html', ec)
                    subject = _(
                        u'A new message has been posted in communities')
                    step = getattr(settings, "ZORNA_MAIL_MAXPERPACKET", 25)
                    for n in range(0, len(users_emails) / step + 1):
                        email.append(subject, body_text, body_html, settings.DEFAULT_FROM_EMAIL, bcc=users_emails[
                                     n * step:(n + 1) * step])
                    email.send()
            return m
        return None


class InviteCommunityForm(forms.Form):
    send_to = forms.CharField(widget=forms.TextInput(attrs={
                              'class': 'com_users'}), label=_(u'Choose'), help_text=_(u"Enter names"))
    manager = forms.BooleanField(label=_(u"Manager"), initial=False, required=False, help_text=_(
        u"Check this if you want to invite these people as managers"))
    message = forms.CharField(label=_(u"Message"),
                              widget=forms.Textarea(attrs={
                                                    'rows': '5', 'cols': '40'}),
                              help_text=_(u"Tell them why you're adding them")
                              )


class EventCommunityForm(ModelForm):
    title = forms.CharField()
    start = forms.DateTimeField(
        label=_("Start date"),
        widget=forms.SplitDateTimeWidget
    )
    end = forms.DateTimeField(label=_("End date"), widget=forms.SplitDateTimeWidget, help_text=_(
        "The end time must be later than start time."))

    class Meta:
        model = EventCommunity

    def clean(self):
        start = self.cleaned_data.get("start")
        end = self.cleaned_data.get("end")
        if not 'start' in self.cleaned_data or not 'end' in self.cleaned_data:
            raise forms.ValidationError(_(
                u'You must specify start and end dates'))
        elif start > end:
            raise forms.ValidationError(_(
                u'The end time must be later than start time.'))
        return self.cleaned_data

    def save(self, *args, **kwargs):
        m = kwargs.pop('message', None)
        request = kwargs.pop('request', None)
        try:
            calendar = Calendar.objects.get(name='default')
        except Calendar.DoesNotExist:
            calendar = Calendar(name='default', slug='default')
            calendar.save()

        if self.instance.pk:
            self.instance.event.title = self.cleaned_data['title']
            self.instance.event.start = self.cleaned_data['start']
            self.instance.event.end = self.cleaned_data['end']
            self.instance.event.save()
            return self.instance.event
        else:
            event_data = {
                'title': self.cleaned_data['title'],
                'start': self.cleaned_data['start'],
                'end': self.cleaned_data['end'],
                'description': m.message,
                'creator': request.user,
                'calendar': calendar,
            }
            event = Event(**event_data)
            event.save()
            EventDetails.objects.create_details(event)
            ec = EventCommunity(event=event)
            ec.save()
            for cal_owner in m.users.all():
                cal = get_personal_calendar(cal_owner)
                event.create_relation(cal.content_object)

            for cal_owner in m.communities.all():
                cal = get_community_calendar(cal_owner)
                event.create_relation(cal.content_object)
            return ec


class PollCommunityForm(ModelForm):
    question = forms.CharField(widget=forms.Textarea(
        attrs={'rows': '2', 'cols': '40'}))

    class Meta:
        model = PollCommunity

    def __init__(self, form, *args, **kwargs):
        self.poll_form_set = form
        super(PollCommunityForm, self).__init__(*args, **kwargs)

    def clean(self):
        if not self.instance.pk:
            # Don't let user to update poll question
            if self.poll_form_set.total_form_count() < 2 or not self.poll_form_set.is_valid():
                raise forms.ValidationError(_(
                    u'You must specify at least two choices'))
            else:
                for form in self.poll_form_set.forms:
                    if not form['choice'].data:
                        raise forms.ValidationError(_(
                            u'You must specify at least two choices'))
        return self.cleaned_data


class PollCommunityChoiceForm(ModelForm):
    choice = forms.CharField(widget=forms.TextInput())

    class Meta:
        model = PollCommunityChoice


class UrlCommunityForm(ModelForm):
    url = forms.CharField(widget=forms.TextInput(attrs={'size': 60}))

    class Meta:
        model = UrlCommunity

    def clean(self):
        url = self.cleaned_data.get("url")
        self.url_summary = None
        if settings.EMBEDLY_KEY and EMBEDLY_RE.search(url):
            pass
        elif url and url[0:4].lower() == 'http':
            from zorna.excerpt_extractor import get_summary
            try:
                self.url_summary = get_summary(url)
            except Exception as e:
                raise forms.ValidationError(_(u'Invalid url'))
        else:
            raise forms.ValidationError(_(
                u'Invalid url or script embed codes are not supported'))
        return self.cleaned_data

    def save(self, *args, **kwargs):
        if self.instance.pk:
            self.instance.url = self.cleaned_data['url']
            if self.url_summary:
                self.instance.title = self.url_summary[0]
                self.instance.excerpt = self.url_summary[1]
            else:
                self.instance.title = ''
                self.instance.excerpt = ''
            self.instance.save()
            return self.instance
        else:
            if self.url_summary:
                uc = UrlCommunity(url=self.cleaned_data[
                                  'url'], title=self.url_summary[0], excerpt=self.url_summary[1])
            else:
                uc = UrlCommunity(url=self.cleaned_data[
                                  'url'], title='', excerpt='')
            uc.save()
        return uc

class PageCommunityForm(ModelForm):
    body = forms.CharField(_(u'body'), widget=forms.Textarea(
        attrs={'rows': '5', 'cols': '80'}))


    class Meta:
        model = PageCommunity

    def __init__(self, *args, **kwargs):
        request = kwargs.pop('request')
        super(PageCommunityForm, self).__init__(*args, **kwargs)
        self.fields['body'] = forms.CharField(label=_(u'body'), widget=CKEditorWidget(config_name=SiteOptions.objects.get_ckeditor_config(request)))
