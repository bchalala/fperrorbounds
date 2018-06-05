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
<<<<<<< HEAD
        double difference = Math.abs(doubleRes - floatRes);
        return difference;
=======
        double error = Math.abs((doubleRes - floatRes));
        return error;
>>>>>>> dbace78b0646d3610bdda15940bffdc6520a6140
    }
}