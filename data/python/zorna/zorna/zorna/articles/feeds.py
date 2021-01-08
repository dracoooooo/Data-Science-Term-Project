from django.contrib.syndication.views import Feed
from django.shortcuts import get_object_or_404
from django.contrib.sites.models import Site
from django.utils.translation import ugettext_lazy as _

from zorna.articles.models import ArticleStory, ArticleCategory


class ArticleCategoryFeed(Feed):
    current_site = Site.objects.get_current()
    description = _(
        u"Updates on changes and additions to ") + current_site.name
    title = current_site.name + " site news"

    def get_object(self, request, category_id):
        return get_object_or_404(ArticleCategory, pk=category_id)

    def title(self, obj):
        return self.current_site.name + ": " + _(u"Articles for category %s") % obj.name

    def link(self, obj):
        return obj.get_url_path()

    def description(self, obj):
        return _(u"Articles recently published in category %s") % obj.name

    def items(self, obj):
        return ArticleStory.objects.filter(categories=obj).order_by('-time_created')[:30]

    def item_title(self, item):
        return item.title

    def item_description(self, item):
        return item.body

    def item_author_name(self, item):
        return item.owner.get_full_name()

    def item_pubdate(self, item):
        return item.time_created
