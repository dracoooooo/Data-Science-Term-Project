from django.conf.urls.defaults import url, patterns

from zorna.articles import views
from zorna.articles.feeds import ArticleCategoryFeed


urlpatterns = patterns('',
                       url(r'^tags/list/$',
                           views.admin_list_articles_tags,
                           name='admin_list_articles_tags'),
                       url(r'^tags/add/$',
                           views.admin_add_articles_tag,
                           name='admin_add_articles_tag'),
                       url(r'^tags/edit/(?P<tag>\d+)/$',
                           views.admin_edit_articles_tag,
                           name='admin_edit_articles_tag'),
                       url(r'^categories/list/$',
                           views.admin_list_categories,
                           name='admin_list_categories'),
                       url(r'^categories/add/$',
                           views.admin_add_category,
                           name='admin_add_category'),
                       url(r'^categories/edit/(?P<category>\d+)/$',
                           views.admin_edit_category,
                           name='admin_edit_category'),
                       url(r'^categories/order/$',
                           views.admin_order_categories,
                           name='admin_order_categories'),
                       url(r'^category/(?P<category>\d+)/$',
                           views.view_category,
                           name='view_category'),
                       url(r'^story/add/$',
                           views.add_new_story,
                           name='add_new_story'),
                       url(r'^story/edit/(?P<story>\d+)/$',
                           views.edit_story,
                           name='edit_story'),
                       url(r'^story/image/(?P<story>\d+)/$',
                           views.get_story_image,
                           name='get_story_image'),
                       url(r'^story/image/(?P<story>\d+)/(?P<size>\S+)/$',
                           views.get_story_image,
                           name='get_story_image'),
                       url(r'^story/file/(?P<file_id>\d+)/$',
                           views.get_story_file,
                           name='get_story_file'),
                       url(r'^story/(?P<category>\d+)/(?P<story>\d+)/(?P<slug>\S+)$',
                           views.view_story,
                           name='view_story'),
                       url(r'^story/comment/(?P<story>\d+)/$',
                           views.add_story_comment,
                           name='add_story_comment'),
                       url(r'^writer/stories/$',
                           views.writer_stories_list,
                           name='writer_stories_list'),
                       url(r'^category/(?P<category_id>\d+)/rss/$', ArticleCategoryFeed(), name='article_category_feed'),
                       )
