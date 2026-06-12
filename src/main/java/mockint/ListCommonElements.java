package mockint;

import java.util.*;
import java.util.stream.Collectors;

public class ListCommonElements {
    public static void main(String[] args) {

        extractedCommon(); //Cleanest Approach

        extractCommonForLargeLists();

        extractCommonElementsByStream(); //Cleanest Approach

    }

    private static void extractCommonElementsByStream() {
        System.out.println("extractCommonElementsByStream");

        List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> list2 = Arrays.asList(3, 4, 5, 6, 7);
        List<Integer> list3 = Arrays.asList(5, 4, 8, 9, 10);

        List<Integer> common = list1.stream()
                .filter(list2::contains)
                .filter(list3::contains)
                .distinct() // To avoid duplicates
                .collect(Collectors.toList());
        System.out.println(common);

    }

    private static void extractCommonForLargeLists() {

   /*          The retainAll() method on a regular ArrayList takes \(O(N \times M)\) time because it performs sequential lookups. If you have large lists, you should convert the lists into HashSet objects first. This drops lookups to an optimal O(1) time complexity. */
        System.out.println("extractCommonForLargeLists --> Most efficient O(N)");

        List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> list2 = Arrays.asList(3, 4, 5, 6, 7);
        List<Integer> list3 = Arrays.asList(5, 4, 8, 9, 10);
        // Convert lists to HashSets for O(1) lookups
        Set<Integer> set1 = new HashSet<>(list1);
        Set<Integer> set2 = new HashSet<>(list2);
        Set<Integer> set3 = new HashSet<>(list3);

        set1.retainAll(set2);
        set1.retainAll(set3);

        System.out.println(set1);
    }

    private static void extractedCommon() {
        System.out.println("extractedCommon -->  O(N2)");

        /*        This approach creates a copy of the first list and iteratively keeps only the elements present in the second and third lists. To avoid duplicate results in the final output, you can pass the list into a HashSet */
        List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> list2 = Arrays.asList(3, 4, 5, 6, 7);
        List<Integer> list3 = Arrays.asList(5, 4, 8, 9, 10);

        List<Integer> common=new ArrayList<>(list1);
        System.out.println(common);

        common.retainAll(list2);
        common.retainAll(list3);



        Set<Integer> uniqueCommon = new HashSet<>(list1);

        System.out.println(uniqueCommon);
        uniqueCommon.retainAll(list2);
        System.out.println(uniqueCommon);

        uniqueCommon.retainAll(list3);
        System.out.println(uniqueCommon);
    }
}
