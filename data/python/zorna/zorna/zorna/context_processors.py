import datetime
from django.conf import settings
from zorna.acl.models import get_allowed_objects
from django.contrib.sites.models import Site

from zorna.site.models import SiteRegistration
from zorna.calendars.api import get_personal_calendar


def alerts(request):
    from zorna.site.models import SiteAlert
    ao = get_allowed_objects(request.user, SiteAlert, 'viewer')
    alerts = SiteAlert.objects.filter(
        pk__in=ao, start__lte=datetime.datetime.now(), end__gte=datetime.datetime.now())
    context_extras = {}
    context_extras['ALERTS'] = alerts

    return context_extras


def zsettings(request):
    """
    Adds zorna context variables to the context.

    """
    try:
        reg = SiteRegistration.objects.get(site=Site.objects.get_current())
        allow_registration = reg.allow_registration
    except:
        allow_registration = False

    media_plugin_url = settings.MEDIA_PLUGIN_URL

    if request.user.is_anonymous():
        usergroups = []
        calendar = None
    else:
        usergroups = [str(g.pk)
                      for g in request.user.get_profile().groups.all()]
        calendar = get_personal_calendar(request.user)

    return {'CKEDITOR_MEDIA_PREFIX': settings.CKEDITOR_MEDIA_PREFIX,
            'ZORNA_SKIN': settings.ZORNA_SKIN,
            'ZORNA_REGISTRATION': allow_registration,
            'MEDIA_PLUGIN_URL': media_plugin_url,
            'ZORNA_USER_GROUPS': usergroups,
            'ZORNA_USER_CALENDAR': calendar,
            }
