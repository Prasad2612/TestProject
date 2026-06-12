package hacker.adv;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

import static hacker.adv.DoNotTerminate.*;

public class CanYouAccess {
    public static void main(String[] args) {
    DoNotTerminate.forbidExit();
    try (Scanner sc = new Scanner(System.in)) {
        int num = sc.nextInt();
        Inner inner = new Inner();
        Class<?> privateClass = inner.getClass();
        for(Class<?> c : Inner.class.getDeclaredClasses()) {
            if (c.getSimpleName().equals("Private")) {
                privateClass = c;
                break;
            }
                /*Object privateInstance = c.getDeclaredConstructor(inner.getClass()).newInstance(inner);
                java.lang.reflect.Method method = c.getDeclaredMethod("powerof2", int.class);
                method.setAccessible(true);
                String result = (String) method.invoke(privateInstance, num);
                System.out.println(result);
                System.out.println("An instance of class: " + privateInstance.getClass().getCanonicalName() + " has been created");
            */
            Constructor<?> ctor = privateClass.getDeclaredConstructor(Inner.class);
            ctor.setAccessible(true);
            Object o = ctor.newInstance(inner);

            Method m= privateClass.getDeclaredMethod("powerof2", int.class);
            m.setAccessible(true);
            String result = (String) m.invoke(o, num);
            System.out.println(num + " is " +result);
            System.out.println("An instance of class: " + o.getClass().getCanonicalName() + " has been created");

        }
    } catch (ExitTrappedException e) {
        System.out.println("Unsuccessful Termination!!");
    } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
    } catch (InstantiationException e) {
        throw new RuntimeException(e);
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    static class Inner {
        private class Private {
            private String powerof2(int num) {
                return ((num & (num - 1)) == 0) ? num + " is power of 2" : num + " is not power of 2";
            }
        }
    }
}


class DoNotTerminate { //This class prevents exit(0)
    public static class ExitTrappedException extends SecurityException {
        private static final long serialVersionUID = 1L;
    }

    public static void forbidExit() {
        final SecurityManager securityManager = new SecurityManager() {
            @Override
            public void checkPermission(java.security.Permission permission) {
                if (permission.getName().contains("exitVM")) {
                    throw new ExitTrappedException();
                }
            }
        };
        System.setSecurityManager(securityManager);
    }
}
/* You are given a class Solution and an inner class Inner.Private. The main method of class Solution takes an integer  as input.
 The powerof2 in class Inner.Private checks whether a number is a power of . You have to call the method powerof2 of the class Inner.
 Private from the main method of the class Solution.

Constraints 1<=num<=2pow10

Sample Input

8
Sample Output

8 is power of 2
An instance of class: Solution.Inner.Private has been created  */
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