 // 冒泡排序
 // 平均时间复杂度:O(N^2)
 // 最坏情况复杂度:O(N^2)
 // 空间复杂度:O(1)
 // 稳定排序
void bubblesort(vector<int>& a)
 { 
    int n = a.size() ; 
    for (int i = 0 ;  i < n ;  i++)
     { 
        for (int j = 0 ;  j < n - 1 - i ;  j++)
         { 
            if (a[j] > a[j+1])
                swap(a[j], a[j+1]) ; 
         } 
     } 
 } 