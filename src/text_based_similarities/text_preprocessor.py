import os
import re


# 除去换行符
def process_text(text):
    text = re.sub(r'\s+','',text)
    return text


def load(path):
    path = os.path.abspath(path)
    f = open(path, encoding='UTF-8')
    source_text = f.read()
    del_comments_text = re.sub("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", '', source_text)
    f.close()
    return del_comments_text