from django.forms import ModelForm
from django import forms
from django.utils.translation import ugettext_lazy as _


from zorna.site.models import SiteAlert, SiteRegistration
from zorna.calendars.models import ZornaCalendarCategory


class SiteAlertAdminAddForm(ModelForm):

    def __init__(self, data=None, **keyw):
        super(SiteAlertAdminAddForm, self).__init__(data, **keyw)
        split_widget = forms.SplitDateTimeWidget()
        split_widget.widgets[1].attrs = {'style': 'width:80px;'}
        self.fields["start"].widget = split_widget
        self.fields["end"].widget = split_widget

    class Meta:
        model = SiteAlert

    def clean(self):
        start = self.cleaned_data.get("start")
        end = self.cleaned_data.get("end")
        if start > end:
            raise forms.ValidationError(_(
                u'The end time must be later than start time.'))
        return self.cleaned_data


class ZornaCalendarCategoryForm(ModelForm):

    class Meta:
        model = ZornaCalendarCategory


class SiteRegistrationForm(ModelForm):

    class Meta:
        model = SiteRegistration
