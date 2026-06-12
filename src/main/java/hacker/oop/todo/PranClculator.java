package hacker.oop.todo;

import java.util.Scanner;

public class PranClculator {
    public static void main(String[] args) {
        System.out.println("Calculator program enter inputs");
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Number 1 \n");
        float num1 = sc.nextFloat();
        System.out.println("Enter Number 2 \n ");
        float num2 = sc.nextFloat();
        System.out.println("Enter one symbol:  +,  -,  *,  /,  ");

        String operation = sc.next();

        switch (operation) {

            case "+":
                System.out.println("The addition of : "+num1+ " "+num2+" is : \n" +(num1 + num2));
                break;
            case "-":
                System.out.println(num1 / num2);
                break;
            case "/" :
            System.out.println(num1 / num2);
            break;
            case  "*":
                System.out.println(num1 / num2); break;
        }

    }
}
