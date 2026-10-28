package hacker.ds;

public class ArrayUniqueSortedSol {
    public static void main(String[] args) {
        int[] arr = {1, 2, 2, 3, 4, 4, 5};
        int[] uniqueSortedArr = getUniqueSortedArray(arr);

        // Print the unique sorted array
        for (int num : uniqueSortedArr) {
            System.out.print(num + " ");
        }
    }

    public static int[] getUniqueSortedArray(int[] arr) {
        // Sort the array
        java.util.Arrays.sort(arr);

        // Use a list to store unique elements
        java.util.List<Integer> uniqueList = new java.util.ArrayList<>();

        // Add unique elements to the list
        for (int i = 0; i < arr.length; i++) {
            if (i == 0 || arr[i] != arr[i - 1]) {
                uniqueList.add(arr[i]);
            }
        }

        // Convert the list back to an array
        int[] uniqueArr = new int[uniqueList.size()];
        for (int i = 0; i < uniqueList.size(); i++) {
            uniqueArr[i] = uniqueList.get(i);
        }

        return uniqueArr;
    }


}
