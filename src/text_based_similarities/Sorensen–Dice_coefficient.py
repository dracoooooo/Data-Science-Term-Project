
# definition : s = 2|X∩Y| / （|X|+|Y|）


def Sorensen_Dice_coefficient(text_1, text_2):
    if text_1 == None and text_2 == None:
        return 1.0
    if text_1 == None or text_2 == None:
        return 0.0
    X = set(text_1)
    Y = set(text_2)
    intersaction = X.intersection(Y)
    return 2*intersaction.__len__()/(X.__len__() + Y.__len__())

if __name__=="__main__":
    print(Sorensen_Dice_coefficient("xsdsadaz", "xvwsdasas"))