from django.forms import ModelForm
from django.template.defaultfilters import slugify
from django.utils.translation import ugettext_lazy as _
from django import forms
from tagging.forms import TagField

from mptt.forms import TreeNodeChoiceField

from zorna.fileman.models import ZornaFile, ZornaFolder


class ZornaFolderForm(ModelForm):
    parent = TreeNodeChoiceField(
        queryset=ZornaFolder.tree.filter(), required=False, empty_label="-- Nothing --")
    name = forms.CharField(label=_(
        u'Name'), widget=forms.TextInput(attrs={'size': '80'}))
    slug = forms.CharField(label=_(
        u'Slug'), widget=forms.TextInput(attrs={'size': '80'}))

    class Meta:
        model = ZornaFolder

    def clean_slug(self):
        if 'slug' in self.cleaned_data and 'name' in self.cleaned_data:
            if self.cleaned_data['slug'] != '':
                pass
            else:
                self.cleaned_data['slug'] = self.cleaned_data['name']
            self.cleaned_data['slug'] = slugify(self.cleaned_data['slug'])
            return self.cleaned_data['slug']
        else:
            raise forms.ValidationError(_(
                u'You must provide a slug or a folder name'))


class ZornaFileForm(ModelForm):
    file = forms.CharField(label=_(
        u'File'), required=False, widget=forms.FileInput(attrs={'size': '80'}))
    description = forms.CharField(label=_(
        u'Description'), widget=forms.TextInput(attrs={'size': '80'}))

    class Meta:
        model = ZornaFile


class ZornaFileAddForm(ModelForm):
    file = forms.CharField(label=_(
        u'File'), widget=forms.FileInput(attrs={'size': '80'}))
    description = forms.CharField(label=_(
        u'Description'), widget=forms.TextInput(attrs={'size': '80'}))
    tags = TagField(required=False, widget=forms.TextInput(
        attrs={'class': 'zorna-tags'}))

    class Meta:
        model = ZornaFile
        fields = ('file', 'description', 'tags')


class ZornaFileUploadForm(ModelForm):
    file = forms.CharField(label=_(
        u'File'), widget=forms.FileInput(attrs={'size': '80'}))

    class Meta:
        model = ZornaFile
        fields = ('file',)
