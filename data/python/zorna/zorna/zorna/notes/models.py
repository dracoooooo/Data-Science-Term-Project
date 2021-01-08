import os
from django.conf import settings
from django.db import models
from django.utils.translation import ugettext_lazy as _
from django.utils.translation import ugettext_noop
from django.core.files.storage import FileSystemStorage
from django.template.defaultfilters import slugify

from mptt.models import MPTTModel
from tagging.fields import TagField
from tagging.utils import parse_tag_input
from tagging.models import Tag

from zorna.models import ZornaEntity
from zorna.utilit import get_upload_notes_attachments


class ZornaNoteCategory(MPTTModel, ZornaEntity):
    parent = models.ForeignKey(
        'self', null=True, blank=True, related_name='children')
    name = models.CharField(_('name'), max_length=255)
    slug = models.SlugField(_('slug'), max_length=255, null=True, blank=True)

    class Meta:
        verbose_name = _('note category')
        verbose_name_plural = _('note categories')
        ordering = ['tree_id', 'lft']
        db_table = settings.TABLE_PREFIX + "note_categories"

    def __unicode__(self):
        return self.name

    def get_acl_permissions():
        return {
            'viewer': ugettext_noop(u'Who can browse notes in this category'),
        }
    get_acl_permissions = staticmethod(get_acl_permissions)


class ZornaNote(ZornaEntity):
    category = models.ForeignKey(ZornaNoteCategory)
    tags = TagField()
    title = models.CharField(max_length=255)
    content = models.TextField(_('content'))

    class Meta:
        verbose_name = _('note')
        verbose_name_plural = _('notes')
        ordering = ['-time_updated']
        db_table = settings.TABLE_PREFIX + "notes"

    def __unicode__(self):
        return u"[%s] %s" % (self.owner.username, self.title)

    def delete(self):
        # Deleting all asociated tags.
        Tag.objects.update_tags(self, None)
        super(ZornaNote, self).delete()

    def get_tag_list(self):
        return parse_tag_input(self.tags)


fs = FileSystemStorage(location=get_upload_notes_attachments(), base_url='')


def get_note_filepath(instance, filename):
    s = os.path.splitext(filename)
    filename = u"%s%s" % (slugify(s[0]), s[1])
    return os.path.join(u"u%s/%s" % (str(instance.note.pk), filename))


class ZornaNoteFile(models.Model):
    note = models.ForeignKey(ZornaNote, blank=True, editable=False)
    file = models.FileField(
        storage=fs, max_length=1024, upload_to=get_note_filepath, blank=True)
    description = models.CharField(max_length=255)
    mimetype = models.CharField(max_length=255, editable=False)

    class Meta:
        verbose_name = _('Note attachment')
        db_table = settings.TABLE_PREFIX + "note_attachments"

    def __unicode__(self):
        return _(u'Attachment for %s') % self.note

    def delete(self, *args, **kwargs):
        path = self.file.path
        os.remove(path)
        return super(ZornaNoteFile, self).delete()
