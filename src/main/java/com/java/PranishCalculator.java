package com.java;

import java.util.Scanner;

public class PranishCalculator {
    public static void main(String[] args) {
        System.out.println("Pranish Calculator");
        Scanner sc = new Scanner(System.in);
//        System.out.println("What operation you want to perform");
//        String operation = sc.nextLine();
        System.out.println("Enter First  Number : ");
        float num1  = sc.nextFloat();
        System.out.println("Enter Second  Number : ");
        float num2  = sc.nextFloat();
        sc.close();
        System.out.println(" "+num1 + " + " +num2+ " = " +(num1 + num2));
        System.out.println(" "+num1 + " - " +num2+ " = "+(num1 - num2));
        System.out.println(" "+num1 + " * " +num2+ " = "+(num1 * num2));
        System.out.println(" "+num1 + " / " +num2+ " = "+(num1 / num2));
        System.out.println(" "+num1 + " % " +num2+ " = "+(num1 % num2));
        double result = Math.pow(num1, num2);
        System.out.println("Result of Power is : "+result);
        System.out.println("Result of Sine is : "+Math.sin(result));
    }
}
