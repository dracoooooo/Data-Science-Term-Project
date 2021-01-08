from django import forms
from django.utils.translation import ugettext_lazy as _

from ckeditor.widgets import CKEditorWidget
from zorna.site.models import SiteOptions


class PageEditTemplateForm(forms.Form):
    # body = forms.CharField(_(u'body'), widget=CKEditorWidget())
    body = forms.CharField(widget=forms.Textarea(
        attrs={'rows': 25, 'cols': 100, }))


class PageEditFileForm(forms.Form):

    def __init__(self, *args, **kwargs):
        extra = kwargs.pop('extra')
        request = kwargs.pop('request')
        super(PageEditFileForm, self).__init__(*args, **kwargs)
        ckeditor_config_name = SiteOptions.objects.get_ckeditor_config(request)

        for b in extra:
            self.fields['%s' % b['block']] = forms.CharField(
                widget=CKEditorWidget(config_name=ckeditor_config_name), initial=b['content'])


class PageEditFileContextForm(forms.Form):
    title = forms.CharField(_(
        u'title'), widget=forms.TextInput(attrs={'size': '80'}))
    keywords = forms.CharField(_(
        u'keywords'), widget=forms.TextInput(attrs={'size': '80'}))
    description = forms.CharField(_(u'description'), widget=forms.Textarea(
        attrs={'rows': '5', 'cols': '80'}), required=False)

    def __init__(self, *args, **kwargs):
        extra = kwargs.pop('extra')
        super(PageEditFileContextForm, self).__init__(*args, **kwargs)

        for f, c in extra.items():
            self.fields['%s' % f] = forms.CharField(initial=c)
