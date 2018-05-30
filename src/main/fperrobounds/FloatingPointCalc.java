package edu.baj.fperrorbounds;
import java.util.Random;
import java.math.BigDecimal;
import java.util.stream.DoubleStream;
import org.apache.commons.math3.distribution.UniformRealDistribution;

public class FloatingPointCalc  { 

    public static void main(String[] args) {
        System.out.println(probCalc());
    }

    public static float probCalc() {
        float a = (float) sampleDouble(-1000, 1000);
        BigDecimal bd = BigDecimal.valueOf((double) a);

        a = a + 1000;
        bd = bd.add(BigDecimal.valueOf(1000));
        for (int i = 0; i < 1000; i++) {
            a = a + ((float) .00001);
            bd = bd.add(BigDecimal.valueOf(.00001));
        }

        // FPError: 
        System.out.println(bd);
        System.out.println(a);
        bd = bd.negate();
        bd = bd.add(BigDecimal.valueOf((double) a));
        System.out.println("Error: " + bd);
        return a;
    }

    public static double sampleDouble(double min, double max) {
        UniformRealDistribution urd =  new UniformRealDistribution(min, max);
        return urd.sample();
    }
}