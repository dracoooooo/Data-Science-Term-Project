from django import forms
from django.utils.translation import ugettext_lazy as _
from schedule.models import Event, Occurrence
import datetime
import time

from zorna.calendars.models import ZornaCalendar, EventDetails, ZornaResourceCalendar

FREQUENCIES_CHOICES = (
    ("", _("-----")),
    ("WEEKLY", _("Weekly")),
    ("DAILY", _("Daily")),
    ("MONTHLY", _("Monthly")),
    ("YEARLY", _("Yearly")))

DAYS_CHOICES = (
    (6, _("Sun")),
    (0, _("Mon")),
    (1, _("Tue")),
    (2, _("Wed")),
    (3, _("Thu")),
    (4, _("Fri")),
    (5, _("Sat")),
)


class EditEventForm(forms.ModelForm):
    interval_choices = [(i, i) for i in range(1, 31)]

    title = forms.CharField()
    description = forms.Textarea()
    start = forms.DateTimeField(label=_(
        "Start date"), widget=forms.SplitDateTimeWidget)
    end = forms.DateTimeField(label=_("End date"), widget=forms.SplitDateTimeWidget, help_text=_(
        "The end time must be later than start time."))
    end_recurring_period = forms.DateField(label=_("Until date"), help_text=_(
        "This date is ignored for one time only events."), required=False)
    rule = forms.ChoiceField(label=_("Rule"), choices=FREQUENCIES_CHOICES, help_text=_(
        "Select '----' for a one time only event."), required=False)
    weekdays = forms.MultipleChoiceField(label=_(
        "Repeat on"), choices=DAYS_CHOICES, widget=forms.CheckboxSelectMultiple, required=False)
    interval = forms.ChoiceField(label=_(
        "Repeat every"), choices=interval_choices, required=False)

    class Meta:
        model = Event
        exclude = ('creator', 'created_on', 'calendar', 'rule')

    def clean(self):
        start = self.cleaned_data.get("start")
        end = self.cleaned_data.get("end")
        if start >= end:
            raise forms.ValidationError(_(
                u'The end time must be later than start time.'))
        return self.cleaned_data


class EditEventDetailsForm(forms.ModelForm):

    class Meta:
        model = EventDetails


class ResourceCalendarForm(forms.ModelForm):

    class Meta:
        model = ZornaResourceCalendar


class ZornaCalendarSettingsForm(forms.ModelForm):

    class Meta:
        model = ZornaCalendar
