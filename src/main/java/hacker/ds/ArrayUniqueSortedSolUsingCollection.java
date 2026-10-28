package hacker.ds;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class ArrayUniqueSortedSolUsingCollection {
    public static void main(String[] args) {
      //  int[] arr = {1, 2, 2, 3, 4, 4, 5};
List<Integer> input  = Arrays.asList(1,5,  22, 2, 22, 3, 34, 34, 55);
input.stream().distinct().forEach(element -> System.out.println(""+element));

    TreeSet<Integer> set = new TreeSet<>(input);
System.out.println("Output of Set"+set);

        // Print the unique sorted array
//        for (int num : uniqueSortedArr) {
//            System.out.print(num + " ");

        int[] numbers = {1, 8, 5, 22, 2, 9, 22, 3, 34, 34, 5, 7, 55};
        int[] uniqueSortedArr = Arrays.stream(numbers).distinct().sorted().toArray();
        System.out.println("Array Stream output"+Arrays.toString(uniqueSortedArr));
//        }
    }
}
