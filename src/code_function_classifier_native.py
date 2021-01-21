import src.code_language_classifier as clc

# 计算代码文件数量
def count():
    java = clc.load_data("../data/leetcode", "java")
    print("java:", java.__len__())

if __name__ == "__main__":
    count()