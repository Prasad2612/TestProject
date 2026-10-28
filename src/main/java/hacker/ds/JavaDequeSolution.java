package hacker.ds;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class JavaDequeSolution {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());

        Deque<Integer> dq = new ArrayDeque<>();
        Map<Integer, Integer> map = new HashMap<>();

        int maxUnique = 0;
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < n; i++) {
            int num = Integer.parseInt(st.nextToken());
                    // 1. Add the incoming element to the sliding window (tail)
            dq.addLast(num);
            map.put(num, map.getOrDefault(num, 0) + 1);
                    // 2. Shrink the window from the front (head) if it exceeds size M
            if (dq.size() > m) {
                int removed = dq.removeFirst();
                map.put(removed, map.get(removed) );
                if (map.get(removed) == 1) {
                    map.remove(removed);
                }
                else  {
                    map.put(removed, map.get(removed) - 1);
                }
            }
                    // 3. Update the maximum number of unique integers
                if(dq.size() == m) {
                    maxUnique = Math.max(maxUnique, map.size());
                    // Optimization shortcut: If max possible unique elements are achieved, stop evaluating
                    if (maxUnique == m) {
                        break;
                    }
                }
        }

    }

}

/* In computer science, a double-ended queue (dequeue, often abbreviated to deque, pronounced deck) is an abstract data type that generalizes a queue,
for which elements can be added to or removed from either the front (head) or back (tail).

Deque interfaces can be implemented using various types of collections such as LinkedList or ArrayDeque classes. For example, deque can be declared as:

Deque deque = new LinkedList<>();
or
Deque deque = new ArrayDeque<>();
You can find more details about Deque here. http://docs.oracle.com/javase/7/docs/api/java/util/Deque.html

In this problem, you are given  integers. You need to find the maximum number of unique integers among all the possible contiguous subarrays of size M.

Note: Time limit is 3 second for this problem.

Input Format

The first line of input contains two integers N and M : representing the total number of integers and the size of the subarray, respectively.
The next line contains N space separated integers.

Constraints
1<=N<=100000
1<=M<=100000
M<=N
The numbers in the array will range between [0, 10000000].

Output Format

Print the maximum number of unique integers among all possible contiguous subarrays of size M.

Sample Input

6 3
5 3 5 2 3 2
Sample Output

3
Explanation

In the sample testcase, there are 4 subarrays of contiguous numbers.

s1 = (5,3,5) - Has  unique numbers.

s2 = (3,5,2) - Has  unique numbers.

s3 = (5,2,3) - Has  unique numbers.

s4 = (2,3,2)- Has  unique numbers.

In these subarrays, there are 2,3,3,2 unique numbers, respectively. The maximum amount of unique numbers among all possible contiguous subarrays is 3.
*/