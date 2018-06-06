import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import java.lang.Math;

public class TestHarness {
    public static double sampleUniform(double min, double max) {
        UniformRealDistribution urd = new UniformRealDistribution(min, max);
        return urd.sample();
    }

    public static double sampleGaussian(double mean, double sd) {
        NormalDistribution nd = new NormalDistribution(mean, sd);
        return nd.sample();
    }

    public static double test() {
        double floatRes = fnFloat();
        double doubleRes = fnDouble();
        double difference = Math.abs(doubleRes - floatRes);
        return difference;
    }
}