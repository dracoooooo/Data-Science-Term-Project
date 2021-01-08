from django.forms import ModelForm
from django.template.defaultfilters import slugify
from django import forms
from django.utils.translation import ugettext_lazy as _
from ckeditor.widgets import CKEditorWidget
from zorna.faq.models import FaqCategory, Faq, FaqQuestion
from zorna.acl.models import get_allowed_objects
from zorna.site.models import SiteOptions


class FaqForm(ModelForm):

    class Meta:
        model = Faq
        exclude = ('sort_order',)

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
                u'You must provide a slug or a category name'))


class FaqQuestionCategoryForm(ModelForm):

    class Meta:
        model = FaqCategory
        exclude = ('sort_order',)

    def __init__(self, request, *args, **kwargs):
        super(FaqQuestionCategoryForm, self).__init__(*args, **kwargs)
        allowed_objects = get_allowed_objects(request.user, Faq, 'manager')
        self.fields['faq'].queryset = Faq.objects.filter(
            pk__in=allowed_objects)

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
                u'You must provide a slug or a category name'))


class FaqQuestionForm(ModelForm):
    question = forms.CharField(label=_(u'Question'), widget=forms.Textarea(
        attrs={'rows': '5', 'cols': '80'}))

    class Meta:
        model = FaqQuestion
        exclude = ('sort_order', 'slug')

    def __init__(self, request, faq, *args, **kwargs):
        super(FaqQuestionForm, self).__init__(*args, **kwargs)
        self.fields['category'].queryset = FaqCategory.objects.filter(faq=faq)
        self.fields['answer'] = forms.CharField(label=_(u'answer'), widget=CKEditorWidget(config_name=SiteOptions.objects.get_ckeditor_config(request)))
