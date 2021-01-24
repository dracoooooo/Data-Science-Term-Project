import os
import pickle
import numpy as np
import matplotlib.pyplot as plt
from sklearn.cluster import SpectralClustering
from sklearn.preprocessing import StandardScaler, normalize
from sklearn.decomposition import PCA
from sklearn.metrics import silhouette_score
from sklearn import metrics
from src.text_based_similarities.Levenshtein_distance import Levenshtein_similarity
from src.text_based_similarities.cos_similarity import cos_similarity
from src.text_based_similarities.jaccard_coefficient import Jaccard_cofficient
from src.text_based_similarities.Sorensen_Dice_coefficient import Sorensen_Dice_coefficient
from src.token_based_similarities.token_lcs import token_lcs
from src.languange_classifier.code_language_classifier import suffix

# 使用谱聚类算法(spectral clustering)通过代码相似度对代码分类


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
    path = "../../data/leetcode/raw"
    files = file_paths(path, "java")
    len = files.__len__()
    matrix = np.empty([len, len])
    for i in range(len):
        for j in range(i,len):
            matrix[j][i] = matrix[i][j] = similarity_function(files[i], files[j])
            print(i, j)

    f = open('similarity_matrix_' + similarity_function.__name__ + '.pkl', 'wb')
    pickle.dump(matrix, f)
    f.close()
    return matrix


def initSC(similarity_matrix_path):
    sc = SpectralClustering(n_clusters=3, affinity='precomputed', assign_labels='discretize')
    f1 = open(similarity_matrix_path, 'rb')
    similarity_matrix = pickle.load(f1)
    f1.close()
    print(similarity_matrix)
    plt.scatter(similarity_matrix[:,0], similarity_matrix[:,1], marker = 'o')
    plt.show()
    y_pred = sc.fit_predict(similarity_matrix)
    # print("Calinski-Harabasz Score", metrics.calinski_harabaz_score(similarity_matrix, y_pred))
    plt.scatter(similarity_matrix[:,2], similarity_matrix[:,1], c = y_pred)
    plt.show()
    return sc


if __name__ == "__main__":
    path = "../../data/leetcode/raw"
    files = file_paths(path, "java")
    sc = initSC('./similarity_matrix_Jaccard_cofficient.pkl')
    labels = sc.labels_
    dic = {}
    for i in range(files.__len__()):
        dic[files[i]] = labels[i]
    for i in dic.items():
        print(i)
    # score = silhouette_score(files, labels)
    # print(score)
