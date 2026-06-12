import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class MergeTwoArrays {
    public static void main(String[] args) {
        int[] array1={1,5,3,2,8,5,1};
        int[] array2={3,7,9,2,4,7};

        int[] mergeToQuniqueArray= mergeArrayWithoutDuplicalte(array1, array2);
        int[] mergeToUniqueArray= mergeArrayWithoutDuplicalteUsingStreams(array1, array2);
        System.out.println(Arrays.toString(array1)+"\n Array2 :"+Arrays.toString(array2));
        System.out.println(Arrays.toString(mergeToQuniqueArray)+" \n Unique Array\n");
        System.out.println(Arrays.toString(mergeToUniqueArray));
    }

    private static int[] mergeArrayWithoutDuplicalteUsingStreams(int[] array1, int[] array2) {
        return IntStream.concat(Arrays.stream(array1),Arrays.stream(array2)).distinct().toArray();
    }

    private static int[] mergeArrayWithoutDuplicalte(int[] array1, int[] array2) {


        Set<Integer> uniqueElements=new HashSet<>();
        for(int element : array1){
            uniqueElements.add(element);
        }
        for(int element : array2){
            uniqueElements.add(element);
        }
        int[] mergedArray=new int[uniqueElements.size()];
        int i=0;
        for(Integer element:uniqueElements){
            mergedArray[i++]=element;
        }

        return mergedArray;
    }
}
