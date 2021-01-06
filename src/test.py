from sklearn.feature_extraction.text import TfidfVectorizer
import os
import re
import nltk

# cnn代码分类
# 自然语言处理

java_path = "C:\\Users\\DRACO\\PycharmProjects\\Data-Science-Term-Project\\data\\java"
python_path = "data/python"

# 返回data_path下所有文件的字符串


def load_data(data_path):
    ret = []
    for dirpath, dirnames, filenames in os.walk(data_path):
        for file in filenames:
            if file.endswith(".java"):
                path = dirpath + "\\" + file
                text = open(path, encoding='UTF-8').read()
                # 删除换行符和tab和空格
                text = text.replace("\n", "").replace("\t", "")
                ret.append(text)
    return ret


# def test():
#     a = "#afdsfds"
#     b = "/*test zhushi*/" \
#         "int a = 1"
#     b = re.sub("/*/", "", b)
#     print(b)


if __name__ == "__main__":
    print("hello")
    data = load_data(java_path)
    for i in data:
        print(i)

    tokens = nltk.word_tokenize(data[0])

    print(tokens)
    vectorizer = TfidfVectorizer(min_df=1)
    vectorizer.fit_transform(tokens)
    X = vectorizer.fit_transform(tokens)
    X.toarray()
    print(X)
