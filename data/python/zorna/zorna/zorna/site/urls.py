from django.conf.urls.defaults import url, patterns

from zorna.site import views


urlpatterns = patterns('',
                       url(r'^options/$',
                           views.admin_list_options,
                           name='admin_list_options'),                       
                       url(r'^registration/$',
                           views.admin_site_registration,
                           name='admin_site_registration'),                       
                       url(r'^version/$',
                           views.admin_site_version,
                           name='admin_site_version'),                       
                       url(r'^alerts/$',
                           views.admin_list_alerts,
                           name='admin_list_alerts'),                       
                       url(r'^alerts/add/$',
                           views.admin_add_alert,
                           name='admin_add_alert'),
                       url(r'^edit/(?P<alert>\d+)/$',
                           views.admin_edit_alert,
                           name='admin_edit_alert'),
                       url(r'^calendar/categories/$',
                           views.admin_list_calendar_categories,
                           name='admin_list_calendar_categories'),                       
                       url(r'^calendar/categories/add/$',
                           views.admin_add_calendar_category,
                           name='admin_add_calendar_category'),
                       url(r'^calendar/categories/edit/(?P<category>\d+)/$',
                           views.admin_edit_calendar_category,
                           name='admin_edit_calendar_category'),
                       )
