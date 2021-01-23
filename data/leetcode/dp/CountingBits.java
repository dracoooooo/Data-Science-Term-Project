public class CountingBits {
    public int[] countBits(int num) {
        int arr[] = new int[num+1];
        arr[0] = 0;
        if(num == 0){
            return arr;
        }
        arr[1] = 1;
        for(int index = 2; index <= num; index++){
            int count = 0;
            int number = index;
            while(number > 0){
                number &= (number - 1);
                count++;
            }
            arr[index] = count;
        }
        return arr;
    }
}
