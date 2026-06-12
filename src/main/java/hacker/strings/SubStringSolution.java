package hacker.strings;

import java.util.Scanner;

public class SubStringSolution {
    public static void main(String[] args) {
        Scanner in =new Scanner(System.in);
        String s=in.next();;
        int start=in.nextInt();
        int end=in.nextInt();

        String res=s.substring(start,end);

        System.out.println(res);
    }

}


/* Given a string, , and two indices,  and , print a substring consisting of all characters in the inclusive range from  to . You'll find the String class' substring method helpful in completing this challenge.

Input Format

The first line contains a single string denoting .
The second line contains two space-separated integers denoting the respective values of  and .

Constraints

String  consists of English alphabetic letters (i.e., ) only.
Output Format

Print the substring in the inclusive range from  to .

Sample Input

Helloworld
3 7
Sample Output

lowo
Explanation

In the diagram below, the substring is highlighted in green:

substring.png */