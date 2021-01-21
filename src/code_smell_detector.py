from src import xml_name_extractor
import os
import re
from functools import reduce
java_path = "../test"
file = "Node.java"

def average_name_score(arg_list):
    total_num = 0
    total_score = 0
    for j in range(0,3):
        for i in arg_list[j]:
            total_num += 1
            total_score += get_name_score(i)

    return total_score/total_num
#  standard:
#  weight : 60% -> len < 3 && len >= 20 -> 0%
#  weight : 40% -> repeated -> 0%
#  weight :
def get_name_score(var):
    iter_name_dict={"i", "j", "k"}
    total_score = 0
    if len(var) < 3 and len(var) >= 20:
        if var in iter_name_dict:
            total_score += 60
    else:
        total_score+=60
    if isRepeated(var) ==  0:
        total_score += 40
    return total_score


def isRepeated(var):
    for i in range(1, len(var)):
        if len(var)%i == 0:
            str = var[0:i] * int(len(var)/i)
            if str == var:
                return 1
    return 0


def load_source_codes(code_path, file_name):
    abs_path = os.path.abspath(os.path.join(code_path, file_name))
    f = open(abs_path, encoding='UTF-8')
    source_text = f.read();
    f.close()
    xml_file_name = file_name.replace(".java", ".xml")
    name_list = xml_name_extractor.search__names(code_path, xml_file_name)
    return (source_text,name_list)


def get_method_lines(source_text, method_name):
    l_r_span = re.search(method_name, source_text).span()
    line_cnt = 0
    parenthesis_cnt = 0
    for i in range(l_r_span[0], len(source_text)):
        if(source_text[i] == '{'):
            parenthesis_cnt+=1
        if(source_text[i] == '}'):
            parenthesis_cnt-=1
            if(parenthesis_cnt == 0):
                line_cnt+=1
                break
        if(source_text[i] == '\n'):
            line_cnt+=1
    return line_cnt


# standard：
# line_size>100 && portion_size > 50% -> 0%
# line_size>100 && portion_size < 50% -> 25%
# line_size<100 && portion_size > 60% -> 50%
# line_size<100 && portion_size > 30% -> 75%
# line_size<100 && portion_size < 30% -> 100%

def average_method_score(source_text, method_list):
    line_size_dict = {}
    total = 0
    method_cnt = 0
    for i in method_list:
        line_size_dict[i] = get_method_lines(source_text, i)
        total+=line_size_dict[i]
        method_cnt+=1
    portion_size_dict = {}
    for lines in line_size_dict.items():
        portion_size_dict[lines[0]] = lines[1] *100 / total
    score_dict = {}
    for lines in line_size_dict.items():
        score_dict[lines[0]] = get_method_score(lines[1], portion_size_dict.get(lines[0]))
    total_score = reduce(lambda x,y: x+y,score_dict.values())
    return total_score/method_cnt


def get_method_score(line_size, portion_size):
    if(line_size>=100 and portion_size >= 50.0):
        return 0;
    elif(line_size>=100 and portion_size<50.0) :
        return 25;
    elif(line_size<100 and portion_size >= 60.0):
        return 50;
    elif(line_size<100 and portion_size>=30.0):
        return 75;
    elif(line_size<100 and portion_size<30.0):
        return 100;

def average_score(code_path, file_name):
    text, arg_list = load_source_codes(code_path, file_name)
    del_comments_text = re.sub("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", '', text)
    avg_method_score = average_method_score(del_comments_text, arg_list[1])
    avg_name_score = average_name_score(arg_list)
    return (avg_method_score +avg_name_score)/2

def test():
    print(average_score(java_path, file))
if __name__ == "__main__":
    test()