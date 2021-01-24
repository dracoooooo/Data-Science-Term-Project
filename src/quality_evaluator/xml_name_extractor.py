import os
import xml.etree.ElementTree as ET

# 使用srcML创建xml，并获取变量名
prefix = '{http://www.srcML.org/srcML/src}'
variable_names_list = []
function_names_list = []
class_names_list = []
def search__names(xml_path, file_name):
    variable_names_list.clear()
    function_names_list.clear()
    class_names_list.clear()
    full_file = os.path.abspath(os.path.join(xml_path, file_name))
    tree = ET.parse(full_file)
    root = tree.getroot()
    find_names(root)
    sets_list=[]
    sets_list.append(set(variable_names_list))
    sets_list.append(set(function_names_list))
    sets_list.append(set(class_names_list))
    return sets_list

def find_names(root):
    if (root.tag == prefix+'decl'):
        name = root.find('./'+prefix + 'name')
        if(name != None):
            if (name.text!= None):
                variable_names_list.append(name.text)
    if(root.tag == prefix+'function'):
        name = root.find(prefix+'name')
        if(name!=None):
            if(name.text != None):
                function_names_list.append(name.text)
    if(root.tag == prefix+'class'):
        name = root.find(prefix+'name')
        if(name!=None):
            if(name.text!=None):
                class_names_list.append(name.text)
    for child in root:
        find_names(child)
    return


if __name__ =="__main__":
    print(search__names('../../test', 'Game.xml'));

