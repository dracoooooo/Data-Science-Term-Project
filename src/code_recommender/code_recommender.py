import os
import src.languange_classifier.code_language_classifier as clc
from tensorflow.python.keras.models import load_model
import pickle
import numpy as np
import re
import src.ast_based_similarities.create_ast as ast_creator
from tensorflow.keras.preprocessing.sequence import pad_sequences
import src.function_classifier.code_function_classifier_cpp as cfc_cpp
import src.function_classifier.code_function_classifier as cfc
import src.ast_based_similarities.ast_similarities as ast_evaluator



data_path_dict = {"java" : os.path.abspath('../../data/leetcode'),
                  "c&cpp" : os.path.abspath('../../data/leetcode_cpp')
                  }

func_model= {"java" : cfc,
                  "c&cpp" : cfc_cpp
}


file_name = 'input_file'
recommden_threshold = 5

def recommend_code(source_text):
    tmp_path = os.path.join(os.path.join(os.path.dirname(os.path.realpath(__file__)), "input_code_file"), 'input_file')
    creat_file(tmp_path + '.txt', source_text)
    lang,lan_prob = clc.predict(tmp_path + '.txt')
    # 消除文本文件
    os.remove(tmp_path + '.txt')

    new_file = tmp_path + '.' + lang
    # 特判c&cpp
    if new_file.endswith(".c&cpp"):
        new_file = new_file[0: new_file.rfind('.')] + '.cpp'
    #

    creat_file(new_file, source_text)
    func,func_prob = predict_function(new_file, lang)
    ast_creator.create_ast(new_file)
    xml_file = tmp_path + '.xml'
    func_path = get_func_path(func, lang)
    get_recommend_list_ast(func_path, xml_file)
    return


def get_recommend_list_cos(func_path, file):
    lang = file[file.rfind('.'):]
    paths = cfc.

def get_recommend_list_ast(func_path, xml_file):
    sim_dict = {}
    paths = cfc.file_paths(func_path, 'xml');
    for xml_path in paths:
        ans = ast_evaluator.tree_distance_similarities(xml_path, xml_file)
        sim_dict[xml_path] = ans
        print(ans)
    for i in sim_dict.items():
        print(i)

def get_func_path(func, language):
    return os.path.join(data_path_dict[language], func)

def predict_function(path, language):
    return  func_model[language].predict(path)

def creat_file(file_path, source_text):
    f = open(file_path, 'w+',encoding='UTF-8')
    f.write(source_text)

if __name__ == "__main__":
    data_path = '../../test'
    ret = clc.load_data(data_path, "cpp")
    recommend_code(ret[0])
