package hacker.ds;

public class SubArrayDs {
    public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */



    }
}

/*  We define the following:

A subarray of an -element array is an array composed from a contiguous block of the original array's elements. For example, if , then the subarrays are , , , , , and . Something like  would not be a subarray as it's not a contiguous subsection of the original array.
The sum of an array is the total sum of its elements.
An array's sum is negative if the total sum of its elements is negative.
An array's sum is positive if the total sum of its elements is positive.
Given an array of  integers, find and print its number of negative subarrays on a new line.

Input Format

The first line contains a single integer, n, denoting the length of array A-[a0,a1,a2,...an-1].
The second line contains  space-separated integers describing each respective element, ai, in array A.

Constraints
1<=n<=100
-10pow4<=ai<=10pow4

Output Format
Print the number of subarrays of  having negative sums.

Sample Input
5
1 -2 4 -5 1

Sample Output
9
Explanation

There are nine negative subarrays of A: [1.-2,4,-5,1]

1. [1:1]=>-2
2. [3:3]=>-5
3. [0,1]=>1+-2--1
4. [2:3]=>4+-5-1
5. []=>-5+1-4
6.[]=>-2+4+-5--3
7.[]=>1+-2+4+-5--2
8.[]=>-2+4+-5+1--2
9.[]=>1+-2+4+5-5+1=-1

 */
