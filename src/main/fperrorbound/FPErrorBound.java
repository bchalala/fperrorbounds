package edu.baj.fperrorbound;
import java.util.Random;
import java.math.BigDecimal;
import java.util.stream.DoubleStream;
import org.apache.commons.math3.distribution.UniformRealDistribution;

public class FPErrorBound { 

    public static void main(String[] args) {
        try {
            System.out.print(FPJavaCodeGenerator.generateHarness("target/input.java"));
        } catch (Exception e) {
            System.err.println("Error: Could not generate test harness.");
        }
    }
}