import os
from django.conf import settings
from django.db import models
from django.utils.translation import ugettext_lazy as _
from django.contrib.contenttypes import generic
from django.contrib.contenttypes.models import ContentType
from django.core.files.storage import FileSystemStorage
from django.template.defaultfilters import slugify

from mptt.models import MPTTModel

from zorna.utilit import get_upload_menus

fs = FileSystemStorage(location=get_upload_menus(), base_url='/images/menus/')


def get_menu_image_filepath(instance, filename):
    s = os.path.splitext(filename)
    filename = u"%s%s" % (slugify(s[0]), s[1])
    return os.path.join(u"m%s/%s" % (str(instance.pk), filename))


class ZornaMenuItem(MPTTModel):
    parent = models.ForeignKey(
        'self', null=True, blank=True, related_name='children')
    name = models.CharField(_(u'name'), max_length=255, default='')
    slug = models.SlugField(_('slug'), max_length=255, unique=True)
    title = models.CharField(_(u'title'), max_length=255, default='',
                             help_text=_(u'Shown when hovering over the menu link'), blank=True)
    image = models.ImageField(
        storage=fs, max_length=1024, upload_to=get_menu_image_filepath, blank=True, default='')
    url = models.CharField(_(
        u'url'), max_length=255, blank=True, editable=False)
    target_blank = models.BooleanField(_(u'Open in new window'))
    content_type = models.ForeignKey(
        ContentType, null=True, blank=True, editable=False)
    object_id = models.IntegerField(editable=False, null=True, blank=True)
    content_object = generic.GenericForeignKey('content_type', 'object_id')
    extra_info = models.CharField(_(
        'extra'), max_length=255, editable=False, default='')

    class Meta:
        verbose_name = _('menu item')
        verbose_name_plural = _('menu items')
        ordering = ['tree_id', 'lft']
        db_table = settings.TABLE_PREFIX + "menu_items"

    def __unicode__(self):
        return u"%s" % self.name
