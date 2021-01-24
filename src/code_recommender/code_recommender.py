from src.languange_classifier.code_language_classifier import predict as language_type_predict
from src.languange_classifier.code_language_classifier import label as language_label
import os
import src.languange_classifier.code_language_classifier as clc
from tensorflow.python.keras.models import load_model
import pickle
import numpy as np
import re
import src.ast_based_similarities.create_ast as ast_creator
from tensorflow.keras.preprocessing.sequence import pad_sequences
import src.function_classifier.code_function_classifier_cpp as cfc

model_abs_path = os.path.abspath('../languange_classifier/language_classifier.h5')
language_tokenizer_path = os.path.abspath('../languange_classifier/tokenizer.pkl')


data_path_dict = {"java" : os.path.abspath('../../data/leetcode'),
                  "c&cpp" : os.path.abspath('../../data/leetcode_cpp')
                  }

func_model_dict = {"java" : os.path.abspath('../function_classifier/function_classifier.h5'),
                  "c&cpp" : os.path.abspath('../function_classifier/function_classifier_cpp.h5')
}
func_tokenizer_dict = {"java" : os.path.abspath('../function_classifier/tokenizer_bow.pkl'),
                      "c&cpp" : os.path.abspath('../function_classifier/tokenizer_bow_cpp.pkl')
}

def recommend_code(source_text):
    tmp_path = os.path.join(os.path.abspath('./input_code_file'), "input_file")
    lang,lan_prob = predict_language(source_text)
    new_file = tmp_path + '.' + lang
    creat_file(new_file, source_text)
    func,func_prob = predict_function(source_text, lang)
    # ast_creator.create_ast(new_file)
    return


def evaluate_func_path(func, language):
    pass


def predict_function(source_text, language):
    model = load_model(func_model_dict[language])
    feature = get_feature_bag_of_word(source_text, language)
    prediction = model.predict([feature])[0]
    func_label = clc.one_hot(prediction)
    func = ''
    if func_label == cfc.label["sort"]:
        func = "sort"
    elif func_label == cfc.label["tree"]:
        func = "tree"
    elif func_label == cfc.label["dp"]:
        func = "dp"
    print("function type : ", func)
    print("possibility : ", max(prediction))
    return func, max(prediction)


def creat_file(file_path, source_text):
    f = open(file_path, 'w+',encoding='UTF-8')
    f.write(source_text)
def predict_language(source_text):
    model = load_model(model_abs_path)
    text = handle_user_data(source_text)
    prediction = model.predict(text)
    prediction = prediction[0]
    lan_label = clc.one_hot(prediction)
    lang = ''
    if lan_label == clc.label["java"]:
        lang = "java"
    elif lan_label== clc.label["python"]:
        lang = "python"
    elif lan_label == clc.label["js"]:
        lang = "javascript"
    elif lan_label == clc.label["ccpp"]:
        lang = "c&cpp"
    print("language type : ", lang)
    print("possibility : ", max(prediction))
    return lang,max(prediction)

def handle_user_data(text):
    features = clc.features
    max_length = clc.max_length
    text = [text]
    text[0] = clc.add_blank(text[0])
    f1 = open(language_tokenizer_path,'rb')
    lan_tokenizer = pickle.load(f1)
    f1.close()
    sequence = lan_tokenizer.texts_to_sequences(text)
    sequence = pad_sequences(sequence, padding='post', truncating='post', maxlen=max_length)
    feature = np.zeros([1, features], "int")
    for j in range(max_length):
        tmp = sequence[0][j]
        if 0 < sequence[0][j] <= features:
            feature[0][tmp] = 1
        else:
            feature[0][tmp] = 0
    return feature


def get_feature_bag_of_word(source_text, language):
    f1 = open(func_tokenizer_dict[language], 'rb')
    tokenizer = pickle.load(f1)
    f1.close()
    code = re.sub("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", '', source_text)
    seq = tokenizer.texts_to_sequences([code])[0]
    feature = [0] * clc.features
    for i in range(seq.__len__()):
        if 0 < seq[i] < clc.features:
            feature[seq[i]] +=1
    return feature



if __name__ == "__main__":
    data_path = '../../test'
    ret = clc.load_data(data_path, "cpp")
    for i in ret:
        recommend_code(i)