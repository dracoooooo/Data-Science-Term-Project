import datetime
from haystack import indexes
from haystack import site
from zorna.faq.models import FaqQuestion


class FaqQuestionIndex(indexes.SearchIndex):
    text = indexes.CharField(document=True, use_template=True)
    faq_name = indexes.CharField()
    faq = indexes.CharField()
    category = indexes.CharField()

    def get_model(self):
        return FaqQuestion

    def get_updated_field(self):
        return 'time_updated'

    def index_queryset(self):
        """Used when the entire index for model is updated."""
        return self.get_model().objects.filter(time_updated__lte=datetime.datetime.now())

    def prepare(self, obj):
        self.prepared_data = super(FaqQuestionIndex, self).prepare(obj)
        category = obj.category
        faq = category.faq
        self.prepared_data['faq_name'] = faq.name
        self.prepared_data['faq'] = str(faq.pk)
        self.prepared_data['category'] = category.name
        return self.prepared_data

site.register(FaqQuestion, FaqQuestionIndex)