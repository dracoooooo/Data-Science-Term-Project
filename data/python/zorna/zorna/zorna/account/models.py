import os
import datetime
import random
import re
import hashlib
import mptt
from django.conf import settings
from django.db import models
from django.utils.translation import ugettext_lazy as _
from django.contrib.auth.models import User
from django.db.models.signals import post_delete
from django.core.files.storage import FileSystemStorage
from django.template.defaultfilters import slugify

from zorna.utilit import get_upload_avatars

SHA1_RE = re.compile('^[a-f0-9]{40}$')


class UserGroup(models.Model):
    name = models.CharField(_('name'), max_length=80)
    parent = models.ForeignKey(
        'self', null=True, blank=True, related_name='children')

    class Meta:
        verbose_name_plural = "Groups"
        db_table = settings.TABLE_PREFIX + "usergroup"

    def __unicode__(self):
        return u'%s' % (self.name)


try:
    mptt.register(UserGroup)
except mptt.AlreadyRegistered:
    pass


class UserProfileManager(models.Manager):

    def activate_user(self, activation_key):
        if SHA1_RE.search(activation_key):
            try:
                profile = self.get(activation_key=activation_key)
            except self.model.DoesNotExist:
                return False
            if not profile.activation_key_expired():
                user = profile.user
                user.is_active = True
                user.save()
                profile.activation_key = self.model.ACTIVATED
                profile.save()
                return user
        return False

    def create_user_profile(self, user, data, register=True):
        if register:
            h = hashlib.sha1()
            h.update(str(random.random()))
            salt = h.hexdigest()[:5]
            h.update(salt + user.username)
            activation_key = h.hexdigest()
        else:
            activation_key = self.model.ACTIVATED
        r = self.create(user=user,
                        activation_key=activation_key,
                        **data
                        )
        return r


class UserProfile(models.Model):

    """
    """
    ACTIVATED = u"ALREADY_ACTIVATED"

    user = models.ForeignKey(
        User, null=True, blank=True, related_name='user_profile')
    activation_key = models.CharField(_(
        'activation key'), max_length=40, editable=False)
    reset_password_key = models.CharField(_(
        'activation key'), max_length=40, editable=False, default='')
    # To minimize database writes, the last_activity field is only updated
    # once per day
    last_activity = models.DateTimeField(_(
        'last activity'), null=True, blank=True, editable=False)
    groups = models.ManyToManyField(
        UserGroup, verbose_name=_('groups'), blank=True,
        help_text=_("In addition to the permissions manually assigned, this user will also get all permissions granted to each group he/she is in."))

    objects = UserProfileManager()

    class Meta:
        verbose_name = _('User profile')
        db_table = settings.TABLE_PREFIX + "userprofile"

    def __unicode__(self):
        return self.user.get_full_name()

    def update_activity(self):
        self.last_activity = datetime.datetime.now()
        self.save()

    def get_avatar(self):
        return UserAvatar.objects.get(user=self.user)

    def email(self):
        return self.user.email

    def first_name(self):
        return self.user.first_name

    def last_name(self):
        return self.user.last_name

    def is_superuser(self):
        return self.user.is_superuser

    def activation_key_expired(self):
        """
        Determine whether this ``RegistrationProfile``'s activation
        key has expired, returning a boolean -- ``True`` if the key
        has expired.

        Key expiration is determined by a two-step process:

        1. If the user has already activated, the key will have been
           reset to the string ``ALREADY_ACTIVATED``. Re-activating is
           not permitted, and so this method returns ``True`` in this
           case.

        2. Otherwise, the date the user signed up is incremented by
           the number of days specified in the setting
           ``ACCOUNT_ACTIVATION_DAYS`` (which should be the number of
           days after signup during which a user is allowed to
           activate their account); if the result is less than or
           equal to the current date, the key has expired and this
           method returns ``True``.

        """
        expiration_date = datetime.timedelta(
            days=settings.ACCOUNT_ACTIVATION_DAYS)
        return self.activation_key == self.ACTIVATED or \
            (self.user.date_joined +
                expiration_date <= datetime.datetime.now())
    activation_key_expired.boolean = True

fs = FileSystemStorage(location=get_upload_avatars())


def get_avatar_filepath(instance, filename):
    s = os.path.splitext(filename)
    filename = u"%s%s" % (slugify(s[0]), s[1])
    return os.path.join(u"avatars/u%s/%s" % (str(instance.user.pk), filename))


def delete_user_avatar(sender, **kwargs):
    obj = kwargs['instance']
    path = get_upload_avatars() + u"/u%s/" % obj.user.pk
    try:
        files = os.listdir(path)
        for f in files:
            fullpath = os.path.join(path, f)
            if os.path.isfile(fullpath):
                os.remove(fullpath)
    except:
        pass


class UserAvatar(models.Model):
    user = models.ForeignKey(User)
    avatar = models.ImageField(
        storage=fs, max_length=1024, upload_to=get_avatar_filepath, blank=True)

    class Meta:
        verbose_name = _('User avatar')
        db_table = settings.TABLE_PREFIX + "useravatar"

    def __unicode__(self):
        return _(u'Avatar for %s') % self.user

    def save(self, *args, **kwargs):
        if self.pk is None:
            UserAvatar.objects.filter(user=self.user).delete()
        super(UserAvatar, self).save(*args, **kwargs)

post_delete.connect(delete_user_avatar, sender=UserAvatar)
