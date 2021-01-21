

# definition : |A∩B|/|A∪B|


def Jaccard_cofficient(text_1, text_2):
    if text_1 == None and text_2 == None:
        return 1.0
    if text_1 == None or text_2 == None:
        return 0.0
    A = set(text_1)
    B = set(text_2)
    intersaction = A.intersection(B)
    union = A.union(B)
    return intersaction.__len__() / union.__len__()


if __name__=="__main__":
    print(Jaccard_cofficient("xyz", "xvw"))