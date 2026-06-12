package hacker.strings;

import java.util.Arrays;
import java.util.Scanner;

public class CheckStringAnagram {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String a = sc.nextLine();
        String b = sc.nextLine();
        sc.close();

        boolean result = isAnagram(a, b);
        if (result) {
            System.out.println("Anagrams");
        } else {
            System.out.println("Not Anagrams");
        }
    }

    private static boolean isAnagram(String a, String b) {

        String lowerA = a.toLowerCase();
        String lowerB = b.toLowerCase();

        if (lowerA.length() != lowerB.length()) {
            return false;
        }

        char[] charArrayA = lowerA.toCharArray();
        char[] charArrayB = lowerB.toCharArray();

        Arrays.sort(charArrayA);
        Arrays.sort(charArrayB);

        return Arrays.equals(charArrayA, charArrayB);

    }
}
