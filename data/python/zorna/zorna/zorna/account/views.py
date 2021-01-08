import os
import random
import hashlib
from django.conf import settings
from django.utils.translation import ugettext_lazy as _
from django.utils.encoding import smart_unicode, smart_str
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User
from django.http import HttpResponseRedirect, HttpResponseForbidden, HttpResponse
from django.shortcuts import render_to_response
from django.template import RequestContext, loader
from django.views.generic.list_detail import object_list
from django.core.urlresolvers import reverse
from django.contrib.auth.views import login as authlogin
from django.contrib.auth.views import logout as authlogout
from django.db.models import Q
from django import forms
from django.contrib.sites.models import Site
from django.template.loader import render_to_string
from django.template.defaultfilters import slugify


from schedule.models.events import Calendar
from zorna.account.models import UserProfile, UserGroup, UserAvatar
from zorna.account.forms import UserGroupForm, UserAvatarForm, UsersFormImportCsv
from zorna.account.forms import AccountEditProfileForm, AccountRegistrationForm, RequestNewPasswordForm, ResetPasswordForm
from zorna.forms.models import FormsForm, FormsFormEntry
from zorna.forms.forms import FormForForm
from zorna import defines
from zorna.forms.api import forms_get_entry
from zorna.recaptcha import ReCaptchaField, ReCaptchaWidget
from zorna.site.models import SiteOptions, SiteRegistration, REGISTRATION_ADMIN_VALIDATION, REGISTRATION_NO_VALIDATION
from zorna.site.email import ZornaEmail


def login(request, *args, **kwargs):
    kwargs['extra_context'] = {'site': Site.objects.get_current()}
    return authlogin(request, *args, **kwargs)


def logout(request, *args, **kwargs):
    authlogout(request, *args, **kwargs)
    return HttpResponseRedirect('/')


def reset_password_link_sent(request):
    context = RequestContext(request)
    return render_to_response("account/reset_password_link_sent.html", context_instance=context)


def forgot_passord(request):
    if request.method == "POST":
        form = RequestNewPasswordForm(data=request.POST)
        if form.is_valid():
            user = User.objects.get(Q(username=form.cleaned_data[
                                    'username']) | Q(email=form.cleaned_data['username']))
            up = user.get_profile()

            email = ZornaEmail()
            subject = _(u'Password reset request')
            h = hashlib.sha1()
            h.update(str(random.random()))
            salt = h.hexdigest()[:5]
            h.update(salt + user.username)
            hashcode = h.hexdigest()
            up.reset_password_key = hashcode
            up.save()

            ec = {
                'user': user,
                'site': Site.objects.get_current(),
                'url': request.build_absolute_uri(reverse('request_password')) + '?user=%s&auth=%s' % (user.username, hashcode),
            }
            body_text = render_to_string(
                'account/email_reset_password_text.html', ec)
            body_html = render_to_string(
                'account/email_reset_password_html.html', ec)
            email.append(subject, body_text, body_html,
                         settings.DEFAULT_FROM_EMAIL, [user.email])
            email.send()
            return reset_password_link_sent(request)
    else:
        form = RequestNewPasswordForm()
    context = RequestContext(request)
    return render_to_response("account/forgot_password.html", {'form': form}, context_instance=context)


def password_changed(request):
    context = RequestContext(request)
    return render_to_response("account/password_changed.html", context_instance=context)


def request_password(request):
    user = request.REQUEST.get('user', '')
    auth = request.REQUEST.get('auth', '').lower()
    try:
        u = User.objects.get(username=user)
        up = u.get_profile()
        if up.reset_password_key != auth:
            return HttpResponseForbidden()
    except:
        return HttpResponseForbidden()

    if request.method == "POST":
        form = ResetPasswordForm(data=request.POST)
        if form.is_valid():
            u.set_password(form.cleaned_data['password1'])
            u.save()
            up.reset_password_key = ''
            up.save()
            return password_changed(request)
    else:
        form = ResetPasswordForm()
    context = RequestContext(request)
    return render_to_response("account/reset_password.html", {'form': form}, context_instance=context)


