from src.clustering.cluster import compute_similarity_matrix
from src.text_based_similarities.Levenshtein_distance import Levenshtein_similarity
from src.text_based_similarities.cos_similarity import cos_similarity
from src.text_based_similarities.jaccard_coefficient import Jaccard_cofficient
from src.text_based_similarities.Sorensen_Dice_coefficient import Sorensen_Dice_coefficient
from src.token_based_similarities.token_lcs import token_lcs

list = [Jaccard_cofficient, cos_similarity, Levenshtein_similarity, Sorensen_Dice_coefficient, token_lcs]
if __name__ == "__main__":
    for i in list:
        compute_similarity_matrix(i)