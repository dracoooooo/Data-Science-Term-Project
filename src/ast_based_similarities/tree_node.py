
class Node(object):
    def __init__(self, label):
        self.children = list()
        self.label = label
        self.index = -1
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
