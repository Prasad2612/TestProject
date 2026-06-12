package hacker.adv;

import java.util.Scanner;

public class FactoryPatternExample {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            if (!scanner.hasNextLine()) return;
            String order = scanner.nextLine().trim();
            FoodFactory factory = new FoodFactory();
            Food food = factory.getFood(order);
            if (food != null) {
                System.out.println("The factory returned class " + food.getClass().getSimpleName());
                System.out.println("Someone ordered " + food.getType());
            } else {
                System.out.println("Invalid order");
            }
        }
    }
}

class FoodFactory {
    public Food getFood(String order) {
        if (order == null) return null;
        switch (order.toLowerCase()) {
            case "pizza": return new Pizza();
            case "cake":  return new Cake();
            default:      return null;
        }
    }
}

 interface Food {
    String getType();
}

class Pizza implements Food {
    @Override
    public String getType() {
        return "Fast Food!";
    }
}

class Cake implements Food {
    @Override
    public String getType() {
        return "a Dessert!";
    }
}

/* According to Wikipedia, a factory is simply an object that returns another object from some other method call, which is assumed to be "new".

In this problem, you are given an interface Food. There are two classes Pizza and Cake which implement the Food interface, and they both contain a method getType().

The main function in the Main class creates an instance of the FoodFactory class. The FoodFactory class contains a method getFood(String) that returns a new instance of Pizza or Cake according to its parameter.

You are given the partially completed code in the editor. Please complete the FoodFactory class.

Sample Input 1

cake
Sample Output 1

The factory returned class Cake
Someone ordered a Dessert!
Sample Input 2

pizza
Sample Output 2

The factory returned class Pizza
Someone ordered Fast Food!

The factory returned class Pizza
Someone ordered a Fast Food!

*/

