import datetime
from django import template
from django.template import TemplateSyntaxError
from django.contrib.auth.models import User
from django.template import Variable
from django.core.urlresolvers import reverse
from schedule.models.events import EventRelation
from schedule.periods import Period

from zorna.acl.models import get_allowed_objects
from zorna.calendars.models import EventDetails, ZornaResourceCalendar
from zorna.calendars.api import get_resource_calendar, get_events_for_object, get_user_calendars


register = template.Library()


def get_last_day_of_month(year, month):
    if (month == 12):
        year += 1
        month = 1
    else:
        month += 1
    return datetime.date(year, month, 1) - datetime.timedelta(1)


def month_cal(context, year, month):
    request = context['request']
    ret = []
    if request.user.is_authenticated():
        evt = EventRelation.objects.get_events_for_object(request.user)
        period = Period(events=evt, start=datetime.datetime(year, month, 1),
                        end=datetime.datetime(year, month, 30))
        occurrences = []
        for o in period.occurrences:
            if period.classify_occurrence(o):
                occurrences.append(o)

    first_day_of_month = datetime.date(year, month, 1)
    last_day_of_month = get_last_day_of_month(year, month)
    first_day_of_calendar = first_day_of_month - \
        datetime.timedelta(first_day_of_month.weekday())
    last_day_of_calendar = last_day_of_month + \
        datetime.timedelta(7 - last_day_of_month.weekday())

    month_cal = []
    week = []
    week_headers = []

    i = 0
    day = first_day_of_calendar
    while day <= last_day_of_calendar:
        if i < 7:
            week_headers.append(day)
        cal_day = {}
        cal_day['day'] = day
        cal_day['event'] = False
        for occ in ret:
            if day >= occ.start.date() and day <= occ.end.date():
                cal_day['event'] = True
        if day.month == month:
            cal_day['in_month'] = True
        else:
            cal_day['in_month'] = False
        week.append(cal_day)
        if day.weekday() == 6:
            month_cal.append(week)
            week = []
        i += 1
        day += datetime.timedelta(1)

    return {'calendar': month_cal, 'headers': week_headers}

register.inclusion_tag(
    'calendars/month_cal.html', takes_context=True)(month_cal)


@register.tag(name="user_calendar_events")
def user_calendar_events(parser, token):
    '''
    {% calendar_events 'date' 'limit' as events %}
    return events for current user
    date must in format as yyyy-mm-dd or empty for now
    limit must be in fomat as x,y where x is the number of days to take in account before date
    and y the nulber of days after date
    '''
    bits = token.split_contents()
    if 5 != len(bits):
        raise TemplateSyntaxError('%r expects 4 arguments' % bits[0])
    if bits[-2] != 'as':
        raise TemplateSyntaxError(
            '%r expects "as" as the third argument' % bits[0])
    cal_date = bits[1]
    limit = bits[2]
    varname = bits[-1]
    return user_calendar_events_node(cal_date, limit, varname)


class user_calendar_events_node(template.Node):

    def __init__(self, cal_date, limit, var_name):
        sd = cal_date[1:-1]
        if sd:
            sd = map(int, sd.split('-'))
            start_date = datetime.datetime(sd[0], sd[1], sd[2])
        else:
            start_date = datetime.datetime.today()

        limits = limit[1:-1].split(',')
        self.start_date = start_date - datetime.timedelta(days=int(limits[0]))
        self.end_date = start_date + datetime.timedelta(days=int(limits[1]))
        self.var_name = var_name

    def render(self, context):
        request = context['request']
        events_list = []
        if request.user.is_authenticated():
            events_list = get_events_for_object(
                User.objects.get(pk=request.user.pk),
                self.start_date, self.end_date)
        context[self.var_name] = events_list
        return ''


@register.tag(name="resource_calendar_events")
def resource_calendar_events(parser, token):
    '''
    {% resource_calendar_events 'id' 'start_date' 'end_date' as events %}
    return events for current user
    dates must in format as yyyy-mm-dd
    '''
    bits = token.split_contents()
    if 6 != len(bits):
        raise TemplateSyntaxError('%r expects 5 arguments' % bits[0])
    if bits[-2] != 'as':
        raise TemplateSyntaxError(
            '%r expects "as" as the third argument' % bits[0])
    cal_date = bits[-4]
    limit = bits[-3]
    varname = bits[-1]
    cal_id = int(bits[1][1:-1])
    return resource_calendar_events_node(cal_id, cal_date, limit, varname)


