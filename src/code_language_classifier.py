import os
import random
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

# python: 3272
# java: 1556
# js: 2103
from tensorflow.python.keras.utils.np_utils import to_categorical

java_path = "C:\\Users\\DRACO\\PycharmProjects\\Data-Science-Term-Project\\data\\java"
python_path = "C:\\Users\\DRACO\\PycharmProjects\\Data-Science-Term-Project\\data\\python"
js_path = "C:\\Users\\DRACO\\PycharmProjects\\Data-Science-Term-Project\\data\\javascript"
test_path = "C:\\Users\\DRACO\PycharmProjects\\Data-Science-Term-Project\data\\test"

suffix = {"java": ".java",
          "python": ".py",
          "javascript": ".js"}

training_size = 1000
testing_size = 500
# 分类语言数量
N = 3


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
                # text = text.replace("\n", "").replace("\t", "")
                ret.append(text)
    return ret


# 想法：用频率最高的1000个词作为1000维特征(是否可行？
# 看起来可行！
# todo: 打乱数据集
def prepare_data():
    # load data
    java_data = load_data(java_path, "java")
    python_data = load_data(python_path, "python")
    js_data = load_data(js_path, "javascript")

    # print(java_data[0: 2])
    # print(python_data)
    # print(js_data)

    # slice data
    train_data = java_data[0: training_size] + python_data[0: training_size] + js_data[0: training_size]
    test_data = java_data[training_size: training_size + testing_size] + python_data[training_size: training_size + testing_size] + js_data[training_size: training_size + testing_size]

    # label:
    # java: [1, 0, 0]
    # python: [0, 1, 0]
    # js: [0, 0, 1]
    train_label = [[1, 0, 0]] * training_size + [[0, 1, 0]] * training_size + [[0, 0, 1]] * training_size
    test_label = [[1, 0, 0]] * testing_size + [[0, 1, 0]] * testing_size + [[0, 0, 1]] * testing_size

    y_train = np.array(train_label)
    y_test = np.array(test_label)
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
    train_tokenizer = tokenize(train_data)

    # 从string变成int数组
    train_sequences = train_tokenizer.texts_to_sequences(train_data)
    test_sequences = train_tokenizer.texts_to_sequences(test_data)
    # 取前500个词
    max_length = 500
    # 取1000个特征
    features = 1000
    train_sequences = pad_sequences(train_sequences, padding='post', truncating='post', maxlen=max_length)
    test_sequences = pad_sequences(test_sequences, padding='post', truncating='post', maxlen=max_length)

    x_train = np.empty([N * training_size, features], "int")
    for i in range(train_sequences.__len__()):
        for j in range(max_length):
            if 0 < train_sequences[i][j] <= features:
                tmp = train_sequences[i][j]
                x_train[i][tmp] = 1

    x_test = np.empty([N * testing_size, features], "int")
    for i in range(test_sequences.__len__()):
        for j in range(max_length):
            if 0 < test_sequences[i][j] <= features:
                tmp = test_sequences[i][j]
                x_test[i][tmp] = 1

    # 随机化
    train_index = [i for i in range(N * training_size)]
    test_index = [i for i in range(N * testing_size)]
    random.shuffle(train_index)
    random.shuffle(test_index)
    x_train = x_train[train_index]
    y_train = y_train[train_index]
    x_test = x_test[test_index]
    y_test = y_test[test_index]

    return (x_train, y_train), (x_test, y_test)


def init_model():
    # define network structure
    model = Sequential()

    model.add(Dense(input_dim=1000, units=500, activation='relu'))
    model.add(Dense(units=500, activation='relu'))
    model.add(Dense(units=500, activation='relu'))
    model.add(Dense(units=3, activation='softmax'))

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


def tokenize(data):
    # init tokenizer
    tokenizer = Tokenizer(num_words=100,
                          filters='!"$%&()*+,-./:<=>?@[\\]^_`{|}~\t\n')  # 去掉了可能能够分别语言的符号
    tokenizer.fit_on_texts(data)

    return tokenizer



if __name__ == "__main__":
    # count()

    (x_train, y_train), (x_test, y_test) = prepare_data()
    model = init_model()

    history = model.fit(x_train, y_train, batch_size=100, epochs=10,
              validation_data=(x_test, y_test), verbose=2)
    plot_graphs(history, "accuracy")
    plot_graphs(history, "loss")


