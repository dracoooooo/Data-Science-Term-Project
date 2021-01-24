import src.text_based_similarities.text_preprocessor as processor
from src.text_based_similarities.text_preprocessor import load


# definition : |A∩B|/|A∪B|


def Jaccard_cofficient(path1, path2):
    text_1 = load(path1)
    text_2 = load(path2)
    if text_1 == None and text_2 == None:
        return 1.0
    if text_1 == None or text_2 == None:
        return 0.0
    text_1 = processor.process_text(text_1)
    text_2 = processor.process_text(text_2)
    A = set(text_1)
    B = set(text_2)
    intersaction = A.intersection(B)
    union = A.union(B)
    return intersaction.__len__() / union.__len__()


if __name__=="__main__":
    print(Jaccard_cofficient("../../data/test/java_file2.java", "../../data/test/java_file2.java",))