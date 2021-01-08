from django.conf import settings
from django.db import models
from django.utils.translation import ugettext_lazy as _
from django.utils.translation import ugettext_noop, ugettext
from django.dispatch import receiver

from zorna.acl.models import get_acl_for_model, get_acl_by_object
from zorna.models import ZornaEntity
from zorna.site.signals import site_options_called
from zorna.account.models import UserGroup


class SiteAlert(ZornaEntity):
    text = models.TextField(_('alert'))
    start = models.DateTimeField(_('Start date'))
    end = models.DateTimeField(_('End date'))

    class Meta:
        ordering = ['start']
        db_table = settings.TABLE_PREFIX + "alerts"

    def __unicode__(self):
        return self.text

    def get_acl_permissions():
        return {
            'viewer': ugettext_noop(u'Who can view this alert'),
        }
    get_acl_permissions = staticmethod(get_acl_permissions)

REGISTRATION_EMAIL_VALIDATION = 0
REGISTRATION_ADMIN_VALIDATION = 1
REGISTRATION_NO_VALIDATION = 2

REGISTRATION_VALIDATION_TYPES = (
    (REGISTRATION_EMAIL_VALIDATION, _(
        'Confirm account by validating address email')),
    (REGISTRATION_ADMIN_VALIDATION, _(
        'Manual confirmation by administrators')),
    (REGISTRATION_NO_VALIDATION, _(
        'Confirm account without address email validation')),
)


class SiteRegistration(ZornaEntity):
    allow_registration = models.BooleanField(
        _('allow registration'), default=False, editable=True,
        help_text=_('Enable this option to let users register on your site'))
    validation_type = models.IntegerField(_(
        'validation type'), max_length=1, choices=REGISTRATION_VALIDATION_TYPES, default=0)
    groups = models.ManyToManyField(UserGroup, verbose_name=_(
        'groups'), blank=True, editable=False)

    class Meta:
        db_table = settings.TABLE_PREFIX + "site_registration"

    def __unicode__(self):
        return self.text


class SiteOptionsManager(models.Manager):

    def is_access_valid(self, user, key_option):
        try:
            so = self.get(key=key_option)
            if so:
                check = get_acl_for_model(SiteOptions)
                return check.access_siteoptions(so, user)
            else:
                return False
        except Exception as e:
            return False

    def get_authorized_user(self, key_option):
        try:
            so = self.get(key=key_option)
            if so:
                return get_acl_by_object(so, 'access')
            else:
                return []
        except Exception as e:
            return []

    def get_ckeditor_config(self, request):
        baccess = self.is_access_valid(request.user, 'zorna_ckeditor')
        if baccess:
            return 'default'
        else:
            return 'basic'


class SiteOptions(ZornaEntity):
    option = models.CharField(_('option'), max_length=255)
    key = models.CharField(_('key'), max_length=255, unique=True)
    objects = SiteOptionsManager()

    class Meta:
        ordering = ['key']
        db_table = settings.TABLE_PREFIX + "site_options"

    def __unicode__(self):
        return ugettext(self.option)

    def get_acl_permissions():
        return {
            'access': ugettext_noop(u'Who can access this option'),
        }
    get_acl_permissions = staticmethod(get_acl_permissions)

SITES_OPTIONS = {
    'zorna_personal_calendar': ugettext_noop(u'Who can have personal calendar'),
    'zorna_personal_documents': ugettext_noop(u'Who can store personal documents on the server'),
    'zorna_pages_pages': ugettext_noop(u'Who can manage static pages'),
    'zorna_pages_templates': ugettext_noop(u'Who can manage pages templates'),
    'zorna_personal_notes': ugettext_noop(u'Who can have personal notes'),
    'zorna_validate_registration': ugettext_noop(u'Who can validate registration'),
    'zorna_menus': ugettext_noop(u'Who can manage menus'),
    'zorna_ckeditor': ugettext_noop(u'Who can use full ckeditor'),
}


@receiver(site_options_called, sender=None)
def handler_views_was_called(**kwargs):
    options = {}
    opts = SiteOptions.objects.filter(key__startswith='zorna_')
    for opt in opts:
        options[opt.key] = opt
    for k, o in SITES_OPTIONS.iteritems():
        if k in options:
            if options[k].option != o:
                options[k].option = o
                options[k].save()
            del options[k]
        else:
            opt = SiteOptions(option=o, key=k)
            opt.save()
    if len(options):
        SiteOptions.objects.filter(pk__in=[
                                   opt.pk for opt in options.values()]).delete()
