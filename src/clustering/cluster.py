import os
import numpy as np
import matplotlib.pyplot as plt
from sklearn.cluster import SpectralClustering
from sklearn.preprocessing import StandardScaler, normalize
from sklearn.decomposition import PCA
from sklearn.metrics import silhouette_score
from src.text_based_similarities.Levenshtein_distance import Levenshtein_similarity
from src.text_based_similarities.cos_similarity import cos_similarity
from src.text_based_similarities.jaccard_coefficient import Jaccard_cofficient
from src.token_based_similarities.token_lcs import token_lcs
from src.languange_classifier.code_language_classifier import suffix

# 使用谱聚类算法(spectral clustering)通过代码相似度对代码分类

# data_path = "../data/leetcode/raw"


def file_paths(data_path, language):
    ret = []
    for dirpath, dirnames, filenames in os.walk(data_path):
        for file in filenames:
            if file.endswith(suffix[language]):
                path = dirpath + "\\" + file
                # print(path)
                ret.append(path)
    return ret


def compute_similarity_matrix(similarity_function):
    path = "../data/leetcode/raw"
    files = file_paths(path, "java")
    len = files.__len__()
    matrix = np.empty([len, len])
    for i in range(len):
        for j in range(len):
            matrix[i][j] = similarity_function(files[i], files[j])
            print(i, j)
    return matrix


def test():
    # SpectralClustering(n_clusters=8,
    #                    eigen_solver='arpack',
    #                    n_init=10,
    #                    gamma=1.0,
    #                    affinity='rbf',
    #                    n_neighbors=10)
    return 1

if __name__ == "__main__":
    sc = SpectralClustering(n_clusters=2, affinity='precomputed',
                                            assign_labels='discretize')
    similarity_matrix = compute_similarity_matrix(Jaccard_cofficient)
    print(similarity_matrix)
    # sc.fit_predict(similarity_matrix)