@login_required()
def add_user(request):
    if request.user.is_superuser:
        return register(request, True, True, reverse("list_users"), "account/add_user.html")
    else:
        return HttpResponseRedirect('/')


def register(request, admin_origin=False, is_active=False, success_url=None, template="account/registration_form.html"):

    register_complete_url = reverse('user_register_complete_email')
    if not admin_origin:
        try:
            reg = SiteRegistration.objects.get(site=Site.objects.get_current())
            if not reg.allow_registration:
                return HttpResponseForbidden()
            if reg.validation_type == REGISTRATION_ADMIN_VALIDATION:
                register_complete_url = reverse(
                    'user_register_complete_noemail')
            elif reg.validation_type == REGISTRATION_NO_VALIDATION:
                register_complete_url = reverse(
                    'user_register_complete_novalidation')
        except SiteRegistration.DoesNotExist:
            return HttpResponseForbidden()

    form = AccountRegistrationForm(data=request.POST or None)
    try:
        f = FormsForm.objects.get(slug=settings.ZORNA_USER_PROFILE_FORM)
        args = (f, request.POST or None, request.FILES or None)
        form_for_form = FormForForm(*args, instance=None)
        if f.bind_to_account:
            del form_for_form.fields['zorna_owner']
            del form_for_form.fields['zorna_owner-id']
            form_for_form.fields['zorna_owner-id'] = forms.CharField(
                widget=forms.HiddenInput(), required=False)
        lform = form_for_form
    except Exception as e:
        form_for_form = None
        lform = form

    if request.user.is_anonymous():
        lform.fields['REMOTE_ADDR'] = forms.CharField(
            widget=forms.HiddenInput(), initial=request.META['REMOTE_ADDR'])
        lform.fields['REMOTE_ADDR'] = forms.CharField(
            widget=forms.HiddenInput(), initial=request.META['REMOTE_ADDR'])
        lform.fields['captcha'] = ReCaptchaField(
            label='Captcha', required=True, widget=ReCaptchaWidget())

    if request.method == 'POST':
        if form.is_valid():
            if not form_for_form or form_for_form.is_valid():
                new_user = form.save(request, admin_origin, is_active)
                if form_for_form:
                    form_for_form.cleaned_data['zorna_owner-id'] = new_user.pk
                    form_for_form.save(request=request)
                return HttpResponseRedirect(success_url or register_complete_url)
    context = RequestContext(request)
    return render_to_response(template, {'form': form, 'form_profile': form_for_form}, context_instance=context)


@login_required()
def edit_user(request, user):
    baccess = SiteOptions.objects.is_access_valid(
        request.user, 'zorna_validate_registration')
    if request.user.is_superuser or baccess:
        try:
            form = FormsForm.objects.get(slug=settings.ZORNA_USER_PROFILE_FORM)
            try:
                entry = form.entries.get(account=user)
            except FormsFormEntry.DoesNotExist:
                entry = None
            args = (form, request.POST or None, request.FILES or None)
            form_for_form = FormForForm(*args, instance=entry or None)
            if form.bind_to_account:
                if not form_for_form.instance.id:
                    del form_for_form.fields['zorna_owner']
                    del form_for_form.fields['zorna_owner-id']
                    form_for_form.fields['zorna_owner-id'] = forms.CharField(
                        widget=forms.HiddenInput(), initial=user)
                else:
                    del form_for_form.fields['zorna_owner']
        except:
            form_for_form = None
        user = User.objects.get(pk=user)
        form = AccountEditProfileForm(user, data=request.POST or None)
        if request.method == 'POST':
            if form.is_valid():
                if not form_for_form or form_for_form.is_valid():
                    user.username = form.cleaned_data['username']
                    user.first_name = form.cleaned_data['firstname']
                    user.last_name = form.cleaned_data['lastname']
                    user.email = form.cleaned_data['email']
                    user.is_active = form.cleaned_data['is_active']
                    user.save()
                    try:
                        cal = Calendar.objects.get_calendar_for_object(
                            user, 'owner')
                        cal.name = user.get_full_name()
                        cal.slug = slugify(cal.name)
                        cal.save()
                    except Calendar.DoesNotExist:
                        pass
                    if form_for_form:
                        form_for_form.save(request=request)
                    return HttpResponseRedirect(reverse('list_users'))
        context = RequestContext(request)
        return render_to_response("account/edit_user.html", {'form': form, 'curuser': user, 'form_profile': form_for_form}, context_instance=context)
    else:
        return HttpResponseRedirect('/')