class resource_calendar_events_node(template.Node):

    def __init__(self, cal_id, cal_date, limit, var_name):
        sd = cal_date[1:-1]
        if sd:
            sd = map(int, sd.split('-'))
            start_date = datetime.datetime(sd[0], sd[1], sd[2])
        else:
            start_date = datetime.datetime.today()

        limits = limit[1:-1].split(',')
        self.start_date = start_date - datetime.timedelta(days=int(limits[0]))
        self.end_date = start_date + datetime.timedelta(days=int(limits[1]))
        self.var_name = var_name
        self.cal_id = cal_id

    def render(self, context):
        request = context['request']
        events_list = []
        try:
            resource = ZornaResourceCalendar.objects.get(pk=self.cal_id)
            calendar = get_resource_calendar(resource)
            ao = get_allowed_objects(request.user, type(calendar), 'viewer')
            if calendar.pk in ao:
                events_list = get_events_for_object(
                    resource, self.start_date, self.end_date)
        except:
            pass
        context[self.var_name] = events_list
        return ''

@register.tag(name="calendar_events_for_object")
def calendar_events_for_object(parser, token):
    '''
    {% resource_calendar_events 'id' 'start_date' 'end_date' as events %}
    return events for current user
    dates must in format as yyyy-mm-dd
    '''
    bits = token.split_contents()
    if 6 != len(bits):
        raise TemplateSyntaxError('%r expects 5 arguments' % bits[0])
    if bits[-2] != 'as':
        raise TemplateSyntaxError(
            '%r expects "as" as the third argument' % bits[0])
    cal_date = bits[-4]
    limit = bits[-3]
    varname = bits[-1]
    zornaobject = bits[1]
    return calendar_events_for_object_node(zornaobject, cal_date, limit, varname)


class calendar_events_for_object_node(template.Node):

    def __init__(self, zornaobject, cal_date, limit, var_name):
        sd = cal_date[1:-1]
        if sd:
            sd = map(int, sd.split('-'))
            start_date = datetime.datetime(sd[0], sd[1], sd[2])
        else:
            start_date = datetime.datetime.today()

        limits = limit[1:-1].split(',')
        self.start_date = start_date - datetime.timedelta(days=int(limits[0]))
        self.end_date = start_date + datetime.timedelta(days=int(limits[1]))
        self.var_name = var_name
        self.zornaobject = Variable(zornaobject)

    def render(self, context):
        request = context['request']
        events_list = []
        try:
            zornaobject = self.zornaobject.resolve(context)
            events_list = get_events_for_object(
                    zornaobject, self.start_date, self.end_date)
        except Exception as e:
            pass
        context[self.var_name] = events_list
        return ''


class resource_calendars(template.Node):

    def __init__(self, var_name, perm):
        self.var_name = var_name
        self.perm = perm

    def render(self, context):
        request = context['request']
        calendars = get_user_calendars(request.user, self.perm)
        context[self.var_name] = [cal for cal in calendars if type(cal.content_object)==ZornaResourceCalendar]
        for cal in context[self.var_name]:
            cal.name = cal.content_object.name
            cal.description = cal.content_object.description
            cal.url = reverse('view_calendar', args=[cal.pk])
        return ''


@register.tag(name="get_resource_calendars")
def get_resource_calendars(parser, token):
    '''
    {% get_resource_calendars as calendars %}
    {% for f in calendars %}
    <a href="{{f.url}}" >{{f.name}}</a>
    <div><i>{{f.description}}</i></div>
    {% endfor %}
    '''
    bits = token.split_contents()
    if len(bits) not in [3,4]:
        raise TemplateSyntaxError('%r expects 3 or 4 arguments' % bits[0])
    if bits[-2] != 'as':
        raise TemplateSyntaxError(
            '%r expects "as" as the second argument' % bits[0])
    varname = bits[-1]
    if len(bits) == 4:
        perm = bits[1].split(',')
    else:
        perm = ['viewer']
    return resource_calendars(varname, perm)
