package hacker.ds;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
//Try to solve the test scenarios
public class TestHashSetCS {
    public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */

       /* Scanner sc = new Scanner(System.in);
     int t= sc.nextInt();
     sc.nextLine();
     String[] one=new String[t];
     String[] two=new String[t];

     for(int i=0;i<t;i++) {
         one[i]=sc.next();
         two[i]=sc.next();
     }
     Set<String> uniquePairs=new HashSet<>();

     for(int i=0;i<t;i++) {
         String pair=one[i]+""+two[i];
         uniquePairs.add(pair);
         System.out.println(uniquePairs.size());
     }*/
        Scanner sc = new Scanner(System.in);
        int t = Integer.parseInt(sc.nextLine()); // Read the integer as a line and parse it

        String[] one = new String[t];
        String[] two = new String[t];

        for (int i = 0; i < t; i++) {
            String[] tokens = sc.nextLine().split(" ");
            one[i] = tokens[0];
            two[i] = tokens[1];
        }

        Set<String> uniquePairs = new HashSet<>();

        for (int i = 0; i < t; i++) {
            String pair = one[i] + "" + two[i];
            uniquePairs.add(pair);
            System.out.println(uniquePairs.size());
        }
        sc.close();
    }
}
/*  In computer science, a set is an abstract data type that can store certain values, without any particular order, and no repeated values(Wikipedia).  is an example of a set, but  is not a set. Today you will learn how to use sets in java by solving this problem.

You are given  pairs of strings. Two pairs  and  are identical if  and . That also implies  is not same as . After taking each pair as input, you need to print number of unique pairs you currently have.

Complete the code in the editor to solve this problem.

Input Format

In the first line, there will be an integer  denoting number of pairs. Each of the next  lines will contain two strings seperated by a single space.

Constraints:

Length of each string is atmost  and will consist lower case letters only.
Output Format

Print  lines. In the  line, print number of unique pairs you have after taking  pair as input.

Sample Input

5
john tom
john mary
john tom
mary anna
mary anna
Sample Output

1
2
2
3
3
Explanation

After taking the first input, you have only one pair: (john,tom)
After taking the second input, you have two pairs: (john, tom) and (john, mary)
After taking the third input, you still have two unique pairs.
After taking the fourth input, you have three unique pairs: (john,tom), (john, mary) and (mary, anna)
After taking the fifth input, you still have three unique pairs. */