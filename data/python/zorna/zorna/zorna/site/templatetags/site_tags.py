from django.template import TemplateSyntaxError
from django import template
register = template.Library()

from zorna.site.models import SiteOptions


class check_if_has_access_to_option_node(template.Node):

    def __init__(self, key, var_name):
        self.var_name = var_name
        if not (key[0] == key[-1] and key[0] in ('"', "'")):
            self.key = key
        else:
            self.key = key[1:-1]

    def render(self, context):
        request = context['request']
        try:
            context[self.var_name] = SiteOptions.objects.is_access_valid(
                request.user, self.key)
        except:
            pass
        return ''


@register.tag(name="check_if_has_access_to")
def check_if_has_access_to_option(parser, token):
    bits = token.split_contents()
    if 4 != len(bits):
        raise TemplateSyntaxError('%r expects 4 arguments' % bits[0])
    if bits[-2] != 'as':
        raise TemplateSyntaxError(
            '%r expects "as" as the second argument' % bits[0])
    key = bits[1]
    varname = bits[-1]
    return check_if_has_access_to_option_node(key, varname)
