import os
import re
import nltk
import numpy as np
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from keras.models import Sequential
from keras.layers.core import Dense, Dropout, Activation
from tensorflow.python.keras.layers import TextVectorization
import matplotlib.pyplot as plt


# 代码语言分类


# 代码文件数量：
# python: 5080
# java: 1763
# js: 2103
java_path = "C:\\Users\\DRACO\\PycharmProjects\\Data-Science-Term-Project\\data\\java"
python_path = "C:\\Users\\DRACO\\PycharmProjects\\Data-Science-Term-Project\\data\\python"
js_path = "C:\\Users\\DRACO\\PycharmProjects\\Data-Science-Term-Project\\data\\javascript"

suffix = {"java": ".java",
          "python": ".py",
          "javascript": ".js"}

training_size = 1200
testing_size = 500


# 返回data_path下所有代码文件的字符串
def load_data(data_path, language):
    ret = []
    for dirpath, dirnames, filenames in os.walk(data_path):
        for file in filenames:
            if file.endswith(suffix[language]):
                path = dirpath + "\\" + file
                # print(path)
                text = open(path, encoding='UTF-8').read()
                # 删除换行符和tab
                text = text.replace("\n", "").replace("\t", "")
                ret.append(text)
    return ret


def prepare_data():
    # load data
    java_data = load_data(java_path, "java")
    python_data = load_data(python_path, "python")
    js_data = load_data(js_path, "javascript")

    # slice data
    train_data = java_data[0: training_size] + python_data[0: training_size] + js_data[0: training_size]
    test_data = java_data[training_size: training_size + testing_size] + python_data[training_size: training_size + testing_size] + js_data[training_size: training_size + testing_size]

    # label:
    # java: [1, 0, 0]
    # python: [0, 1, 0]
    # js: [0, 0, 1]
    train_label = [[1, 0, 0]] * training_size + [[0, 1, 0]] * training_size + [[0, 0, 1]] * training_size
    test_label = [[1, 0, 0]] * testing_size + [[0, 1, 0]] * testing_size + [[0, 0, 1]] * testing_size

    # java_tokens = nltk.word_tokenize(java_data[0])
    #
    # print(java_tokens)
    #
    # vectorizer = TfidfVectorizer()
    # X = vectorizer.fit_transform(java_tokens)
    # print(X)
    # X.toarray()
    # print(X)

    # init tokenizer
    tokenizer = Tokenizer(num_words=100, oov_token="<OOV>")
    tokenizer.fit_on_texts(train_data)
    word_index = tokenizer.word_index
    # 从string变成int数组
    sequence = tokenizer.texts_to_sequences(train_data)

    padded = pad_sequences(train_data, padding='post', truncating='post', maxlen=400)

    print(word_index)
    print(sequence)
    return


def init_model():
    # define network structure
    model = Sequential()

    model.add(Dense(input_dim=28*28, units=500, activation='relu'))
    model.add(Dense(units=500, activation='relu'))
    model.add(Dense(units=500, activation='relu'))
    model.add(Dense(units=10, activation='softmax'))

    # set configurations
    model.compile(loss='categorical_crossentropy',
                  optimizer='adam',
                  metrics=['accuracy'])

    return model


def count():
    py = load_data(python_path, "python")
    js = load_data(js_path, "javascript")
    java = load_data(java_path, "java")
    print("python:", py.__len__())
    print("java:", java.__len__())
    print("js:", js.__len__())


def plot_graphs(history, string):
    plt.plot(history.history[string])
    plt.plot(history.history['val_'+string])
    plt.xlabel("Epochs")
    plt.ylabel(string)
    plt.legend([string, 'val_'+string])
    plt.show()


if __name__ == "__main__":
    # count()

    prepare_data()

    print([[0,0,1]] * 2 + [[0,0,0]])

    # (x_train, y_train), (x_test, y_test) = prepare_data()
    # model = init_model()
    # history = model.fit(x_train, y_train, batch_size=100, epochs=10,
    #           validation_data=(x_test, y_test), verbose=2)
    # plot_graphs(history, "accuracy")
    # plot_graphs(history, "loss")


