package hacker.adv;

import java.util.Scanner;

public class LambdaExpressionTestOp {
    public static void main(String[] args) throws Exception {
        MyMath myMath = new MyMath();
        Scanner sc = new Scanner(System.in);
        int  T = sc.nextInt();

        while(T-->0){
            String[] numbs=sc.next().split(" ");
            int opCode=Integer.parseInt(numbs[0]);
            int m=Integer.parseInt(numbs[1]);
            PerformOperation op;
            boolean result;

            switch (opCode){
                case 1:
                     op=myMath.isOdd();
                     result=myMath.checker(op, m);
                    System.out.println(result ? "ODD" : "EVEN");
                    break;
                case 2:
                    op=myMath.isPrime();
                    result=myMath.checker(op, m);
                    System.out.println(result ? "PRIME" : "COMPOSITE");
                    break;
                case 3:
                    op=myMath.isPalindrome();
                    result=myMath.checker(op, m);
                    System.out.println(result ? "PALINDROME" : "NOT PALINDROME");
                    break;
            }

        }

    }
}

class MyMath {
    public PerformOperation isOdd() {
        return a -> a % 2 != 0;
    }

    public PerformOperation isPrime() {
        return a -> {
            if (a < 2) return false;
            for (int i = 2; i < Math.sqrt(a); i++) {
                if (a % i == 0) return false;
            }
            return true;
        };
    }

    public boolean checker(PerformOperation op, int m) {
        return op.check(m);
    }

    public PerformOperation isPalindrome() {
        return a->{
            String str = String.valueOf(a);
            return str.equals(new StringBuilder(str).reverse().toString());
        };
    }
}

interface PerformOperation{
    boolean check(int a);
}

/* Write the following methods that return a lambda expression performing a specified action:

PerformOperation isOdd(): The lambda expression must return  if a number is odd or  if it is even.
PerformOperation isPrime(): The lambda expression must return  if a number is prime or  if it is composite.
PerformOperation isPalindrome(): The lambda expression must return  if a number is a palindrome or  if it is not.
Input Format

Input is handled for you by the locked stub code in your editor.

Output Format

The locked stub code in your editor will print  lines of output.

Sample Input

The first line contains an integer,  (the number of test cases).

The  subsequent lines each describe a test case in the form of  space-separated integers:
The first integer specifies the condition to check for ( for Odd/Even,  for Prime, or  for Palindrome). The second integer denotes the number to be checked.

5
1 4
2 5
3 898
1 3
2 12
Sample Output

EVEN
PRIME
PALINDROME
ODD
COMPOSITE */


/*
public class Solution {

 public static void main(String[] args) throws IOException {
  MyMath ob = new MyMath();
  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  int T = Integer.parseInt(br.readLine());
  PerformOperation op;
  boolean ret = false;
  String ans = null;
  while (T--> 0) {
   String s = br.readLine().trim();
   StringTokenizer st = new StringTokenizer(s);
   int ch = Integer.parseInt(st.nextToken());
   int num = Integer.parseInt(st.nextToken());
   if (ch == 1) {
    op = ob.isOdd();
    ret = ob.checker(op, num);
    ans = (ret) ? "ODD" : "EVEN";
   } else if (ch == 2) {
    op = ob.isPrime();
    ret = ob.checker(op, num);
    ans = (ret) ? "PRIME" : "COMPOSITE";
   } else if (ch == 3) {
    op = ob.isPalindrome();
    ret = ob.checker(op, num);
    ans = (ret) ? "PALINDROME" : "NOT PALINDROME";

   }
   System.out.println(ans);
  }
 }
}

 */