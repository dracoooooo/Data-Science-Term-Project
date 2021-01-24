import os
import pickle
import re

from tensorflow.keras.preprocessing.text import Tokenizer
from src.languange_classifier import code_language_classifier as clc
from src.text_based_similarities import Levenshtein_distance
from src.text_based_similarities import cos_similarity
data_path = "../../data/leetcode/raw"


def init_tokenizer():
    java_data = clc.load_data(data_path, "java")
    tokenizer = Tokenizer(filters='!"#$%&()*+,-./:;<=>?@[\\]^_`{|}~\t\n',)
    tokenizer.fit_on_texts(java_data)
    # save
    f = open('tokenizer_lcs.pkl', 'wb')
    pickle.dump(tokenizer, f)
    f.close()


def code2text(path):
    text = open(path, encoding='UTF-8').read()
    text = re.sub("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", '', text)
    return text


def token_lcs(path1, path2):
    text1 = code2text(path1)
    text2 = code2text(path2)
    path = os.path.abspath(os.path.dirname(__file__)) + "\\tokenizer_lcs.pkl"
    f1 = open(path, 'rb')
    tokenizer = pickle.load(f1)
    f1.close()

    # java_data = clc.load_data(data_path, "java")
    # tokenizer = Tokenizer(filters='!"#$%&()*+,-./:;<=>?@[\\]^_`{|}~\t\n',)
    # tokenizer.fit_on_texts(java_data)

    seq1 = tokenizer.texts_to_sequences([text1])[0]
    seq2 = tokenizer.texts_to_sequences([text2])[0]
    return Levenshtein_distance.Levenshtein_distance(seq1,seq2) / max(len(seq1), len(seq2))

def token_cos(path1, path2):
    text1 = code2text(path1)
    text2 = code2text(path2)
    path = os.path.abspath(os.path.dirname(__file__)) + "\\tokenizer_lcs.pkl"
    f1 = open(path, 'rb')
    tokenizer = pickle.load(f1)
    f1.close()

    # java_data = clc.load_data(data_path, "java")
    # tokenizer = Tokenizer(filters='!"#$%&()*+,-./:;<=>?@[\\]^_`{|}~\t\n',)
    # tokenizer.fit_on_texts(java_data)

    seq1 = tokenizer.texts_to_sequences([text1])[0]
    seq2 = tokenizer.texts_to_sequences([text2])[0]
    return cos_similarity.cos_similarity_text(seq1, seq2)

if __name__ == "__main__":
    # init_tokenizer()
    path1 = "../../data/leetcode/raw/solutions/_1.java"
    path2 = "../../data/leetcode/raw/solutions/_2.java"
    similarity = token_cos(path1, path2)
    print(similarity)