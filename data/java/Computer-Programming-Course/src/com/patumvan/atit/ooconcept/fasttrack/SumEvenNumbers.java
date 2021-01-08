
package com.patumvan.atit.ooconcept.fasttrack;

public class SumEvenNumbers {

    public static void main(String[] args) {
       int sum = 0;
       for(int  i =1; i<=100;i++){
           if (i%2 == 0){
               sum += i;
           }
       }
       System.out.println("Sum = "+ sum);
       System.out.println("Sum = "+sumEven(1,100));
    }
    
    public static int sumEven(int start, int end){
        if (start%2!=0) start++;
        if (end%2!=0) end--;
        int term = (((end-start)/2)+1);
        int sum = ((start+end)*term)/2;
        return sum;
    } 
}