@login_required()
def edit_user_password(request, user):
    baccess = SiteOptions.objects.is_access_valid(
        request.user, 'zorna_validate_registration')
    if request.user.is_superuser or baccess:
        from zorna.account.forms import AccountEditPasswordForm
        user = User.objects.get(pk=user)
        if request.method == 'POST':
            form = AccountEditPasswordForm(data=request.POST)
            if form.is_valid():
                user.set_password(form.cleaned_data['password1'])
                user.save()
                return HttpResponseRedirect(reverse('list_users'))
            else:
                form = AccountEditPasswordForm(data=request.POST)
        else:
            form = AccountEditPasswordForm()
        context = RequestContext(request)
        return render_to_response("account/edit_password_user.html", {'form': form, 'curuser': user}, context_instance=context)
    else:
        return HttpResponseRedirect('/')


def register_complete(request):
    context = RequestContext(request)
    return render_to_response("account/registration_complete.html", context_instance=context)


def activate_account(request, activation_key):
    activation_key = activation_key.lower(
    )  # Normalize before trying anything with it.
    account = UserProfile.objects.activate_user(activation_key)
    context = RequestContext(request)
    return render_to_response('account/activate.html',
                              {'account': account,
                               'expiration_days': 4},
                              context_instance=context)


@login_required()
def list_users(request):
    baccess = SiteOptions.objects.is_access_valid(
        request.user, 'zorna_validate_registration')
    if request.user.is_superuser or baccess:
        q = request.REQUEST.get('q', '')
        if q != '':
            user_lists = User.objects.filter(Q(first_name__icontains=q) | Q(
                last_name__icontains=q))
        else:
            user_lists = User.objects.all()
        orderby = request.REQUEST.get('o', 'first_name')
        order = request.REQUEST.get('ot', 'asc')
        if order == 'desc':
            user_lists = user_lists.order_by("-%s" % orderby)
        else:
            user_lists = user_lists.order_by("%s" % orderby)
        extra_context = {'search_value': q,
                         'orderby': orderby,
                         'order': 'asc' if order == 'desc' else 'desc',
                         }
        return object_list(request, queryset=user_lists, extra_context=extra_context, template_name='account/users_list.html', paginate_by=50)
    else:
        return HttpResponseRedirect('/')


@login_required()
def list_groups(request):
    if request.user.is_superuser:
        group_lists = UserGroup.objects.filter(
            id__gt=defines.ZORNA_GROUP_REGISTERED).order_by('lft')
        return object_list(request, queryset=group_lists, template_name='account/groups_list.html')
    else:
        return HttpResponseRedirect('/')


@login_required()
def add_group(request):
    if request.user.is_superuser:
        if request.method == 'POST':
            form = UserGroupForm(request.POST)
            if form.is_valid():
                form.save()
                return HttpResponseRedirect(reverse('list_groups'))
            else:
                form = UserGroupForm(request.POST)
        else:
            form = UserGroupForm()

        context = RequestContext(request)
        extra_context = {'form': form, 'title': _("Add new group")}
        return render_to_response('account/edit_group.html', extra_context, context_instance=context)
    else:
        return HttpResponseRedirect('/')


@login_required()
def edit_group(request, group):
    if request.user.is_superuser:
        group = UserGroup.objects.get(pk=group)
        if request.method == 'POST':
            form = UserGroupForm(request.POST, instance=group)
            if form.is_valid():
                form.save()
                return HttpResponseRedirect(reverse('list_groups'))
            else:
                form = UserGroupForm(request.POST)
        else:
            form = UserGroupForm(instance=group)

        context = RequestContext(request)
        extra_context = {'form': form, 'title': _("Update group")}
        return render_to_response('account/edit_group.html', extra_context, context_instance=context)
    else:
        return HttpResponseRedirect('/')


