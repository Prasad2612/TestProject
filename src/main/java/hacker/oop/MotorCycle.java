package hacker.oop;

public class MotorCycle extends Cycle{

    @Override
    String define_me() {
        return "Hello I am a motorcycle, I am a cycle with an engine.\n" +
                "My ancestor is a cycle who is "+super.define_me();
    }
}
