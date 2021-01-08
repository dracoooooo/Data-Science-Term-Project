# Copyright (c) 2010, AFRAZ ( http://www.afraz.fr )
# All rights reserved.

VERSION = (1, 1, 8, "final", 0)

from django.contrib.auth.models import User, Group
from django.contrib.sites.models import Site
import mptt

from django.db.models.signals import post_save, post_delete
import settings

from zorna.account.models import UserProfile

def get_version():
    version = '%s.%s' % (VERSION[0], VERSION[1])
    if VERSION[2]:
        version = '%s.%s' % (version, VERSION[2])
    if VERSION[3:] == ('alpha', 0):
        version = '%s pre-alpha' % version
    else:
        if VERSION[3] != 'final':
            version = '%s %s %s' % (version, VERSION[3], VERSION[4])
    return version


def user_post_save(sender, **kwargs):
    instance = kwargs['instance']
    if kwargs['created'] == True:
        try:
            instance.get_profile()
        except UserProfile.DoesNotExist:
            UserProfile.objects.create_user_profile(instance, {}, True)

post_save.connect(user_post_save, sender=User)
