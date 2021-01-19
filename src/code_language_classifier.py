import os
import pickle
import random
import numpy as np
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from keras.models import Sequential
from keras.layers.core import Dense, Dropout, Activation
import matplotlib.pyplot as plt

# 使用神经网络进行代码语言分类

# 代码文件数量：
# python: 3272
# java: 1556
# js: 2103
from tensorflow.python.keras.models import load_model

java_path = "../data/java"
python_path = "../data/python"
js_path = "../data/javascript"
test_path = "../data/test"


# 代码语言的后缀名
suffix = {
    "java": ".java",
    "python": ".py",
    "javascript": ".js"
}

# 代码语言的标签(one-hot)
label = {
    "java": [1, 0, 0],
    "python": [0, 1, 0],
    "js": [0, 0, 1]
}

# 每个语言的训练集大小和测试集大小
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


# 取前500个词
max_length = 500
# 取200个特征
features = 200


# 用来处理代码文件转成能使用的特征
# 想法：用频率最高的200个词作为200维特征(是否可行？
# 看起来可行！
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

    y_train = np.array(train_label)
    y_test = np.array(test_label)

    # init tokenizer
    # train_tokenizer = tokenize(train_data)

    # save tokenizer
    # f = open('tokenizer.pkl', 'wb')
    # pickle.dump(train_tokenizer, f)
    # f.close()

    # read tokenizer
    f1 = open('tokenizer.pkl', 'rb')
    train_tokenizer = pickle.load(f1)
    f1.close()

    # 从string变成int数组
    train_sequences = train_tokenizer.texts_to_sequences(train_data)
    test_sequences = train_tokenizer.texts_to_sequences(test_data)

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


# 初始化神经网络
def init_model():
    # define network structure
    model = Sequential()

    model.add(Dense(input_dim=features, units=30, activation='relu'))
    model.add(Dense(units=20, activation='relu'))
    model.add(Dense(units=3, activation='softmax'))

    # set configurations
    model.compile(loss='categorical_crossentropy',
                  optimizer='adam',
                  metrics=['accuracy'])

    return model


# 计算代码文件数量
def count():
    py = load_data(python_path, "python")
    js = load_data(js_path, "javascript")
    java = load_data(java_path, "java")
    print("python:", py.__len__())
    print("java:", java.__len__())
    print("js:", js.__len__())


# 画画
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


# 用来将一个文本文件转换成上述特征向量
def handle_user_data(path):
    text = [open(path, encoding='UTF-8').read()]
    # read tokenizer
    f1 = open('tokenizer.pkl', 'rb')
    tokenizer = pickle.load(f1)
    f1.close()

    sequence = tokenizer.texts_to_sequences(text)
    sequence = pad_sequences(sequence, padding='post', truncating='post', maxlen=max_length)
    feature = np.zeros([1, features], "int")
    for j in range(max_length):
        tmp = sequence[0][j]
        if 0 < sequence[0][j] <= features:
            feature[0][tmp] = 1
        else:
            feature[0][tmp] = 0
    return feature


# 训练并保存模型
def train():
    (x_train, y_train), (x_test, y_test) = prepare_data()
    model = init_model()
    history = model.fit(x_train, y_train, batch_size=100, epochs=10,
              validation_data=(x_test, y_test), verbose=2)
    plot_graphs(history, "accuracy")
    plot_graphs(history, "loss")

    model.save('language_classifier.h5')


def soft_max(arr):
    max1 = max(arr)
    ret = []
    for i in arr:
        if i == max1:
            ret.append(1)
        else:
            ret.append(0)
    return ret


def predict(path):
    model = load_model('language_classifier.h5')
    # model.summary()
    test = handle_user_data(path)
    prediction = model.predict(test)
    tmp = prediction[0]
    int_tmp = soft_max(tmp)
    lang = ""
    if int_tmp == [1, 0, 0]:
        lang = "java"
    elif int_tmp == [0, 1, 0]:
        lang = "python"
    elif int_tmp == [0, 0, 1]:
        lang = "javascript"
    print("predict language:",lang)
    print("prossibility: ", max(tmp))


if __name__ == "__main__":
    train()
    # predict('./code_language_classifier.py')
    # predict('../data/test/java_file1.js')
    # predict('../data/test/java_file2.java')
    # predict('../data/test/js_file1.java')
    # predict('../data/test/js_file2.js')
    # predict('../data/test/python_file1.java')
    # predict('../data/test/python_file2.js')



