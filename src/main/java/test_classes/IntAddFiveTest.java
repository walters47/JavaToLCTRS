package test_classes;

public class IntAddFiveTest {

    public static int addFive(int x) {
        return x + 5;
    }

    public static int addFiveIfOverTen (int x) {
        if (x > 10) {
            return addFive(x);
        } else {
            return x;
        }
    }

}