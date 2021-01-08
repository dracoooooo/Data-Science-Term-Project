import django.dispatch
form_entry_post_save = django.dispatch.Signal(providing_args=['cols', 'rows'])
