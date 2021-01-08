from django.conf.urls.defaults import url, patterns

from zorna.menus import views


urlpatterns = patterns('',
                       url(r'^home/$',
                           views.menus_home_view,
                           name='menus_home_view'),
                       url(r'^add/url/$',
                           views.menus_add_item_url,
                           name='menus_add_item_url'),
                       url(r'^add/article_category/$',
                           views.menus_add_item_article_category,
                           name='menus_add_item_article_category'),
                       url(r'^add/faq/$',
                           views.menus_add_item_faq,
                           name='menus_add_item_faq'),
                       url(r'^add/form/submission/$',
                           views.menus_add_item_form_submission,
                           name='menus_add_item_form_submission'),
                       url(r'^add/form/browse/$',
                           views.menus_add_item_form_browse,
                           name='menus_add_item_form_browse'),
                       url(r'^add/page/$',
                           views.menus_add_item_page_content,
                           name='menus_add_item_page_content'),
                       url(r'^edit/item/(?P<item_id>\d+)/$',
                           views.menus_edit_item,
                           name='menus_edit_item'),
                       )
