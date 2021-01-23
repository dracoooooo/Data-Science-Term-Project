/*
    首先对于n > 4的情况，肯定是越拆乘积越大的，这个可以通过二次函数来证明

    然后到了2和3的时候就不能拆下去了，因为拆出1的话只会越拆越小

    也就是说最后对于n >= 4的情况乘积一定是2^x*3^y（满足2x+3y==n）这样的一个形式

    然后我们可以通过取对数证明，当2x==3y时 ，有2^x < 3^y，即和一定，都是3的乘积比都是2要大

    所以可以得出结论，要尽可能的多拆出3，然后剩下的都是2

    time O(n) , space O(1)

    另外leetcode的编译器强制要求函数结尾一定要有返回值，但是我在这里去掉了，因为实际上到不了结尾

*/
class Solution {
public:
    int integerBreak(int n) {
        if(n == 2)  return 1;
        if(n == 3)  return 2;
        for(int i = n / 3;i >= 0;i--) {     //尽量拆出多的3
            if((n - 3 * i) % 2 == 0) {      //剩下的都是2
                return pow(3, i) * pow(2, (n - 3 * i) / 2);     //结果2^x*3^y（满足2x+3y==n）
            }
        }
    }
};

