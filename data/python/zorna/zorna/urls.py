import os
from django.conf import settings
from django.conf.urls.defaults import patterns, include


urlpatterns = []

if settings.DEBUG:
    try:
        plugins_path = os.path.join(settings.PROJECT_PATH, 'zorna_plugins')
        for app in os.listdir(plugins_path):
            app_path = os.path.join(plugins_path, app)
            if os.path.isdir(app_path):
                urlpatterns += patterns('',
                    (r'^pmedia/plugin/%s/(?P<path>.*)$' % app, 'django.views.static.serve',
                     {'document_root':     os.path.join(app_path, 'media') }),
                     )
    except:
        pass

    urlpatterns += patterns('',
        (r'^zmedia/(?P<path>.*)$', 'django.views.static.serve',
         {'document_root':     settings.MEDIA_ROOT}),
        (r'^avatars/(?P<path>.*)$', 'django.views.static.serve',
         {'document_root':     os.path.join(settings.ZORNA_UPLOAD_PATH, 'avatars') }),
        (r'^zckeditor/(?P<path>.*)$', 'django.views.static.serve',
         {'document_root':     os.path.join(settings.PROJECT_PATH, 'public') }),
        (r'^images/ckeditor/(?P<path>.*)$', 'django.views.static.serve',
         {'document_root':     os.path.join(settings.ZORNA_UPLOAD_PATH, 'ickeditor') }),
        (r'^images/(?P<path>.*)$', 'django.views.static.serve',
         {'document_root':     os.path.join(settings.PROJECT_PATH, 'public/images') }),
        (r'skins/(?P<path>.*)$', 'django.views.static.serve',
         {'document_root':     os.path.join(settings.PROJECT_PATH, 'public/skins') }),
    )

urlpatterns += patterns('',
                        ('^activity/', include('actstream.urls')),
                        )

execfile(os.path.join(settings.PROJECT_PATH, 'zorna/urls.py'))
