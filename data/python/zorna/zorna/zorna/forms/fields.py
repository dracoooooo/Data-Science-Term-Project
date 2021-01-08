from django import forms
from django.forms.extras import SelectDateWidget
from django.utils.translation import ugettext_lazy as _

from zorna.recaptcha import ReCaptchaField, ReCaptchaWidget
# Constants for all available field types.
TEXT = 1
TEXTAREA = 2
EMAIL = 3
CHECKBOX = 4
CHECKBOX_MULTIPLE = 5
SELECT = 6
SELECT_MULTIPLE = 7
RADIO_MULTIPLE = 8
FILE = 9
DATE = 10
DATE_TIME = 11
HIDDEN = 12
CAPTCHA = 13
ZORNA_USER = 14
DECIMAL = 15
INTEGER = 16
REFERENCE = 17
TIME = 18
ZORNA_USER_SINGLETON = 100
FORM_ENTRY = 101

# Names for all available field types.
NAMES = (
    (TEXT, _("Single line text")),
    (TEXTAREA, _("Multi line text")),
    (EMAIL, _("Email")),
    (CHECKBOX, _("Check box")),
    (CHECKBOX_MULTIPLE, _("Check boxes")),
    (SELECT, _("Drop down")),
    (SELECT_MULTIPLE, _("Multi select")),
    (RADIO_MULTIPLE, _("Radio buttons")),
    (FILE, _("File upload")),
    (DATE, _("Date")),
    (DATE_TIME, _("Date/time")),
    (HIDDEN, _("Hidden")),
    (CAPTCHA, _("Captcha")),
    (ZORNA_USER, _("Registered user")),
    (DECIMAL, _("Decimal")),
    (INTEGER, _("Integer")),
    (REFERENCE, _("Reference")),
    (TIME, _("Time")),
)

# Names for all available field types to be used in templates.
NAMES_TPL = {
    TEXT: 'text',
    TEXTAREA: 'textarea',
    EMAIL: 'email',
    CHECKBOX: 'checkbox',
    CHECKBOX_MULTIPLE: 'checkbox_multiple',
    SELECT: 'select',
    SELECT_MULTIPLE: 'select_multiple',
    RADIO_MULTIPLE: 'radio_multiple',
    FILE: 'file',
    DATE: 'date',
    DATE_TIME: 'date_time',
    HIDDEN: 'hidden',
    CAPTCHA: 'captcha',
    ZORNA_USER: 'zorna_user',
    DECIMAL: 'decimal',
    INTEGER: 'integer',
    REFERENCE: 'reference',
    TIME: 'time',
    ZORNA_USER_SINGLETON: 'zorna_user',
}

# Field classes for all available field types.
CLASSES = {
    TEXT: forms.CharField,
    TEXTAREA: forms.CharField,
    EMAIL: forms.EmailField,
    CHECKBOX: forms.BooleanField,
    CHECKBOX_MULTIPLE: forms.MultipleChoiceField,
    SELECT: forms.ChoiceField,
    SELECT_MULTIPLE: forms.MultipleChoiceField,
    RADIO_MULTIPLE: forms.ChoiceField,
    FILE: forms.FileField,
    DATE: forms.DateField,
    DATE_TIME: forms.DateTimeField,
    HIDDEN: forms.CharField,
    CAPTCHA: ReCaptchaField,
    ZORNA_USER: forms.CharField,
    DECIMAL: forms.DecimalField,
    INTEGER: forms.IntegerField,
    REFERENCE: forms.CharField,
    TIME: forms.TimeField,
}

# Widgets for field types where a specialised widget is required.
WIDGETS = {
    TEXTAREA: forms.Textarea,
    CHECKBOX_MULTIPLE: forms.CheckboxSelectMultiple,
    RADIO_MULTIPLE: forms.RadioSelect,
    DATE: forms.DateInput,
    DATE_TIME: forms.SplitDateTimeWidget,
    HIDDEN: forms.HiddenInput,
    CAPTCHA: ReCaptchaWidget,
}

# Some helper groupings of field types.
LISTS = (CHECKBOX_MULTIPLE, SELECT, SELECT_MULTIPLE, RADIO_MULTIPLE)
CHOICES = (CHECKBOX, CHECKBOX_MULTIPLE,
           SELECT, SELECT_MULTIPLE, RADIO_MULTIPLE)
MULTIPLE_CHOICES = (CHECKBOX_MULTIPLE, SELECT_MULTIPLE, RADIO_MULTIPLE)

DATES = (DATE, DATE_TIME)

STYLED_CONTROLS = (TEXT, TEXTAREA, EMAIL, SELECT, SELECT_MULTIPLE,
                   DATE_TIME, ZORNA_USER, DECIMAL, INTEGER, REFERENCE)
