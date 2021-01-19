# 代码语言分类

## 概述

使用神经网络实现了对`java`,`python`,`javascript`三种语言的分类。

## 思路

三种语言各取1000份代码文件作为训练集，各取500份文件作为测试集。使用分词器选出训练集中出现频率最高的200个词，以是否出现这个词作为特征，使每个文件都能产生一个200维的特征向量。给各个文件打上标签，然后随机化处理，用神经网络训练，在10个`epoch`后得到的模型在测试集上的正确率大致为`98.2%`。

![Screenshot 2021-01-19 152753](https://draco-picbed.oss-cn-shanghai.aliyuncs.com/img/Screenshot 2021-01-19 152753.png)

![Figure_1](https://draco-picbed.oss-cn-shanghai.aliyuncs.com/img/Figure_1.png)

![Figure_2](https://draco-picbed.oss-cn-shanghai.aliyuncs.com/img/Figure_2.png)

## 数据来源

[Big Code](http://learnbigcode.github.io/datasets/)

