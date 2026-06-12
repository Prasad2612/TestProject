import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Solution {
    // Record to hold country and locale pairs (Java 14+)
    public record CountryLocale(String country, Locale locale) {}

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var payment = scanner.nextDouble();
        scanner.close();

        // Using List of records for cleaner data structure
        var countryLocales = List.of(
                new CountryLocale("US", Locale.US),
                new CountryLocale("India", new Locale("en", "IN")),
                new CountryLocale("China", Locale.CHINA),
                new CountryLocale("France", Locale.FRANCE)
        );

        // Stream-based processing with method references
        countryLocales.stream()
                .map(cl -> formatCountryCurrency(cl, payment))
                .forEach(System.out::println);
    }

    private static String formatCountryCurrency(CountryLocale cl, double amount) {
        var formatter = NumberFormat.getCurrencyInstance(cl.locale());
        return cl.country() + ": " + formatter.format(amount);
    }
}

/*

public class ThreadSafeSingleton {

    privatre stativ ThreadSafeSingleton inbstace;

    private ThreadSafeSingleton() {
        if(inbstace !==null) {
            throw new IllegalStateException("Single ")
        }
    }


    public static ThreadSafeSingleton getInstance() {
        if(instance==nul) {
            synchronized ( ) {
                instancce = new ThreadSafeSingleton()
            }
        }
    }

    retrun instacne
}*/
