import os
from django.conf import settings
from django import forms
from django.utils.translation import ugettext_lazy as _
from django.contrib.auth.models import User
from django.forms import ModelForm
from django.contrib.sites.models import Site
from django.template.loader import render_to_string
from django.core.urlresolvers import reverse
from django.db.models import Q
from mptt.forms import TreeNodeChoiceField

from zorna.account.models import UserProfile, UserAvatar
from zorna.account.models import UserGroup
from zorna.site.models import SiteRegistration, REGISTRATION_NO_VALIDATION, \
    REGISTRATION_EMAIL_VALIDATION, REGISTRATION_ADMIN_VALIDATION
from zorna.site.email import ZornaEmail


class AccountEditPasswordForm(forms.Form):
    password1 = forms.CharField(widget=forms.PasswordInput(render_value=False),
                                label=_(u'Password'))
    password2 = forms.CharField(widget=forms.PasswordInput(render_value=False),
                                label=_(u'Password (again)'))

    def clean(self):
        """
        Verify that the 2 passwords fields are equal
        """
        if 'password1' in self.cleaned_data and 'password2' in self.cleaned_data:
            if self.cleaned_data['password1'] != self.cleaned_data['password2'] or self.cleaned_data['password2'] == '':
                raise forms.ValidationError(_(
                    u'You must type the same password each time'))
        else:
            raise forms.ValidationError(_(u'You must type password'))
        return self.cleaned_data


class AccountEditProfileForm(forms.Form):
    username = forms.RegexField(regex=r'^\w+$',
                                max_length=30,
                                widget=forms.TextInput(attrs={'size': 80}), label=_(u'Username'))
    email = forms.EmailField(widget=forms.TextInput(
        attrs={'size': 80}), label=_(u'Email address'))
    firstname = forms.CharField(widget=forms.TextInput(
        attrs={'size': 80}), label=_(u'Firstname'))
    lastname = forms.CharField(widget=forms.TextInput(
        attrs={'size': 80}), label=_(u'Lastname'))
    is_active = forms.BooleanField(label=_('is active'), required=False, help_text=_(
        "Designates whether this user should be treated as active. Unselect this instead of deleting accounts."))

    def __init__(self, user, *args, **kwargs):
        super(AccountEditProfileForm, self).__init__(*args, **kwargs)
        self.user = user
        self.initial['email'] = user.email
        self.initial['firstname'] = user.first_name
        self.initial['lastname'] = user.last_name
        self.initial['username'] = user.username
        self.initial['is_active'] = user.is_active

    def clean_username(self):
        """
        Validate that the username is alphanumeric and is not already
        in use.

        """
        username = self.cleaned_data.get("username")
        if not set(username).issubset("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_"):
            raise forms.ValidationError(_(
                "That username has invalid characters. The valid values are letters, numbers and underscore."))
        try:
            user = User.objects.get(
                username__iexact=self.cleaned_data['username'])
        except User.DoesNotExist:
            return self.cleaned_data['username']
        if self.user and self.user.pk == user.pk:
            return self.cleaned_data['username']
        raise forms.ValidationError(_(
            u'This username is already taken. Please choose another.'))


class AccountRegistrationForm(forms.Form):

    username = forms.RegexField(regex=r'^\w+$',
                                max_length=30,
                                widget=forms.TextInput(attrs={'size': 80}), label=_(u'Login ID'))
    password1 = forms.CharField(widget=forms.PasswordInput(render_value=False),
                                label=_(u'Password'))
    password2 = forms.CharField(widget=forms.PasswordInput(render_value=False),
                                label=_(u'Password (again)'))
    firstname = forms.CharField(widget=forms.TextInput(
        attrs={'size': 80}), label=_(u'Firstname'))
    lastname = forms.CharField(widget=forms.TextInput(
        attrs={'size': 80}), label=_(u'Lastname'))
    email = forms.EmailField(widget=forms.TextInput(
        attrs={'size': 80}), label=_(u'Email address'))

    def clean_username(self):
        """
        Validate that the username is alphanumeric and is not already
        in use.

        """
        username = self.cleaned_data.get("username")
        if not set(username).issubset("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_"):
            raise forms.ValidationError(_(
                "That username has invalid characters. The valid values are letters, numbers and underscore."))
        try:
            User.objects.get(username__iexact=self.cleaned_data['username'])
        except User.DoesNotExist:
            return self.cleaned_data['username']
        raise forms.ValidationError(_(
            u'This username is already taken. Please choose another.'))

    def clean(self):
        """
        Verify that the 2 passwords fields are equal
        """
        if 'password1' in self.cleaned_data and 'password2' in self.cleaned_data:
            if self.cleaned_data['password1'] != self.cleaned_data['password2']:
                raise forms.ValidationError(_(
                    u'You must type the same password each time'))
        return self.cleaned_data

    def save(self, request, admin_origin, is_active, *args, **kwargs):
        """
        Create the new ``User`` and his profile
        """
        current_site = Site.objects.get_current()
        try:
            reg = SiteRegistration.objects.get(site=current_site)
        except SiteRegistration.DoesNotExist:
            reg = None

        if reg and reg.validation_type == REGISTRATION_NO_VALIDATION:
            is_active = True

        new_user = User.objects.create_user(self.cleaned_data[
                                            'username'], self.cleaned_data['email'], self.cleaned_data['password1'])
        new_user.is_active = is_active
        new_user.first_name = self.cleaned_data['firstname']
        new_user.last_name = self.cleaned_data['lastname']
        new_user.save()
        up = new_user.get_profile()
        if admin_origin or (reg and reg.validation_type == REGISTRATION_NO_VALIDATION):
            up.activation_key = UserProfile.ACTIVATED
        up.save()
        if reg:
            groups = reg.groups.all()
            if groups:
                args = [obj for obj in groups]
                up.groups.clear()
                up.groups.add(*args)

        if admin_origin is False and reg:
            ec = {'activation_key': up.activation_key,
                  'expiration_days': settings.ACCOUNT_ACTIVATION_DAYS,
                  'site': current_site,
                  'user': new_user, 'mime_type': 'text',
                  'url': request.build_absolute_uri(reverse('account_activate', args=[up.activation_key]))
                  }
            if reg.validation_type == REGISTRATION_EMAIL_VALIDATION:
                subject = _("Account activation on") + ' ' + current_site.name
                body_text = render_to_string(
                    'account/activation_email.html', ec)
                ec['mime_type'] = 'html'
                body_html = render_to_string(
                    'account/activation_email.html', ec)
                email = ZornaEmail()
                email.append(subject, body_text, body_html, settings.DEFAULT_FROM_EMAIL, [
                             new_user.email])
                email.send()
            elif reg.validation_type == REGISTRATION_ADMIN_VALIDATION:
                from zorna.site.models import SiteOptions
                email_lists = SiteOptions.objects.get_authorized_user(
                    'zorna_validate_registration')
                if email_lists:
                    subject = _("Your site has recorded a new registration")
                    body_text = render_to_string(
                        'account/registration_email.html', ec)
                    ec['mime_type'] = 'html'
                    body_html = render_to_string(
                        'account/registration_email.html', ec)
                    email = ZornaEmail()
                    email.append(subject, body_text, body_html, settings.DEFAULT_FROM_EMAIL, [
                                 u.email for u in email_lists])
                    email.send()
        return new_user


