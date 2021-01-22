import numpy as np
from src.ast_based_similarities.tree_node import Node


max_nodes = 3000

LR_keyroots1 = list()
LR_keyroots2 = list()
l1 = np.empty(max_nodes, dtype=int)
l2 = np.empty(max_nodes, dtype=int)
T1 = np.empty(max_nodes, dtype=str)
T2 = np.empty(max_nodes, dtype=str)
treedist = np.zeros([max_nodes,max_nodes],dtype=int)

def l_and_LRkeyroot_A(node):
    leftmost = node.index
    chl_cnt=0
    for i in Node.get_children(node):
        chl_cnt+=1
        l = l_and_LRkeyroot_A(i)
        if(chl_cnt == 1):
            leftmost = l
        else:
            LR_keyroots1.append(i.index)
    l1[node.index-1] = leftmost
    return leftmost

def l_and_LRkeyroot_A_wrapper(root):
    l_and_LRkeyroot_A(root)
    LR_keyroots1.append(root.index)


def l_and_LRkeyroot_B(node):
    leftmost = node.index
    chl_cnt=0
    for i in Node.get_children(node):
        chl_cnt+=1
        l = l_and_LRkeyroot_B(i)
        if(chl_cnt == 1):
            leftmost = l
        else:
            LR_keyroots2.append(i.index)
    l2[node.index-1] = leftmost
    return leftmost

def l_and_LRkeyroot_B_wrapper(root):
    l_and_LRkeyroot_B(root)
    LR_keyroots2.append(root.index)

# return index
def postorder_index_A(node, index):
    for chl in Node.get_children(node):
        index = postorder_index_A(chl, index)
    index+=1
    node.index = index
    T1[index-1] = Node.get_label(node)
    return index
# return index
def postorder_index_B(node, index):
    for chl in Node.get_children(node):
        index = postorder_index_B(chl, index)
    index+=1
    node.index = index
    T2[index-1] = Node.get_label(node)
    return index

def postorder_index_A_wrapper(root):
    postorder_index_A(root,0)

def postorder_index_B_wrapper(root):
    postorder_index_B(root,0)

def print_test(node):
    print(Node.get_label(node)+ " : "+str(node.index))
    for i in Node.get_children(node):
        print_test(i)

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
                    .addkid(Node("a"))
                    .addkid(Node("b"))
                      )
                    )
            .addkid(Node("e")))
    print(edit_distance(A,B))



def edit_distance(A, B):
    postorder_index_A_wrapper(A)
    l_and_LRkeyroot_A_wrapper(A)
    postorder_index_B_wrapper(B)
    l_and_LRkeyroot_B_wrapper(B)
    for i1 in range(1, LR_keyroots1.__len__()+1):
        for j1 in range(1, LR_keyroots2.__len__()+1):
            i = LR_keyroots1[i1-1]
            j = LR_keyroots2[j1-1]
            treedist[i,j] =treeDist(i,j,A,B)
    return treedist[A.index,B.index]

def treeDist(i,j,A,B):
    forestdist = np.zeros([i+1,j+1],dtype=int)
    forestdist[0,0] = 0
    delete = 1
    insert = 1

    for i1 in range(l1[i-1], i+1):
        forestdist[i1, 0] = forestdist[i1-1, 0] + delete
    for j1 in range(l2[j-1], j+1):
        forestdist[0, j1] = forestdist[0, j1 - 1] + insert
    for i1 in range(l1[i-1], i + 1):
        for j1 in range(l2[j-1], j + 1):
            i_tmp = i1-1
            if  (l1[i-1] > i1 - 1):i_tmp = 0
            j_tmp = j1-1
            if  (l2[j-1] > j1 - 1): j_tmp = 0
            if(l1[i1-1] == l1[i-1] and l2[j1-1]==l2[j-1]):
                cost=1
                if (T1[i1-1] == T2[j1-1]): cost = 0
                forestdist[i1, j1] = min(
                    min(forestdist[i_tmp,j1] + delete, forestdist[i1, j_tmp] + insert),
                    forestdist[i_tmp, j_tmp] + cost
                )
                treedist[i1,j1] = forestdist[i1,j1]
            else:
                i1_tmp = l1[i1-1] -1
                j1_tmp = l2[j1-1] -1

                i_tmp2 = i1_tmp
                if l1[i-1] > i1_tmp: i_tmp2 = 0
                j_tmp2 = j1_tmp
                if l2[j-1] > j1_tmp:j_tmp2 = 0
                forestdist[i1,j1] = min(
                    min(forestdist[i_tmp,j1] + delete, forestdist[i1, j_tmp] + insert),
                    forestdist[i_tmp2, j_tmp2] + treedist[i1,j1]
                )
    return forestdist[i,j]



if __name__=="__main__":
    test()