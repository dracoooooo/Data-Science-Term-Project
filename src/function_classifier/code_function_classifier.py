import os
import pickle
import random
import re
import numpy as np
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from keras.models import Sequential
from keras.layers.core import Dense, Dropout, Activation
import matplotlib.pyplot as plt
from src.languange_classifier.code_language_classifier import load_data, plot_graphs, soft_max

features = 1000


def init_model():
    model = Sequential()
    model.add(Dense(input_dim=features, units=30, activation='relu'))
    model.add(Dense(units=20, activation='relu'))
    model.add(Dense(units=3, activation='softmax'))
    # set configurations
    model.compile(loss='categorical_crossentropy',
                  optimizer='adam',
                  metrics=['accuracy'])
    return model


# 提取特征
def prepare_data():
    return


def self_learning():
    (x_train, y_train), (x_test, y_test) , raw = prepare_data()
    model = init_model()
    n = 5 # 一次确定多少个
    while raw:
        model.fit(x_train, y_train, batch_size=100, epochs=10,
            validation_data=(x_test, y_test), verbose=2)
        prediction = model.predict(raw)
        max_prediction = []
        for i in prediction:
            max_prediction.append(max(prediction))

        for i in range(n):
            if raw:
                index = max_prediction.index(max(max_prediction))
                data = raw[index]
                label = soft_max(prediction[index])
                x_train.append(data)
                y_train.append(label)
                max_prediction.pop(index)
                raw.pop(index)
                prediction.pop(index)
    model.save('function_classifier.h5')
    return model



if __name__ == "__main__":
    array = load_data("../../data/leetcode/array", "java")
    math = load_data("../../data/leetcode/math", "java")
    search = load_data("../../data/leetcode/search", "java")
    sort = load_data("../../data/leetcode/sort", "java")
    string = load_data("../../data/leetcode/string", "java")
    tree = load_data("../../data/leetcode/tree", "java")
    raw = load_data("../../data/leetcode/raw", "java")

