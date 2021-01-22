import os

relative_back_path = '../../src/function_classifier'
abs_back_path = os.path.abspath(relative_back_path)
def create_ast(path):
    path = os.path.abspath(path)
    name = path.rsplit("\\")[-1].split(".")[0]
    dir = path[0: path.rfind("\\")]
    os.chdir(dir)
    cmd = "srcml " + path + " -o " + name + ".xml"
    os.system(cmd)
    os.chdir(abs_back_path)


if __name__ == "__main__":
    create_ast("../../data/test/java_file2.java")