package hacker.strings.todo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ValidUsernameRegEx {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int numUsrNames = input.nextInt();
        List<String> userNamesList = new ArrayList<String>();

        for (int i = 0; i < numUsrNames; i++) {
            String userNames = input.next();
            userNamesList.add(userNames);
        }
        for (String value : userNamesList) {
            if (validateUserName(value)) {
                System.out.println("Valid");
            } else {
                System.out.println("InValid");
            }
        }

    }

    private static boolean validateUserName(String userNames) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{8,29}$");
        Matcher matcher = pattern.matcher(userNames);
        return matcher.matches();
    }
}
/*

public class ValidUsernameRegEx {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{8,29}$");

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            int numUsrNames = input.nextInt();
            // Consume the rest of the line after reading the integer.
            input.nextLine();

            for (int i = 0; i < numUsrNames; i++) {
                String userName = input.nextLine();
                if (validateUserName(userName)) {
                    System.out.println("Valid");
                } else {
                    System.out.println("InValid");
                }
            }
        }
    }

    private static boolean validateUserName(String userName) {
        // Use the pre-compiled Pattern object to create a Matcher.
        Matcher matcher = USERNAME_PATTERN.matcher(userName);
        return matcher.matches();

     */
/*   Scanner input = new Scanner(System.in);
        int numUsrNames = input.nextInt();
        List<String> userNamesList = new ArrayList<String>();

        for (int i = 0; i < numUsrNames; i++) {
            String userNames = input.next();
            userNamesList.add(userNames);
        }
        for (String value : userNamesList) {
            if (validateUserName(value)) {
                System.out.println("Valid");
            } else {
                System.out.println("InValid");
            }
        }

    }

    private static boolean validateUserName(String userNames) {
        Pattern UserPattern=Pattern.compile("^[a-zA-Z0-9_-]{8,29}$");
        //pattern = Pattern.compile("^[a-zA-Z0-9_-]{8,29}$");
        Matcher matcher = UserPattern.matcher(userNames);
        return matcher.matches();*//*

    }
}
*/


/* You are updating the username policy on your company's internal networking platform. According to the policy, a username is considered valid if all the following constraints are satisfied:

The username consists of  to  characters inclusive. If the username consists of less than  or greater than  characters, then it is an invalid username.
The username can only contain alphanumeric characters and underscores (_). Alphanumeric characters describe the character set consisting of lowercase characters , uppercase characters , and digits .
The first character of the username must be an alphabetic character, i.e., either lowercase character  or uppercase character .
For example:

Username	Validity
INVALID; Username length < 8 characters
VALID
VALID
INVALID; Username begins with non-alphabetic character
INVALID; '?' character not allowed
Update the value of regularExpression field in the UsernameValidator class so that the regular expression only matches with valid usernames.

Input Format

The first line of input contains an integer , describing the total number of usernames. Each of the next  lines contains a string describing the username. The locked stub code reads the inputs and validates the username.

Constraints

The username consists of any printable characters.
Output Format

For each of the usernames, the locked stub code prints Valid if the username is valid; otherwise Invalid each on a new line.

Sample Input 0

8
Julia
Samantha
Samantha_21
1Samantha
Samantha?10_2A
JuliaZ007
Julia@007
_Julia007
Sample Output 0

Invalid
Valid
Valid
Invalid
Invalid
Valid
Invalid
Invalid*/

/* Program executing with test failures
* public class ValidUsernameRegEx {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int numUsrNames = input.nextInt();
        List<String> userNamesList = new ArrayList<String>();

        for (int i = 0; i < numUsrNames; i++) {
            String userNames = input.next();
            userNamesList.add(userNames);
        }
        for (String value : userNamesList) {
            if (validateUserName(value)) {
                System.out.println("Valid");
            } else {
                System.out.println("InValid");
            }
        }

    }

    private static boolean validateUserName(String userNames) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{8,29}$");
        Matcher matcher = pattern.matcher(userNames);
        return matcher.matches();
    }
}
*/

