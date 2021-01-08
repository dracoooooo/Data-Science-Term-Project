from django import template

from zorna.utils import render_page

register = template.Library()


def show_content(context, page):
    """Display a content from a page.

    Example::

        {% show_content "path-ti-page" %}

    """
    request = context.get('request', None)
    return {'content': render_page(request, page)}
show_content = register.inclusion_tag('pages/content.html',
                                      takes_context=True)(show_content)
