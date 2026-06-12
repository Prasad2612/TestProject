package hacker.strings;

import java.util.Scanner;

public class StringPalindromeCheck {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        sc.close();
        str=str.toLowerCase();
        System.out.println("String to verify palindrome: " +str);

        String reverseStr=new StringBuilder(str).reverse().toString();
        if(str.equals(reverseStr)) {
            System.out.println("Yes");
        } else  {
            System.out.println("No");
        }

    }
}



/* A palindrome is a word, phrase, number, or other sequence of characters which reads the same backward or forward.

Given a string , print Yes if it is a palindrome, print No otherwise.

Constraints

 will consist at most  lower case english letters.
Sample Input

madam
Sample Output

Yes */