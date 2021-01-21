import re
# 除去换行符
def process_text(text):
    text = re.sub(r'\s+','',text)
    return text