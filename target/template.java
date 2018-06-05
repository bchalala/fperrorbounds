import org.apache.commons.math3.distribution.UniformRealDistribution;
import java.lang.Math;

public class TestHarness {
    public static double sample(double min, double max) {
        UniformRealDistribution urd =  new UniformRealDistribution(min, max);
        return urd.sample();
    }

    public static double test() {
        double floatRes = (double) fnFloat();
        double doubleRes = fnDouble();
        double error = Math.abs((doubleRes - floatRes));
        return error;
    }
}