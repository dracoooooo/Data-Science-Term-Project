from django.conf.urls.defaults import url, patterns

from zorna.communities import views

urlpatterns = patterns('',
                       url(r'^list/$',
                           views.admin_list_communities,
                           name='admin_list_communities'),
                       url(r'^list/communities/$',
                           views.list_communities,
                           name='list_communities'),
                       url(r'^user/list/communities/$',
                           views.user_list_communities,
                           name='user_list_communities'),
                       url(r'^activity/$',
                           views.last_activity_communities,
                           name='last_activity_communities'),
                       url(r'^(?P<comname>\S+)/join/(?P<community>\d+)/$',
                           views.join_community,
                           name='join_community'),
                       url(r'^(?P<comname>\S+)/leave/(?P<community>\d+)/$',
                           views.leave_community,
                           name='leave_community'),
                       url(r'^add/$',
                           views.admin_add_community,
                           name='admin_add_community'),
                       url(r'^edit/(?P<community>\d+)/$',
                           views.admin_edit_community,
                           name='admin_edit_community'),
                       url(r'^manage/members/(?P<community>\d+)/$',
                           views.manage_community_members,
                           name='manage_community_members'),
                       url(r'^manage/managers/(?P<community>\d+)/$',
                           views.manage_community_managers,
                           name='manage_community_managers'),
                       url(r'^community/members/(?P<community>\d+)/$',
                           views.community_members,
                           name='community_members'),
                       url(r'^community/managers/(?P<community>\d+)/$',
                           views.community_managers,
                           name='community_managers'),
                       url(r'^message/send_message/$',
                           views.user_send_message,
                           name='user_send_message'),
                       url(r'^message/send_reply/$',
                           views.user_send_reply,
                           name='user_send_reply'),
                       url(r'^message/update_message/$',
                           views.user_update_reply,
                           name='user_update_reply'),
                       url(r'^message/delete_reply/$',
                           views.user_delete_reply,
                           name='user_delete_reply'),
                       url(r'^message/delete_message/$',
                           views.user_delete_message,
                           name='user_delete_message'),
                       url(r'^message/check_messages_ajax/$',
                           views.check_messages_ajax,
                           name='check_messages_ajax'),
                       url(r'^message/follow_message_ajax/$',
                           views.follow_message_ajax,
                           name='follow_message_ajax'),
                       url(r'^message/unfollow_message_ajax/$',
                           views.unfollow_message_ajax,
                           name='unfollow_message_ajax'),
                       url(r'^message/getfile/(?P<msg>\d+)/(?P<filename>\S+)$',
                           views.get_file,
                           name='get_file'),
                       url(r'^member/profile/(?P<member>\d+)/$',
                           views.member_profile,
                           name='member_profile'),
                       url(r'^invite/member/(?P<community_id>\d+)/$',
                           views.invite_community_member,
                           name='invite_community_member'),
                       url(r'^invite/list/users/(?P<community_id>\d+)/$',
                           views.invite_list_users,
                           name='invite_list_users'),
                       url(r'^files/$',
                           views.communities_home_files,
                           name='communities_home_files'),
                       url(r'^members/$',
                           views.communities_home_members,
                           name='communities_home_members'),
                       url(r'^poll/vote/(?P<poll>\d+)$',
                           views.communities_poll_vote,
                           name='communities_poll_vote'),
                       url(r'^plugin/home/(?P<id>\S+)/$',
                           views.communities_home_plugin,
                           name='communities_home_plugin'),
                       url(r'^plugin/edit/(?P<id>\S+)/(?P<instance_id>\d+)/$',
                           views.communities_edit_plugin,
                           name='communities_edit_plugin'),
                       url(r'^form/(?P<tab>\S+)/$',
                           views.get_json_tab,
                           name='get_json_tab'),
                       url(r'^dashboard/$',
                           views.community_dashboard,
                           name='community_dashboard'),
                       url(r'^dashboard/(?P<community_id>\d+)/$',
                           views.community_dashboard,
                           name='community_dashboard'),
                       url(r'^$',
                           views.communities_home_page,
                           name='communities_home_page'),
                       )