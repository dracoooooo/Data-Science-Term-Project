//堆排序
//建堆的平均时间是：O(N)
//建堆的最坏情况是：O(NlogN)
//删除元素的时间是：O(logN)
//整个排序平均时间复杂度：O(N+NlogN)=O(NlogN)
//最坏情况复杂度：O(NlogN)
//不稳定排序

//建立一个大顶堆O(n),要求就是 把最大的元素 移动到堆顶 也就是a[0]
void make_heap(vector<int>& a, int size) //size的当前堆的大小，也就是数组的前size个数
{
    for (int i = size - 1; i > 0; i--)
    {
        if (i % 2 && a[i] > a[(i - 1) / 2])//奇数
            swap(a[i], a[(i - 1) / 2]);
        else if (i % 2 == 0 && a[i] > a[(i - 2) / 2])//偶数
            swap(a[i], a[(i - 2) / 2]);
    }
}
void heapsort(vector<int>& a)
{
    int n = a.size();
    while (n)
    {
        make_heap(a, n); //每次把新的最大元素移到堆顶，也就是a[0]
        n--;
        swap(a[0], a[n]); //然后把当前最大移动到后面来作为排好序的元素
    }
}