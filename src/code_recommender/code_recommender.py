from src.languange_classifier.code_language_classifier import predict as language_type_predict
from src.languange_classifier.code_language_classifier import label as language_label
import os
import src.languange_classifier.code_language_classifier as clc
from tensorflow.python.keras.models import load_model
import pickle
import numpy as np
import src.ast_based_similarities.create_ast as ast_creator
from tensorflow.keras.preprocessing.sequence import pad_sequences
input_file_path = os.path.abspath('./input_code_file')
file_name = "input_file.txt"
model_abs_path = os.path.abspath('../languange_classifier/language_classifier.h5')
language_tokenizer_path = os.path.abspath('../languange_classifier/tokenizer.pkl')

data_path_dict = {"java" : os.path.abspath('../../data/leetcode'),
                  "cpp" : os.path.abspath('../../data/leetcode_cpp')
                  }


def recommend_code(source_text):
    file_path = os.path.join(input_file_path, file_name)
    creat_file(file_path, source_text)
    lang = get_language(source_text)
    new_file = file_path[0:file_path.rfind('.')] + '.' + lang
    creat_file(new_file, source_text)
    # ast_creator.create_ast(new_file)
    evaluate_func(new_file, lang)
    return


def evaluate_func(path, language):
    if language == 'java':
        pass
    elif language == 'cpp':
        pass




def creat_file(file_path, source_text):
    f = open(file_path, 'w+',encoding='UTF-8')
    f.write(source_text)
def get_language(source_text):
    model = load_model(model_abs_path)
    text = handle_user_data(source_text)
    prediction = model.predict(text)
    prediction = prediction[0]
    lan_label = clc.soft_max(prediction)
    lang = ''
    if lan_label == clc.label["java"]:
        lang = "java"
    elif lan_label== clc.label["python"]:
        lang = "python"
    elif lan_label == clc.label["js"]:
        lang = "javascript"
    elif lan_label == clc.label["ccpp"]:
        lang = "c&cpp"
    return lang

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








if __name__ == "__main__":
    data_path = '../../test'
    ret = clc.load_data(data_path, "java")
    recommend_code(ret[0])