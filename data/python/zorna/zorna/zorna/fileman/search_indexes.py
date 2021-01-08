import datetime
from haystack import indexes
from haystack import site
from zorna.fileman.models import ZornaFile


class ZornaFileIndex(indexes.SearchIndex):
    text = indexes.CharField(document=True, use_template=True)
    filename = indexes.CharField()
    description = indexes.CharField(model_attr='description')
    folder = indexes.CharField(model_attr='folder')
    path = indexes.CharField()
    url = indexes.CharField()
    author = indexes.CharField(model_attr='owner')
    pub_date = indexes.DateTimeField(model_attr='time_created')
    tags = indexes.CharField(model_attr='tags')

    def get_model(self):
        return ZornaFile

    def get_updated_field(self):
        return 'time_updated'

    def index_queryset(self):
        """Used when the entire index for model is updated."""
        return self.get_model().objects.filter(time_updated__lte=datetime.datetime.now())

    def prepare_author(self, obj):
        return obj.owner.get_full_name()

    def prepare(self, obj):
        self.prepared_data = super(ZornaFileIndex, self).prepare(obj)
        info = obj.get_file_info()
        if info:
            self.prepared_data['filename']=info['filename']
            self.prepared_data['path']=info['path']
            self.prepared_data['url']=info['url']
        return self.prepared_data

site.register(ZornaFile, ZornaFileIndex)
