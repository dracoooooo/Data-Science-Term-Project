from django.utils.translation import ugettext_lazy as _
from django import forms


class SearchForm(forms.Form):
    q = forms.CharField(required=False, label=_(
        'Search'), widget=forms.TextInput(attrs={'size': 40}))
