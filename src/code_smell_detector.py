from src import code_language_classifier
import re

java_keyword = ["for", "new", "int", "double", "else"]

java_path = "../data/java"
def deal_source_code_java():
    ret =code_language_classifier.load_data(java_path, "java")
    source_code_str = ret[0]
    return source_code_str



if __name__ == "__main__":
    deal_source_code_java()

