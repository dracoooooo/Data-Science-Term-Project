import xml.etree.ElementTree as ET
from src.ast_based_similarities.tree_edit_distance import edit_distance
from src.ast_based_similarities.tree_node import Node
# from zss import simple_distance, Node
import os
prefix = '{http://www.srcML.org/srcML/src}'

def parse_tree(xml_path):
    path = os.path.abspath(xml_path)
    xml_file_tree = ET.parse(path)
    root = xml_file_tree.getroot()
    zss_root, num = traverse_and_parse(root)
    return (zss_root, num)

def traverse_and_parse(root):
    if root.tag == prefix + 'comment' or root.tag == prefix + 'import' or root.tag == prefix + 'package':
        return (None , None)
    new_node = Node(str(root.tag).replace(prefix, ''))
    node_num = 1
    for child in root:
        (chl_node, chl_num) = traverse_and_parse(child)
        if chl_node != None:
            new_node.addkid(chl_node)
            node_num += chl_num
    return (new_node, node_num)


def tree_distance_similarities(xml_path_1,xml_path_2):
    (zss_tree_1, num1) = parse_tree(xml_path_1)
    (zss_tree_2, num2) = parse_tree(xml_path_2)
    dis = edit_distance(zss_tree_1, zss_tree_2)
    return 1 - dis/max(num1,num2)

def output_label(root):
    print(root.label)
    for i in Node.get_children(root):
        output_label(i)

def test():
    A = (
        Node("f")
            .addkid(Node("d")
                    .addkid(Node("a"))
                    .addkid(Node("c")
                            .addkid(Node("b"))))
            .addkid(Node("e"))
    )
    B = (
        Node("f")
            .addkid(Node("c")
                    .addkid(Node("d")
                            .addkid(Node("a")
                                    .addkid(Node("b")))
                            ))
            .addkid(Node("e")))
    # print(edit_distance(A,B))
    path = '../../test'
    file1 = os.path.join(path, 'ALU.xml')
    file2 = os.path.join(path, 'Game.xml')
    # print(tree_distance_similarities(file1,file2))


if __name__ == '__main__':
    test()
