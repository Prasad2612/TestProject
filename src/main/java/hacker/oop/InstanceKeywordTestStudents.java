package hacker.oop;

import java.util.ArrayList;
import java.util.Scanner;

public class InstanceKeywordTestStudents {
    static String count(ArrayList myList) {
        int a = 0, b = 0, c = 0;
        for (int i = 0; i < myList.size(); i++) {
            Object element = myList.get(i);
            if (element instanceof Student) {
                a++;
            }

            if (element instanceof Rockstar) {
                b++;
            }
            if (element instanceof Hacker){
                c++;
            }
        }

        return Integer.toString(a)+" "+ Integer.toString(b)+" "+ Integer.toString(c);
    }

    public static void main(String[] args) {
        System.out.println("");
        ArrayList list = new ArrayList();
        Scanner sc = new Scanner(System.in);
        int t=sc.nextInt();
        for(int i=0;i<t;i++){
            String name=sc.next();
            if(name.equals("Student"))list.add(new Student());
            if(name.equals("Rockstar"))list.add(new Rockstar());
            if(name.equals ("Hacker"))list.add(new Hacker());
        }
        System.out.println(count(list));
    }
    }
class Student{}
class Rockstar{}
class Hacker{}