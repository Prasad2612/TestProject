package hacker.oop.todo;

import hacker.oop.Bird;

public class InheritanceEx {
    public static void main(String[] args) {
        Birdd bird = new Birdd();
        bird.walk();
        bird.fly();
        bird.sing();
    }


    public static class Birdd extends Animal {

        public void sing() {
            System.out.println("I am singing");
        }

        public void fly() {
            System.out.println("I am flying");
        }
    }
    public static class Animal {

        public void walk() {
            System.out.println("I am walking");
        }


    }

}
