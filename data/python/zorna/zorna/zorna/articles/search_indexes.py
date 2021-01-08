import datetime
from haystack import indexes
from haystack import site
from zorna.articles.models import ArticleStory


class ArticleStoryIndex(indexes.SearchIndex):
    text = indexes.CharField(document=True, use_template=True)
    author = indexes.CharField(model_attr='owner')
    pub_date = indexes.DateTimeField(model_attr='time_created')
    categories = indexes.MultiValueField()

    def get_model(self):
        return ArticleStory

    def get_updated_field(self):
        return 'time_updated'

    def index_queryset(self):
        """Used when the entire index for model is updated."""
        return self.get_model().objects.filter(time_updated__lte=datetime.datetime.now())

    def prepare_author(self, obj):
        return obj.owner.get_full_name()

    def prepare_categories(self, obj):
        # Since we're using a M2M relationship with a complex lookup,
        # we can prepare the list here.
        return [category.pk for category in obj.categories.all()]

site.register(ArticleStory, ArticleStoryIndex)
