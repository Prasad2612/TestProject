package com.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestNewClass {
     //find the mim sum of array intergers
    //5+3=8 8+1=9 9+2=11 8+9+11=28
    //5+1=6 6+2=8 8+3=11
    //6+8+11=25

    public static void main(String[] args) {
        int[] number = {5,3,1,2};

     System.out.println("Minimum Sum of pairs "+calculateSum(number));

    }

    public static int calculateSum (int[] arr) {
        List<Integer> iSums = new ArrayList<>();

        int fsSum=0;
        int currentSum=arr[0] +arr[1];
        iSums.add(currentSum);

        for (int i = 2; i < arr.length; i++) {
            currentSum+=arr[i];
            iSums.add(currentSum);
        }

        for(int sum:iSums){
            fsSum+=sum;
        }
        return fsSum;
    }



    public static int findSumOfPairs(int[] arr) {

        int minSum=0;
   /*     if(arr==null||arr.length==0){
            return 0;
        }
        */
     //   Arrays.sort(arr);
        for (int i=0; i<arr.length; i++) {
     minSum +=arr[i];
        }
        return minSum;
    }
}
