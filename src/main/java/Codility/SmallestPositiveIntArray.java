package Codility;
/*
Write a function:

class Solution { public int solution(int[] A); }
content_copy

that, given an array A of N integers, returns the smallest positive integer (greater than 0) that does not occur in A.

        For example, given A = [1, 3, 6, 4, 1, 2], the function should return 5.

Given A = [1, 2, 3], the function should return 4.

Given A = [−1, −3], the function should return 1.

Write an efficient algorithm for the following assumptions:

N is an integer within the range [1..100,000];
each element of array A is an integer within the range [−1,000,000..1,000,000].
*/

public class SmallestPositiveIntArray {
    public static int solution(int[] A) {
        int n = A.length;
        boolean[] present = new boolean[n + 1];

        for (int i = 0; i < n; i++) {
            if (A[i] > 0 && A[i] <= n) {
                present[A[i]] = true;
            }
        }

        for (int i = 1; i <= n; i++) {
            if (!present[i]) {
                return i;
            }
        }

        return n + 1;
    }

    public static void main(String[] args) {
        int[] A1 = {1, 3, 6, 4, 1, 2};
        System.out.println(solution(A1)); // Output: 5

        int[] A2 = {1, 2, 3};
        System.out.println(solution(A2)); // Output: 4

        int[] A3 = {-1, -3};
        System.out.println(solution(A3)); // Output: 1

        
        System.out.println(solutionSet(A1)); // Output: 5\
        System.out.println(solutionSet(A2)); // Output: 4        
        System.out.println(solutionSet(A3)); // Output: 1
    }

    private static Object solutionSet(int[] a1) {
        java.util.Set<Integer> set = new java.util.HashSet<>();
        for (int num : a1) {
            if (num > 0) {
                set.add(num);
            }
        }

        int smallestPositive = 1;
        while (set.contains(smallestPositive)) {
            smallestPositive++;
        }

        return smallestPositive;
    }
}
