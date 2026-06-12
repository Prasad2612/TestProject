package hacker.strings.todo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MyRegExValidateIp {


    public static void main(String[] args) {

        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
        Scanner sc=new Scanner(System.in);
        List<String> ipAddresses=new ArrayList<>();
        int numberOfIPs=6;
        for(int i=0; i<numberOfIPs; i++) {

            String ip=sc.nextLine();
            ipAddresses.add(ip);
        }

        for(String ip : ipAddresses) {

            System.out.println();
        }
        System.out.println();
        for (String ip : ipAddresses) {
            System.out.println(isValidIPv4(ip));
        }
        sc.close();
    }


    static boolean isValidIPv4(String ip) {
        if (ip == null) {
            return false;
        }
        // Regex for a valid IPv4 address
        final String IPV4_REGEX = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

        final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);
        Matcher matcher = IPV4_PATTERN.matcher(ip);
        return matcher.matches();
    }
    }
     /*   Scanner sc = new Scanner(System.in);
        List<String> ipAddresses = new ArrayList<>();
        // String str=sc.nextLine();
        System.out.println("Enter IP addresses ");
        int numberOfIPs = 6;
        for (int i = 0; i < numberOfIPs; i++) {
            String ip = sc.nextLine();
            ipAddresses.add(ip);
        }
        System.out.println("Entered IP addresses");
        for (String ip : ipAddresses) {
            System.out.println(ip);
        }
        for (String ip : ipAddresses) {
            System.out.println(isValidIPv4(ip));
        }
        sc.close();
    }

    /*  static boolean isValidIPv4(String ip) {
        if (ip == null) {
            return false;
        }*/
        // Regex for a valid IPv4 address
/*        final String IPV4_REGEX = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

          final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);
        Matcher matcher = IPV4_PATTERN.matcher(ip);
        return matcher.matches();*/

    //}

//} */

/*
Write a class called MyRegex which will contain a string pattern. You need to write a regular expression and assign it to the pattern such that it can be used to validate an IP address. Use the following definition of an IP address:

IP address is a string in the form "A.B.C.D", where the value of A, B, C, and D may range from 0 to 255. Leading zeros are allowed. The length of A, B, C, or D can't be greater than 3.

Some valid IP address:
000.12.12.034
121.234.12.12
23.45.12.56

Some invalid IP address:
000.12.234.23.23
666.666.23.23
.213.123.23.32
23.45.22.32.
I.Am.not.an.ip
In this problem you will be provided strings containing any combination of ASCII characters. You have to write a regular expression to find the valid IPs.
Just write the MyRegex class which contains a String . The string should contain the correct regular expression.
(MyRegex class MUST NOT be public)

Sample Input
000.12.12.034
121.234.12.12
23.45.12.56
00.12.123.123123.123
122.23
Hello.IP

Sample Output

true
true
true
false
false
false
 */

/* class MyRegExValidateIp {


    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        List<String> ipAddresses=new ArrayList<>();
       // String str=sc.nextLine();
        System.out.println("Enter IP addresses ");
    int numberOfIPs=6;
    for(int i=0;i<numberOfIPs;i++) {
        String ip=sc.nextLine();
        ipAddresses.add(ip);
    }
    System.out.println("Entered IP addresses");
    for(String ip :  ipAddresses) {
        System.out.println(ip);
    }
        private static final String IPV4_REGEX =
                "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static final Pattern IPv4_Pattern = Pattern.compile(IPV4_REGEX);
   // String ip="(\\\\d{1,2}|(0|1)\\\\d{2}|2[0-4]\\\\d|25[0-5])";
   // String ip="(25[0-5]|2[0-4][0-9]|[01]?[0-9]{1,2})";

        private static boolean isValidIPv4(String ipAddress);
        return matcher.matches();

   //String pattern=ip+"\\."+ip+"."+"\\."+ip+".";
   String pattern=ip+ "\\." +ip+ "\\." +ip+ "\\." +ip;
        String patternIp = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
/*
     public MyRegExValidateIp() {
        this.pattern=patternIp+ "\\." +patternIp+ "\\." +patternIp+ "\\." +patternIp;
        } */

//            System.out.println("Pattern"+ pattern);
//            System.out.println("Pattern IP: "+pattern);
//
//    }
//            } */
