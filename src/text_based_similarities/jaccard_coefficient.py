import src.text_based_similarities.text_preprocessor as processor

# definition : |A∩B|/|A∪B|
from src import code_smell_detector


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
    path = "../../test"
    file1 = "Cache.java"
    file2 = "ALU.java"
    text1 = code_smell_detector.load_source_codes_del_comments(path, file1)
    text2 = code_smell_detector.load_source_codes_del_comments(path, file2)
    print(Jaccard_cofficient(text1, text2))