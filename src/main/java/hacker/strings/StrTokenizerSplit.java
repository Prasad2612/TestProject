package hacker.strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class StrTokenizerSplit {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();

        String delim= "[ !,?._'@]+";
       // String delim= "[ !,?._'@]";
        StringTokenizer st=new StringTokenizer(s, delim );
        System.out.println("Token Count : "+st.countTokens());

        while(st.hasMoreTokens()){
            System.out.println("\n"+st.nextToken());
        }

    }
}
/* public class StrTokenizerSplit {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();

        String[] strTokens = s.split(" [^A-Za-z]+");
        String delim= "[ !,?._'@]";
        StringTokenizer st=new StringTokenizer(s, delim );
        System.out.println("Token Count : "+st.countTokens());

        while(st.hasMoreTokens()){
            System.out.println("\n"+st.nextToken());
        }

        List<String> validTokens = new ArrayList<>();
        for (String token : strTokens) {
            if(!token.isEmpty()){
                validTokens.add(token);
            }
        }
        System.out.println(validTokens.size());
        for(String token : validTokens){
            System.out.println(token);
        }

        sc.close();
    }

}
*/
      /*  List<String> validTokens = new ArrayList<>();
        for (String token : strTokens) {
            if (!validTokens.contains(token)) {

                System.out.println(validTokens.size());

                // 5. Print each valid token on a new line
                for (String token : validTokens) {
                    System.out.println(token);
                }
            }
        }
    }*/



/* Given a string, , matching the regular expression [A-Za-z !,?._'@]+, split the string into tokens. We define a token to be one or more consecutive English alphabetic letters. Then, print the number of tokens, followed by each token on a new line.

Note: You may find the String.split method helpful in completing this challenge.

Input Format

A single string, .

Constraints

 is composed of any of the following: English alphabetic letters, blank spaces, exclamation points (!), commas (,), question marks (?), periods (.), underscores (_), apostrophes ('), and at symbols (@).
Output Format

On the first line, print an integer, , denoting the number of tokens in string  (they do not need to be unique). Next, print each of the  tokens on a new line in the same order as they appear in input string .

Sample Input

He is a very very good boy, isn't he?
Sample Output

10
He
is
a
very
very
good
boy
isn
t
he
Explanation

We consider a token to be a contiguous segment of alphabetic characters. There are a total of  such tokens in string , and each token is printed in the same order in which it appears in string .  */