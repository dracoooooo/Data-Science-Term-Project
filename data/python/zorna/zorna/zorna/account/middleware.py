import datetime


class ActivityMiddleware(object):

    def process_request(self, request):
        if request.user.is_authenticated():
            now = datetime.datetime.now()
            profile = request.user.get_profile()
            if profile.last_activity is None:
                # workaround for profiles not having last_active set
                try:
                    profile.last_activity = now
                    profile.save()
                except:
                    pass
            # To minimize database writes, the last_activity field is only
            # updated once per day
            if profile.last_activity is not None and profile.last_activity.date() < now.date():
                # update last_activity
                profile.update_activity()