class UserGroupForm(ModelForm):
    parent = TreeNodeChoiceField(queryset=UserGroup.objects.filter(
        id__gte=3).order_by('lft'), required=True)  # @UndefinedVariable
    name = forms.CharField(max_length=80,
                           widget=forms.TextInput({'size': 100})
                           )

    class Meta:
        model = UserGroup


class UserAvatarForm(forms.ModelForm):

    class Meta:
        exclude = ('user',)
        model = UserAvatar

    def clean_avatar(self):
        data = self.cleaned_data['avatar']
        if data:
            (root, ext) = os.path.splitext(data.name.lower())
            allowed_extensions = ['.jpg', '.jpeg', '.png', '.gif']
            if ext not in allowed_extensions:
                raise forms.ValidationError(
                    _(u"%(ext)s is an invalid file extension. Authorized extensions are : %(valid_exts_list)s") %
                    {'ext': ext, 'valid_exts_list': ", ".join(allowed_extensions)})
        else:
            raise forms.ValidationError(_(
                u"Please, choose a valid image file"))
        return data

SEPARATOR_CHOICES = (
    (";", _("Semicolon")),
    (",", _("Comma")),
    ("tab", _("Tabulation")))

ENCODING_CHOICES = (
    ("UTF-8", _("UTF-8")),
    ("ISO-8859-1", _("ISO-8859-1")),)


class UsersFormImportCsv(forms.Form):
    csv_file = forms.CharField(label=_(
        u'File'), widget=forms.FileInput(attrs={'size': '80'}))
    separator = forms.ChoiceField(label=_(
        "Separator"), choices=SEPARATOR_CHOICES, required=True)
    other = forms.CharField(widget=forms.TextInput(attrs={
                            'size': 1}), label=_(u'Other'), max_length=1, required=False)
    encoding = forms.ChoiceField(label=_(
        "Encoding"), choices=ENCODING_CHOICES, required=True)


class RequestNewPasswordForm(forms.Form):
    username = forms.CharField(widget=forms.TextInput(attrs={
                               'size': '80'}), label=_(u'Username'), help_text=_(u'Username or email address'))

    def clean(self):
        """
        Verify that user with email or username field exist
        """
        try:
            User.objects.get(Q(username=self.cleaned_data[
                'username']) | Q(email=self.cleaned_data['username']))
        except User.DoesNotExist:
            raise forms.ValidationError(_(u'Unkown username or email'))
        except:
            raise forms.ValidationError(_(u'Invlaid username or email'))
        return self.cleaned_data


class ResetPasswordForm(forms.Form):
    password1 = forms.CharField(widget=forms.PasswordInput(render_value=False),
                                label=_(u'Password'))
    password2 = forms.CharField(widget=forms.PasswordInput(render_value=False),
                                label=_(u'Password (again)'))

    def clean(self):
        """
        Verify that the 2 passwords fields are equal
        """
        if 'password1' in self.cleaned_data and 'password2' in self.cleaned_data:
            if self.cleaned_data['password1'] != self.cleaned_data['password2']:
                raise forms.ValidationError(_(
                    u'You must type the same password each time'))
        return self.cleaned_data
