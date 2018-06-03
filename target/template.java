import org.apache.commons.math3.distribution.UniformRealDistribution;

public class TestHarness {
    public static double sample(double min, double max) {
        UniformRealDistribution urd =  new UniformRealDistribution(min, max);
        return urd.sample();
    }

    public static double test() {
        return 0.0;
    }
}