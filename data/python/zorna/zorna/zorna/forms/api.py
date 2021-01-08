import os
from os.path import join
import urllib
from django.conf import settings
from django.utils.translation import ugettext_lazy as _
from django.utils.encoding import smart_str
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
from django import forms
from django.core.files.storage import FileSystemStorage
from django.contrib.contenttypes.models import ContentType

from zorna.utilit import get_upload_forms_attachments
from zorna.forms import fields
from zorna.acl.models import get_allowed_objects
from zorna.forms.models import FormsFieldEntry, FormsWorkspace, FormsForm, FormsFormEntry
from zorna.forms.forms import FormForForm
from zorna.communities.api import detete_message_community_extra


def isUserManager(request, slug):
    try:
        fw = FormsWorkspace.objects.get(slug=slug)
        ao = get_allowed_objects(request.user, FormsWorkspace, 'manager')
        if fw.pk in ao:
            return fw
        else:
            return False
    except:
        return False


def forms_get_entries(slug_or_form, *args, **kwargs):
    """
    """
    return FormsFieldEntry.objects.forms_get_entries(slug_or_form, *args, **kwargs)


def forms_get_entry(entry):
    if not isinstance(entry, FormsFormEntry):
        entry = FormsFormEntry.objects.get(pk=entry)
    columns, entries = forms_get_entries(entry.form, entries=[entry.pk])
    return columns, entries[0]


def forms_delete_entry(entry):
    if not isinstance(entry, FormsFormEntry):
        entry = FormsFormEntry.objects.get(pk=entry)
    form = entry.form
    fs = FileSystemStorage(location=get_upload_forms_attachments())
    path = join(fs.location, str(form.pk), str(entry.pk))
    if os.path.isdir(path):
        import shutil
        shutil.rmtree(path)
    ct = ContentType.objects.get_for_model(FormsFormEntry)
    detete_message_community_extra(entry, ct)
    FormsFieldEntry.objects.filter(field__reference__startswith='%s.' %
                                   form.slug, value='%s' % entry.pk).update(value='')
    entry.delete()


def forms_get_form(slug):
    try:
        form = FormsForm.objects.get(slug=slug)
    except FormsForm.DoesNotExist:
        return None
    return form


def forms_get_form_edit_entry(request, entry):
    try:
        entry = FormsFormEntry.objects.get(pk=entry)
        form = entry.form
    except:
        return None

    rget = request.GET  # {'lot.designation': '110'}
    args = (form, request.POST or None, request.FILES or None)
    form_for_form = FormForForm(*args, instance=entry, rget=rget)
    return form_for_form


def forms_get_form_add_entry(request, form_slug, **kwargs):
    form = forms_get_form(form_slug)
    if form:
        kwargs['rget'] = request.GET  # {'lot.designation': '110'}
        args = (form, request.POST or None, request.FILES or None)
        form_for_form = FormForForm(*args, **kwargs)
        return form_for_form
    else:
        return None


def get_users_profile_form_entries(users):
    try:
        form = FormsForm.objects.get(slug=settings.ZORNA_USER_PROFILE_FORM)
        try:
            entries = FormsFormEntry.objects.filter(
                form=form, account__in=users)
        except FormsFormEntry.DoesNotExist:
            entries = None
    except Exception as e:
        entries = None

    if entries:
        return forms_get_entries(form, entries=entries)
    else:
        return None, None


def get_users_profile_form_entry(user):
    c, r = get_users_profile_form_entries([user])
    if r:
        return c, r[0]
    else:
        return None, None


def forms_form_browse_entries(request, slug, entries=None):
    try:
        form = FormsForm.objects.select_related(depth=1).get(slug=slug)
    except:
        return None

    # dont't verify access, caller must do it for us
    extra_context = {}
    kwargs = {}
    sort = ''
    if entries:
        kwargs['entries'] = entries
    extra_context['filters'] = {}
    extra_context['field_filters'] = []
    form_fields = form.fields.select_related().all().order_by('label')
    form.fields_reference = {}
    hidden = request.GET.get('hidden', '')
    if hidden:
        hidden = hidden.split(',')
    else:
        hidden = []
    for f in form_fields:
        fl = request.GET.get(f.slug, None)
        if fl:
            kwargs[f.slug] = smart_str(fl)
        if f.for_sort:
            sort = f.slug
        if not f.visible_in_list:
            hidden.append(f.slug)
    extra_context['params'] = urllib.urlencode(kwargs)

    kwargs['f'] = request.GET.get('f', '')
    kwargs['q'] = request.GET.get('q', '')
    kwargs['o'] = request.GET.get('o', sort)
    kwargs['ot'] = request.GET.get('ot', 'asc')
    kwargs['where'] = request.GET.get('where', '')

    extra_context['parents'] = []
    if kwargs['where']:
        try:
            bte = kwargs['where'].split(':')
            extra_context['where_entry_id'] = entry_id = bte[1]
            bte = bte[0].split('.')
            extra_context['where_form_slug'] = form_slug = bte[0]
            extra_context['where_form_field'] = form_field = bte[1]
            while entry_id:
                e = FormsFormEntry.objects.select_related().get(
                    pk=entry_id, form__slug=form_slug)
                c, r = forms_get_entry(e)
                extra_context['parents'].append({'row': r[
                                                form_field], 'entry': e})
                hidden.append(r[form_field]['slug'])
                if not e.form.bind_to_entry:
                    break
                bte = e.form.bind_to_entry.split('.')
                form_slug = bte[0]
                form_field = bte[1]
                entry_id = e.entry.pk
        except Exception as e:
            extra_context['parents'] = []
    extra_context['parents'].reverse()
    kwargs['hidden'] = ','.join(hidden)

    columns, entries = forms_get_entries(form, **kwargs)
    for f in form_fields:
        if '.' in f.reference and f.is_a(*fields.CHOICES):
            fl = request.GET.get(f.slug, None)
            choices = [('', '--- %s ---' % f.label)]
            for e in form.fields_reference[f.pk]:
                choices.append((e[0], e[1]))
            s = forms.Select(choices=choices)
            extra_context['filters'][f.label] = s.render(f.slug, fl, None)
        else:
            extra_context['field_filters'].append([f.label, f.slug])
            if fl:
                kwargs[f.slug] = fl

    paginator = Paginator(entries, 25)
    page = int(request.GET.get('page', 1))
    try:
        rows = paginator.page(page)
    except PageNotAnInteger:
        # If page is not an integer, deliver first page.
        rows = paginator.page(1)
    except EmptyPage:
        # If page is out of range (e.g. 9999), deliver last page of results.
        rows = paginator.page(paginator.num_pages)

    extra_context['q'] = kwargs['q']
    extra_context['f'] = kwargs['f']
    extra_context['column_filter'] = kwargs['o']
    extra_context['column_order'] = kwargs['ot']
    extra_context['zorna_title_page'] = _(u'Forms')
    extra_context['form'] = form
    extra_context['columns'] = columns
    extra_context['entries'] = entries
    extra_context['rows'] = rows
    extra_context['page'] = page
    extra_context['paginator'] = paginator
    extra_context['where'] = kwargs['where']
    try:
        r = form.bind_to_entry.split('.')
        extra_context['bind_entry_slug'] = r[0]
        extra_context['bind_entry_field'] = r[1]
    except:
        pass

    extra_context['where'] = kwargs['where']

    return extra_context
