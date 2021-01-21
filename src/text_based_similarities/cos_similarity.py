from src import code_smell_detector
from src.text_based_similarities.text_preprocessor import load
import numpy as np

# definition the cosθ of two vectors [0, 1]


def cos_similarity(path1, path2):
    text_1 = load(path1)
    text_2 = load(path2)
    A = set(text_1)
    B = set(text_2)
    union = A.union(B)
    list = []
    vectorA = np.zeros(union.__len__(), dtype=np.int)
    vectorB = np.zeros(union.__len__(), dtype= np.int)
    for i in union:
        list.append(i)
    for i in range(0, len(text_1)):
        for j in range(0, union.__len__()):
            if text_1[i] == list[j] :
                vectorA[j]+=1
    for i in range(0, len(text_2)):
        for j in range(0, union.__len__()):
            if text_2[i] == list[j] :
                vectorB[j]+=1
    p1=p2=p3=0
    for i in range(0, vectorA.__len__()):
        p1 += vectorA[i] * vectorB[i];
    for i in range(0, vectorA.__len__()):
        p2 += vectorA[i] * vectorA[i];
    p2 = np.sqrt(p2)
    for i in range(0, vectorA.__len__()):
        p3 += vectorB[i] * vectorB[i];
    p3 = np.sqrt(p3)

    return p1/(p2*p3)


if __name__== "__main__":
    print(cos_similarity("../../data/test/java_file2.java", "../../data/test/java_file2.java"))