@login_required()
def groups_user(request, user):
    if request.user.is_superuser:
        extra_context = {}
        u = User.objects.get(pk=user)
        extra_context['curuser'] = u
        up = u.get_profile()

        if request.method == 'POST' and u:
            selected = request.POST.getlist('_selected_action')
            args = [obj for obj in UserGroup.objects.filter(pk__in=selected)]
            up.groups.clear()
            up.groups.add(*args)

        group_lists = UserGroup.objects.filter(
            id__gt=defines.ZORNA_GROUP_REGISTERED).order_by('lft')
        if u:
            up = up.groups.all()
            extra_context['users_groups'] = [g.id for g in up]

        return object_list(request, queryset=group_lists, template_name='account/groups_user.html', extra_context=extra_context)
    else:
        return HttpResponseRedirect('/')


@login_required()
def users_group(request, group):
    if request.user.is_superuser:

        extra_context = {}
        g = group  # request.REQUEST.get('g', None)
        if g:
            grp = UserGroup.objects.get(pk=g)
            for ancestor in reversed(grp.get_ancestors()[2:]):
                grp.name = ancestor.name + u' / ' + grp.name
            extra_context['group_name'] = grp.name
            extra_context['group_id'] = grp.id
        else:
            grp = None
            extra_context['group_id'] = ''

        if request.method == 'POST' and grp:
            selected = request.POST.getlist('_selected_action')
            args = [obj for obj in UserProfile.objects.filter(
                user__in=selected)]
            grp.userprofile_set.clear()
            grp.userprofile_set.add(*args)
            u = request.POST.get("u", "")
            if u:
                u = User.objects.get(pk=u).get_profile()
                u.groups.add(grp)

        if grp:
            up = grp.userprofile_set.all()
            extra_context['users_groups'] = [g.user_id for g in up]
        else:
            extra_context['users_groups'] = []

        group_lists = User.objects.filter(pk__in=extra_context[
                                          'users_groups']).order_by('first_name')
        return object_list(request, queryset=group_lists, template_name='account/users_group.html', extra_context=extra_context, paginate_by=20)
    else:
        return HttpResponseRedirect('/')


@login_required()
def user_settings_intialize(request):
    extra_context = {}
    try:
        avatar_user = UserAvatar.objects.get(user=request.user)
    except UserAvatar.DoesNotExist:
        avatar_user = None
    extra_context['avatar'] = avatar_user
    return extra_context


@login_required()
def user_settings(request):
    extra_context = user_settings_intialize(request)
    try:
        form = FormsForm.objects.get(slug=settings.ZORNA_USER_PROFILE_FORM)
        try:
            entry = form.entries.get(account__id=request.user.pk)
        except FormsFormEntry.DoesNotExist:
            entry = None
    except Exception as e:
        entry = None
    if entry:
        t = loader.get_template('account/user_card.html')
        columns, row = forms_get_entry(entry)
        c = RequestContext(request, {
                           'row': row, 'columns': columns, 'account': request.user})
        extra_context['profile'] = t.render(c)
    else:
        extra_context['profile'] = ''

    context = RequestContext(request)
    return render_to_response('account/user_settings_home.html', extra_context, context_instance=context)


@login_required()
def user_settings_profile(request):
    try:
        form = FormsForm.objects.get(slug=settings.ZORNA_USER_PROFILE_FORM)
        try:
            entry = form.entries.get(account=request.user)
        except FormsFormEntry.DoesNotExist:
            entry = None
        args = (form, request.POST or None, request.FILES or None)
        form_for_form = FormForForm(*args, instance=entry or None)
        if form.bind_to_account:
            if not form_for_form.instance.id:
                del form_for_form.fields['zorna_owner']
                del form_for_form.fields['zorna_owner-id']
                form_for_form.fields['zorna_owner-id'] = forms.CharField(
                    widget=forms.HiddenInput(), initial=request.user.pk)
            else:
                del form_for_form.fields['zorna_owner']
    except:
        form_for_form = None
        form = None

    if form_for_form and request.method == 'POST' and form_for_form.is_valid():
        form_for_form.save(request=request)
        return user_settings(request)
    extra_context = user_settings_intialize(request)
    extra_context['form_for_form'] = form_for_form
    extra_context['form'] = form
    context = RequestContext(request)
    return render_to_response('account/user_profile.html', extra_context, context_instance=context)


