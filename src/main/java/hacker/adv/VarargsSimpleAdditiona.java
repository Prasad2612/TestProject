package hacker.adv;

import java.util.Scanner;

public class VarargsSimpleAdditiona {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        int[] input=new int[6];
        for(int i=0;i<input.length;i++)
        {
            input[i]=sc.nextInt();
        }
        Add adder=new Add();
        adder.add(input[0], input[1]);
        adder.add(input[0], input[1], input[2]);
        adder.add(input[0], input[1], input[2], input[3], input[4]);
        adder.add(input[0], input[1], input[2], input[3], input[4], input[5]);
    }
}
class Add
{

    public void add(int... numbers) {
        int sum=0;
        StringBuilder expression = new StringBuilder();
        for(int i=0;i<numbers.length;i++)
        {
            sum += numbers[i];
            expression.append(numbers[i]);
            if (i < numbers.length - 1) {
                expression.append("+");
            }
        }
        expression.append("=").append(sum);
        System.out.println(expression);
    }
}
/* You are given a class Solution and its main method in the editor.
Your task is to create the class Add and the required methods so that the code prints the sum of the numbers passed to the function add.

Note: Your add method in the Add class must print the sum as given in the Sample Output

Input Format

There are six lines of input, each containing an integer.

Output Format

There will be only four lines of output. Each line contains the sum of the integers passed as the parameters to add in the main method.

Sample Input

1
2
3
4
5
6
Sample Output

1+2=3
1+2+3=6
1+2+3+4+5=15
1+2+3+4+5+6=21*/