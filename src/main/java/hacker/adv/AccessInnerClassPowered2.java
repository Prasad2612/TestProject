package hacker.adv;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Scanner;

public class AccessInnerClassPowered2 {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        int num=sc.nextInt();

        Inner inner= new Inner();
        Class<?>  innerClass = inner.getClass();
        Class<?>[] innerClassesOp = innerClass.getDeclaredClasses();
        Constructor<?> constructor = innerClass.getDeclaredConstructor(Inner.class);
        constructor.setAccessible(true);
        Object privateInstance = constructor.newInstance(inner);

        // Access powerof2 method
        Method method = innerClass.getDeclaredMethod("powerof2", int.class);
        method.setAccessible(true);
        String result = (String) method.invoke(privateInstance, num);

        // Output
        System.out.println(result);
        System.out.println("An instance of class: " + privateInstance.getClass().getCanonicalName() + " has been created");
    }
    static class Inner {
        private class Private {
            private String powerof2(int num) {
                return ((num & (num - 1)) == 0) ? num + " is power of 2" : num + " is not power of 2";
            }
        }
    }
}


/* You are given a class Solution and an inner class Inner.Private. The main method of class Solution takes an integer  as input. The powerof2 in class Inner.Private checks whether a number is a power of . You have to call the method powerof2 of the class Inner.Private from the main method of the class Solution.

Constraints

Sample Input

8
Sample Output

8 is power of 2
An instance of class: Solution.Inner.Private has been created */

/*
			//Write your code here

		System.out.println("An instance of class: " + o.getClass().getCanonicalName() + " has been created");

		}//end of try

		catch (DoNotTerminate.ExitTrappedException e) {
			System.out.println("Unsuccessful Termination!!");
		}
	}//end of main
	static class Inner{
		private class Private{
			private String powerof2(int num){
				return ((num&num-1)==0)?"power of 2":"not a power of 2";
			}
		}
	}//end of Inner

}//end of Solution

class DoNotTerminate { //This class prevents exit(0)

    public static class ExitTrappedException extends SecurityException {

		private static final long serialVersionUID = 1L;
    }

    public static void forbidExit() {
        final SecurityManager securityManager = new SecurityManager() {
            @Override
            public void checkPermission(Permission permission) {
                if (permission.getName().contains("exitVM")) {
                    throw new ExitTrappedException();
                }
            }
        };
        System.setSecurityManager(securityManager);
    }
}
	*/