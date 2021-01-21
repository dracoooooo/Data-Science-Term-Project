import xml.etree.ElementTree as ET
from zss import simple_distance, Node
import os
prefix = '{http://www.srcML.org/srcML/src}'

def initilize(xml_path, xml_file_name):
    path = os.path.abspath(os.path.join(xml_path,xml_file_name))
    xml_file_tree= ET.parse(path)
    return xml_file_tree

def parse_tree(xml_file_tree):
    root = xml_file_tree.getroot()
    zss_root, num = traverse_and_parse(root)
    return (zss_root, num)

def traverse_and_parse(root):
    new_node = Node(str(root.tag).replace(prefix, ''))
    node_num = 1
    for child in root:
        (chl_node, chl_num) = traverse_and_parse(child)
        new_node.addkid(chl_node)
        node_num += chl_num
    return (new_node, node_num)


def tree_distance_similarities(xml_path_1, xml_file_1,xml_path_2, xml_file_2):
    (zss_tree_1, num1) = parse_tree(initilize(xml_path_1, xml_file_1))
    (zss_tree_2, num2) = parse_tree(initilize(xml_path_2, xml_file_2))
    print(num1)
    print(num2)
    dis = simple_distance(zss_tree_1, zss_tree_2)
    return 1 - dis/max(num1,num2)



def test():
    java_path = '../../test'
    xml_file_name1 = 'ALU.xml'
    xml_file_name2= 'Map.xml'
    path1 = path2 = java_path
    print(tree_distance_similarities(path1,xml_file_name1,path2,xml_file_name2))


if __name__ == '__main__':
    test()
