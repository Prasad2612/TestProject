package hacker.bign;

import java.util.Scanner;

public class BigIntegerTest {
    public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
        Scanner sc = new Scanner(System.in);

        String oneStr = sc.nextLine();
        String twoStr = sc.nextLine();
        java.math.BigInteger one = new java.math.BigInteger(oneStr);
        java.math.BigInteger two = new java.math.BigInteger(twoStr);
        java.math.BigInteger sum = one.add(two);
        java.math.BigInteger product = one.multiply(two);

        sc.close();
        System.out.println(sum+"\n"+product);
    }
}
/* In this problem, you have to add and multiply huge numbers! These numbers are so big that you can't contain them in any ordinary data types like a long integer.

Use the power of Java's BigInteger class and solve this problem.

Input Format
There will be two lines containing two numbers,  and .

Constraints
 and  are non-negative integers and can have maximum  digits.

Output Format
Output two lines. The first line should contain , and the second line should contain . Don't print any leading zeros.

Sample Input
1234
20
Sample Output

1254
24680
Explanation
 */
