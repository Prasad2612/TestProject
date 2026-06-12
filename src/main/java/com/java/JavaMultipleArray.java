package com.java;

import java.util.Arrays;

public class JavaMultipleArray {
    public static void main(String[] args) {
        int[] arr = new int[20];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i + 1;
        }
        System.out.println(Arrays.toString(arr));

        int sum2 = 0, sum3 = 0, sum4 = 0, sum5 = 0;
        for (int n : arr) {
            if (n % 2 == 0) {
                sum2 = sum2 + n;
            }
            if (n % 3 == 0) {
                sum3 += n;
            }
            if (n % 4 == 0) {
                sum4 += n;
            }
            if (n % 5 == 0) {
                sum5 += n;
            }
        }
        System.out.println("sum multiples of 2:" + sum2);
        System.out.println("sum multiples of 3:" + sum3);
        System.out.println("sum multiples of 4:" + sum4);
    }

}
