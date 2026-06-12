package hacker.ds;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JavaListTest {
    public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */


                Scanner scanner = new Scanner(System.in);

                // Read initial list
                int n = scanner.nextInt();
                List<Integer> list = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    list.add(scanner.nextInt());
                }

                // Read number of queries
                int q = scanner.nextInt();
                scanner.nextLine(); // Consume the remaining newline character

                // Process queries
                for (int i = 0; i < q; i++) {
                    String queryType = scanner.next();
                    if (queryType.equals("Insert")) {
                        int index = scanner.nextInt();
                        int value = scanner.nextInt();
                        list.add(index, value);
                    } else if (queryType.equals("Delete")) {
                        int index = scanner.nextInt();
                        list.remove(index);
                    }
                }

                // Print final list
                for (int item : list) {
                    System.out.print(item + " ");
                }
                System.out.println(); // Print a newline at the end

                scanner.close();


    }
}

/*
For this problem, we have  types of queries you can perform on a List:

Insert  at index :

Insert
x y
Delete the element at index :

Delete
x
Given a list, , of  integers, perform  queries on the list. Once all queries are completed, print the modified list as a single line of space-separated integers.

Input Format

The first line contains an integer,  (the initial number of elements in ).
The second line contains  space-separated integers describing .
The third line contains an integer,  (the number of queries).
The  subsequent lines describe the queries, and each query is described over two lines:

If the first line of a query contains the String Insert, then the second line contains two space separated integers , and the value  must be inserted into  at index .
If the first line of a query contains the String Delete, then the second line contains index , whose element must be deleted from .
Constraints



Each element in is a 32-bit integer.
Output Format

Print the updated list  as a single line of space-separated integers.

Sample Input

5
12 0 1 78 12
2
Insert
5 23
Delete
0
Sample Output

0 1 78 12 23
Explanation
L-[12,0,1,78,12]
Q0*- Insert 23 at index 5.
L0-[12,0,1,78,12,23]
 Q1-Delete the element at index 0.
L1-[0,1,78,12,23]
Having performed all  queries, we print  as a single line of space-separated integers.*/


/* public class JavaListQueries {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read initial list
        int n = scanner.nextInt();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(scanner.nextInt());
        }

        // Read number of queries
        int q = scanner.nextInt();
        scanner.nextLine(); // Consume the remaining newline character

        // Process queries
        for (int i = 0; i < q; i++) {
            String queryType = scanner.next();
            if (queryType.equals("Insert")) {
                int index = scanner.nextInt();
                int value = scanner.nextInt();
                list.add(index, value);
            } else if (queryType.equals("Delete")) {
                int index = scanner.nextInt();
                list.remove(index);
            }
        }

        // Print final list
        for (int item : list) {
            System.out.print(item + " ");
        }
        System.out.println(); // Print a newline at the end

        scanner.close();
    }
}*/