@login_required()
def user_settings_avatar(request):
    if request.method == 'POST':
        upload_avatar_form = UserAvatarForm(request.POST, request.FILES)
        if 'bavatar' in request.POST:
            if upload_avatar_form.is_valid():
                avatar = UserAvatar(
                    user=request.user,
                )
                avatar.save()
                image_file = request.FILES['avatar']
                avatar.avatar.save(image_file.name, image_file)
        elif 'bdelavatar' in request.POST:
            try:
                UserAvatar.objects.get(user=request.user).delete()
            except:
                pass
    else:
        upload_avatar_form = UserAvatarForm()
    extra_context = user_settings_intialize(request)
    extra_context['upload_avatar_form'] = upload_avatar_form
    context = RequestContext(request)
    return render_to_response('account/user_profile_avatar.html', extra_context, context_instance=context)


@login_required()
def change_my_password(request):
    if not request.user.is_anonymous():
        from zorna.account.forms import AccountEditPasswordForm
        user = request.user
        if request.method == 'POST':
            form = AccountEditPasswordForm(data=request.POST)
            if form.is_valid():
                user.set_password(form.cleaned_data['password1'])
                user.save()
                return user_settings(request)
            else:
                form = AccountEditPasswordForm(data=request.POST)
        else:
            form = AccountEditPasswordForm()
        context = RequestContext(request)
        return render_to_response("account/change_my_password.html", {'form': form}, context_instance=context)
    else:
        return HttpResponseRedirect('/')


@login_required()
def list_administrators(request):
    if request.user.is_superuser:
        extra_context = {}
        if request.method == 'POST':
            selected = request.POST.getlist('_selected_action')
            User.objects.all().update(is_superuser=0)
            User.objects.filter(pk__in=selected).update(is_superuser=1)
            u = request.POST.get("u", "")
            if u:
                try:
                    u = User.objects.get(pk=u)
                    u.is_superuser = 1
                    u.save()
                except:
                    pass

        admin_lists = User.objects.filter(
            is_superuser=1).order_by('first_name')
        return object_list(request, queryset=admin_lists, template_name='account/users_administrators.html', extra_context=extra_context, paginate_by=20)
    else:
        return HttpResponseRedirect('/')


def form_process_import_users(request):
    tempfile = request.POST.get('tempfile', None)
    if tempfile:
        separator = request.POST.get('separator', ';')
        encoding = request.POST.get('encoding', 'UTF-8')
        try:
            csv_file = open(tempfile, 'rb')
        except Exception as e:
            return False, '%s' % e
        import csv
        nb_row = 0
        users = User.objects.all()
        registered_users = {}
        for u in users:
            registered_users[u.username] = u

        required_fields = [
            'username', 'first_name', 'last_name', 'email', 'password']
        csv_reader = csv.reader(csv_file, delimiter=smart_str(separator))
        fields = {}
        users_added = {}
        users_updated = {}
        users_error = {}
        for row in csv_reader:
            if nb_row == 0:
                for i, col in enumerate(row):
                    ft = request.POST.get('field_type_%s' % i, '')
                    if ft in required_fields:
                        fields[ft] = i

                if len(fields) != len(required_fields):
                    csv_file.close()
                    return False, _(u'You must provide required fields')
            else:
                username = smart_unicode(row[fields[
                                         'username']].strip(), encoding)
                first_name = smart_unicode(row[fields[
                                           'first_name']].strip(), encoding)
                last_name = smart_unicode(row[fields[
                                          'last_name']].strip(), encoding)
                email = smart_unicode(row[fields['email']].strip(), encoding)
                password = row[fields['password']].strip()
                if username and first_name and last_name and email and password:
                    # user already exist, update record
                    if username in registered_users:
                        user = registered_users[username]
                        user.first_name = first_name
                        user.last_name = last_name
                        user.email = email
                        user.set_password(password)
                        try:
                            user.save()
                            users_updated[nb_row + 1] = ''
                        except Exception as e:
                            users_error[nb_row + 1] = '%s' % e
                    else:
                        new_user = User.objects.create_user(
                            username, email, password)
                        new_user.is_active = True
                        new_user.first_name = first_name
                        new_user.last_name = last_name
                        try:
                            new_user.save()
                            up = new_user.get_profile()
                            up.activation_key = UserProfile.ACTIVATED
                            up.save()
                            users_added[nb_row + 1] = ''
                        except Exception as e:
                            users_error[nb_row + 1] = '%s' % e
                else:
                    users_error[nb_row + 1] = _(u'One more fields are empty')
            nb_row = nb_row + 1
        csv_file.close()
        os.unlink(tempfile)

        t = loader.get_template('account/import_results.html')
        c = RequestContext(request, {
                           'users_error': users_error, 'users_added': users_added, 'users_updated': users_updated})
        return True, t.render(c)
    else:
        return False, _(u'Invalid file name')


