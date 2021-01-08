import os
from django.core.management.base import BaseCommand, CommandError
from django.conf import settings
from whoosh.index import create_in
import HTMLParser
from BeautifulSoup import BeautifulSoup
from zorna.pages import PAGES_WHOOSH_SCHEMA
from zorna.utils import get_context_text


class Command(BaseCommand):
    help = 'Closes the specified poll for voting'

    def handle(self, *args, **options):
        ix = create_in(settings.HAYSTACK_WHOOSH_PATH, PAGES_WHOOSH_SCHEMA, "ZORNA_PAGES")
        writer = ix.writer()
        root = settings.PROJECT_PATH + \
                os.sep + settings.ZORNA_CONTENT + os.sep
        for dirName, subdirList, fileList in os.walk(root):
            for file_name in fileList:
                try:
                    path_file = os.path.join(dirName, file_name)
                    context, text = get_context_text(path_file)
                    pars = HTMLParser.HTMLParser()
                    text = pars.unescape(unicode(text,'utf-8'))
                    text = ''.join( BeautifulSoup( text ).findAll( text = True ))
                    url = path_file.replace(root, '')
                    title = os.path.splitext(os.path.split(path_file)[1])[0]
                    if context:
                        import yaml
                        context = yaml.load(context)
                        if context['title']:
                            title = context['title']
                    print "Added %s ---- %s" % (url, title)

                    writer.add_document(title=unicode(title), content=text,
                                    url=unicode(url))
                except Exception as e:
                    print e
                    pass
        writer.commit()