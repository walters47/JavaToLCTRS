package test_classes;

public class DoubleCompareDoublesTest {

    public double compareDoubles (double x, double y) {
        if (x > y) {
            return x;
        } else if (y > x) {
            return y;
        } else {
            return y + x;
        }
    }
}
