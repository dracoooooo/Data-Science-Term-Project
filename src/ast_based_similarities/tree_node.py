import numpy as np

class Node(object):
    def __init__(self, label, tree_num):
        self.children = list()
        self.label = label
        self.index = -1
        self.T = np.empty(tree_num+1, dtype=str)
        self.l = np.empty(tree_num+1, dtype=int)
    @staticmethod
    def get_children(node):
        return node.children
    @staticmethod
    def get_label(node):
        return node.label
    def addkid(self, node, before=False):
        if before:
            self.children.insert(0, node)
        else:
            self.children.append(node)
        return self
