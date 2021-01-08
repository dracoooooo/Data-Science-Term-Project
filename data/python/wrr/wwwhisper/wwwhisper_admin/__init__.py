# wwwhisper - web access control.
# Copyright (C) 2012-2015 Jan Wrobel <jan@mixedbit.org>

"""wwwhisper admin API.

The package exposes http API for specifying which users can access
which locations and for other admin operations.
"""
from django.forms import ValidationError
from django.conf import settings
from django.core.urlresolvers import reverse
from django.db.models import signals
from django.contrib.auth import models as contrib_auth_models
from django.core.exceptions import ImproperlyConfigured
from wwwhisper_auth import models as auth_models

SITE_URL = getattr(settings, 'WWWHISPER_INITIAL_SITE_URL', None)

def _create_site():
    """Creates a site configured in settings.py."""
    try:
        site =  auth_models.SitesCollection().create_item(
            auth_models.SINGLE_SITE_ID)
        site.aliases.create_item(SITE_URL)
        return site
    except ValidationError as ex:
        raise ImproperlyConfigured('Failed to create site %s: %s'
                                   % (SITE_URL, ex))

def _create_initial_locations(site):
    """Creates all locations listed in WWWHISPER_INITIAL_LOCATIONS setting."""
    locations_paths = getattr(settings, 'WWWHISPER_INITIAL_LOCATIONS', [])
    for path in locations_paths:
        try:
            site.locations.create_item(path)
        except ValidationError as ex:
            raise ImproperlyConfigured('Failed to create location %s: %s'
                                       % (path, ', '.join(ex.messages)))

def _create_initial_admins(site):
    """Creates all users listed in WWWHISPER_INITIAL_ADMINS setting."""
    emails = getattr(settings, 'WWWHISPER_INITIAL_ADMINS', [])
    for email in emails:
        try:
            user = site.users.create_item(email)
        except ValidationError as ex:
            raise ImproperlyConfigured('Failed to create admin user %s: %s'
                                       % (email, ', '.join(ex.messages)))

def _grant_admins_access_to_all_locations(site):
    for user in site.users.all():
        for location in site.locations.all():
            location.grant_access(user.uuid)

def grant_initial_permission(app, created_models, *args, **kwargs):
    """Configures initial permissions for wwwhisper protected site.

    Allows users with emails listed on WWWHISPER_INITIAL_ADMINS to
    access locations listed on WWWHISPER_INITIAL_LOCATIONS. The
    function is invoked when the wwwhisper database is created.
    Initial access rights is the only difference between users listed
    on WWWHISPER_INITIAL_ADMINS and other users. The admin application
    manages access to itself, so it can be used to add and remove
    users that can perform administrative operations.
    """
    if (auth_models.User in created_models and
        kwargs.get('interactive', True)):
        site = _create_site()
        _create_initial_locations(site)
        _create_initial_admins(site)
        _grant_admins_access_to_all_locations(site)

if SITE_URL:
    # Invoke grant_initial_permission function defined in this module
    # when admin user is created.
    signals.post_syncdb.connect(
        grant_initial_permission,
        sender=contrib_auth_models,
        dispatch_uid = "django.contrib.auth.management.create_superuser")