def form_map_import_fields(request, tempfile, separator, encoding='utf-8', message=''):
    if request.user.is_superuser:
        try:
            form_profile = FormsForm.objects.get(
                slug=settings.ZORNA_USER_PROFILE_FORM)
        except:
            form_profile = None

        import csv
        extra_context = {'message': message}
        csv_reader = csv.reader(open(
            tempfile, 'rb'), delimiter=smart_str(separator))
        for headers in csv_reader:
            break
        i = 0
        extra_context['csv_header'] = []
        for col in headers:
            extra_context['csv_header'].append({
                                               'id': i, 'label': smart_unicode(col, encoding)})
            i = i + 1
        extra_context['tempfile'] = tempfile
        extra_context['separator'] = separator
        extra_context['encoding'] = encoding

        extra_context['user_field_choices'] = [
            {'id': 'username', 'label': _(
             "Nickname")},
            {'id': 'first_name', 'label': _(
             "First name")},
            {'id': 'last_name', 'label': _(
             "Last name")},
            {'id': 'email', 'label': _(
             "E-mail address")},
            {'id': 'password', 'label': _(
             "Password")},
        ]

        extra_context['current_form'] = form_profile
        context = RequestContext(request)
        extra_context['zorna_title_page'] = _(u'Forms')
        return render_to_response('account/import_users_map_form.html', extra_context, context_instance=context)
    else:
        return HttpResponseForbidden()


@login_required()
def import_users_view(request):
    if request.user.is_superuser:
        form_csv = UsersFormImportCsv(
            request.POST or None, request.FILES or None)
        if request.method == 'POST':
            if 'tempfile' in request.POST:
                ret, message = form_process_import_users(request)
                if not ret:
                    return form_map_import_fields(request, request.POST.get('tempfile'), request.POST.get('separator'), request.POST.get('encoding'), message)
                else:
                    return HttpResponse(message)
            elif form_csv.is_valid() and 'csv_file' in request.FILES:
                import tempfile
                f = request.FILES['csv_file']
                temp = tempfile.NamedTemporaryFile(suffix='.csv', delete=False)
                for chunk in f.chunks():
                    temp.write(chunk)
                temp.close()
                delimiter = request.POST.get('other', None)
                encoding = request.POST.get('encoding', 'UTF-8')
                if not delimiter:
                    delimiter = request.POST.get('separator', ';')
                    delimiter = '\t' if delimiter == 't' else delimiter
                return form_map_import_fields(request, temp.name, delimiter, encoding)

        extra_context = {}
        context = RequestContext(request)
        extra_context['zorna_title_page'] = _(u'Users import')
        extra_context['form'] = form_csv
        return render_to_response('account/import_users_form.html', extra_context, context_instance=context)
    else:
        return HttpResponseForbidden()
