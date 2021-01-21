import numpy as np
from src import code_smell_detector
from src.text_based_similarities.text_preprocessor import load


# definition: the least edit operations from text to text


def Levenshtein_distance(text_1, text_2):
    dp = np.empty([len(text_1)+1, len(text_2)+1], dtype= int)
    len_1 = len(text_1)
    len_2 = len(text_2)
    for i in range(0, len_1+1):
        dp[i, 0] = 0
    for i in range(0, len_2+1):
        dp[0, i] = 0
    for i in range(1, len_1+1):
        for j in range(1, len_2+1):
            if text_1[i-1] == text_2[j-1] :
                dp[i, j] = dp[i-1, j-1] + 1
            else:
                dp[i, j] = max(dp[i-1, j], dp[i, j-1])
    return dp[len_1, len_2]


def Levenshtein_similarity(path1, path2):
    text_1 = load(path1)
    text_2 = load(path2)
    return Levenshtein_distance(text_1, text_2)/max(len(text_1), len(text_2))


if __name__ == "__main__":
    print(Levenshtein_similarity("../../data/test/java_file2.java", "../../data/test/java_file2.java"))