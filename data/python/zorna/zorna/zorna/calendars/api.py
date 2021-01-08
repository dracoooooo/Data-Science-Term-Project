from datetime import date
from django.contrib.auth.models import User
from schedule.models.events import EventRelation
from schedule.periods import Period
from zorna.acl.models import get_allowed_objects
from zorna.calendars.models import ZornaCalendar

from zorna.calendars.models import EventDetails
from zorna.communities.models import Community
from zorna.communities.api import get_communities
from zorna.site.models import SiteOptions


def get_user_calendars(user, permissions=['viewer']):
    allowed_objects = set([])
    for perm in permissions:
        objects = get_allowed_objects(user, ZornaCalendar, perm)
        allowed_objects = allowed_objects.union(set(objects))
    b_personal_calendar = SiteOptions.objects.is_access_valid(
        user, 'zorna_personal_calendar')
    if b_personal_calendar:
        pcal = get_personal_calendar(User.objects.get(pk=user.pk))
        allowed_objects.update([pcal.pk])

    communities = get_communities(user)
    for com in communities:
        if com.enable_calendar:
            cal = com.get_calendar()
            allowed_objects.update([cal.pk])

    allowed_objects = ZornaCalendar.objects.filter(pk__in=allowed_objects)
    return allowed_objects


def get_personal_calendar(user):
    user = User.objects.get(pk=user.pk)
    return ZornaCalendar.objects.get_or_create_calendar_for_object(user, user, distinction='owner', name=user.get_full_name())


def get_resource_calendar(resource):
    return ZornaCalendar.objects.get_or_create_calendar_for_object(None, resource, distinction='owner', name=resource.name)


def delete_event(event):
    instance_event_details = EventDetails.objects.get_eventdetails_for_object(
        event)
    instance_event_details.delete()
    event.delete()
    return True


def get_date_status(value):
        """
        For date values that are tomorrow, today or yesterday compared to
        present day returns representing string. Otherwise, returns an empty string or overdue string
        if date value is late.
        """
        try:
            value = date(value.year, value.month, value.day)
        except AttributeError:
            # Passed value wasn't a date object
            ret = ''
        except ValueError:
            # Date arguments out of range
            ret = ''
        delta = value - date.today()
        if delta.days == 0:
            ret = 'today'
        elif delta.days == 1:
            ret = 'tomorrow'
        elif delta.days < 0:
            ret = 'overdue'
        else:
            ret = ''

        return ret


def get_events_for_object(obj, start_date, end_date):
    occurrences = []
    evt = EventRelation.objects.get_events_for_object(obj)
    period = Period(events=evt, start=start_date, end=end_date)
    for o in period.occurrences:
        if period.classify_occurrence(o):
            if o.id:
                o.details = EventDetails.objects.get_eventdetails_for_object(o)
            else:
                o.details = EventDetails.objects.get_eventdetails_for_object(
                    o.event)
            occurrences.append(o)

    events_list = []
    for occ in occurrences:
        ed = {}
        if occ.start.time().__str__() == '00:00:00' and occ.end.time().__str__() == '00:00:00':
            ed['allday'] = True
        else:
            ed['allday'] = False

        ed['title'] = occ.title
        ed['start'] = occ.start
        ed['end'] = occ.end
        ed['status'] = get_date_status(
            occ.start)  # 'overdue', 'today' , 'tomorrow' or ''
        ed['description'] = occ.description
        ed['author'] = User.objects.get(
            pk=occ.event.creator_id).get_full_name()
        if occ.details.bgcolor:
            ed['backgroundColor'] = occ.details.bgcolor
        elif occ.details.category:
            ed['backgroundColor'] = occ.details.category.bgcolor
            ed['category'] = occ.details.category.name
        else:
            ed['backgroundColor'] = False
            ed['category'] = ''

        events_list.append(ed)
    return events_list
