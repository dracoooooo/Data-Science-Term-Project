from django.db import models
from django.contrib import admin
from django.contrib.contenttypes.models import ContentType
from django.contrib.sites.models import Site
from django.contrib.auth.models import User
from django.core.cache import cache

from django.utils.translation import ugettext_lazy as _
from zorna.acl.models import ACLPermission
from zorna.account.models import UserAvatar

ENTITY_DATA_TYPES = (
    ('site', 'Site'),
    ('user', 'User'),
    ('group', 'Group'),
    ('object', 'Object'),
)

ENTITIES_OWNERS_CACHE_CACHE = 'entities_owners_cache'
ENTITIES_AVATARS_CACHE_CACHE = 'entities_avatars_cache'


class ZornaEntityAdmin(admin.ModelAdmin):

    def save_model(self, request, obj, form, change):
        if not change:
            obj.owner = request.user
        obj.save()


class ZornaEntity(models.Model):

    """
    """
    site = models.ForeignKey(
        Site, null=True, related_name='site_owner_%(app_label)s_%(class)s_related', editable=False)
    owner = models.ForeignKey(
        User, null=True, related_name='user_owner_%(app_label)s_%(class)s_related', editable=False)
    modifier = models.ForeignKey(
        User, null=True, related_name='user_modifier_%(app_label)s_%(class)s_related', editable=False)
    time_created = models.DateTimeField(_(
        'Date created'), auto_now_add=True, editable=False)
    time_updated = models.DateTimeField(_(
        'Date updated'), auto_now=True, auto_now_add=True, editable=False)
    enabled = models.BooleanField(_('Enabled'), editable=False)

    class Meta:
        abstract = True

    def save(self, *args, **kwargs):
        self.site = Site.objects.get_current()
        super(ZornaEntity, self).save(*args, **kwargs)

    def delete(self, *args, **kwargs):
        ct = ContentType.objects.get_for_model(self)
        ACLPermission.objects.filter(
            object_id=self.pk, content_type=ct).delete()
        super(ZornaEntity, self).delete(*args, **kwargs)

    def as_leaf_class(self):
        content_type = self.content_type
        model = content_type.model_class()
        if(model == ZornaEntity):
            return self
        return model.objects.get(id=self.id)

    def get_owner_full_name(self):
        return self.get_user_full_name(self.owner_id)

    def get_modifier_full_name(self):
        return self.get_user_full_name(self.modifier_id)

    def get_user_avatar(self, user_id):
        entities_avatars_cache = cache.get(ENTITIES_AVATARS_CACHE_CACHE)
        if entities_avatars_cache is None:
            entities_avatars_cache = {}

        try:
            return entities_avatars_cache[user_id]
        except:
            try:
                avatar_user = UserAvatar.objects.get(user=user_id)
            except UserAvatar.DoesNotExist:
                avatar_user = None

            entities_avatars_cache[
                user_id] = avatar_user.avatar if avatar_user else ''
            cache.set(ENTITIES_AVATARS_CACHE_CACHE, entities_avatars_cache)

        return entities_avatars_cache[user_id]

    def get_owner_avatar(self):
        return self.get_user_avatar(self.owner_id)

    def get_modifier_avatar(self):
        return self.get_user_avatar(self.modifier_id)

    def get_user_full_name(self, user_id):
        entities_owners_cache = cache.get(ENTITIES_OWNERS_CACHE_CACHE)
        if entities_owners_cache is None:
            entities_owners_cache = {}

        try:
            return entities_owners_cache[user_id].get_full_name()
        except:
            try:
                entities_owners_cache[user_id] = User.objects.get(pk=user_id)
                cache.set(ENTITIES_OWNERS_CACHE_CACHE, entities_owners_cache)
            except User.DoesNotExist:
                return ''
        return entities_owners_cache[user_id].get_full_name()

    def get_acl_permissions():
        return {}
    get_acl_permissions = staticmethod(get_acl_permissions)
