package hacker;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class TheCurrencyFormatter {

    public static void main(String[] args) {

        Scanner scanner=new Scanner(System.in);
        double payment = scanner.nextDouble();
        scanner.close();

        NumberFormat usFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String us=usFormat.format(payment);
        System.out.println("US: "+us);

        Locale indiaLocale=new Locale("en","IN");
        NumberFormat indFormat = NumberFormat.getCurrencyInstance(indiaLocale);
        String india=indFormat.format(payment);
        System.out.println("India: "+india);

        NumberFormat chinaFormat = NumberFormat.getCurrencyInstance(Locale.CHINA);
        String china=chinaFormat.format(payment);
        System.out.println("China: "+china);

        NumberFormat franceFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        String france=franceFormat.format(payment);
        System.out.println("France: "+france);
    }
/* Using Java 15
*  // Record to hold country and locale pairs (Java 14+)
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
*
* */

   /* public static void main(String[] args) {
        *//* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. *//*

        Scanner scanner=new Scanner(System.in);
        double payment=scanner.nextDouble();
        scanner.close();


        NumberFormat usFormat= NumberFormat.getCurrencyInstance(Locale.US);
        // Locale indLocale = new Locale("en", IN);
        var locales= Map.of(
                "US",Locale.US,
                "china",Locale.CHINA,
                "France", Locale.FRANCE,
                "India", new Locale("en","IN")) ;


        Function<Locale, String> formatCurrency= locale -> NumberFormat.getCurrencyInstance(locale).format(payment);

        //Generate formatted currencies using modern java features
        var currencies=Map.of("US", formatCurrency.apply(locales.get("US")),
                "India", formatCurrency.apply(locales.get("India")),
                "China", formatCurrency.apply(locales.get("China")),
                "France", formatCurrency.apply(locales.get("France"))
        );
        System.out.println("US: "+currencies.get("US"));
        System.out.println("India"+currencies.get("India"));
        System.out.println("China: "+currencies.get("China"));
        System.out.println("France: "+currencies.get("France"));

    }*/
}


/*
Given a double-precision number, , denoting an amount of money, use the NumberFormat class' getCurrencyInstance method to convert  into the US,
 Indian, Chinese, and French currency formats. Then print the formatted values as follows:

US: formattedPayment
India: formattedPayment
China: formattedPayment
France: formattedPayment
where  is  formatted according to the appropriate Locale's currency.

Note: India does not have a built-in Locale, so you must construct one where the language is en (i.e., English).

Input Format

A single double-precision number denoting .

Constraints

Output Format

On the first line, print US: u where  is  formatted for US currency.
On the second line, print India: i where  is  formatted for Indian currency.
On the third line, print China: c where  is  formatted for Chinese currency.
On the fourth line, print France: f, where  is  formatted for French currency.

Sample Input

12324.134
Sample Output

US: $12,324.13
India: Rs.12,324.13
China: ￥12,324.13
France: 12 324,13 €
Explanation

Each line contains the value of  formatted according to the four countries' respective currencies.
* */