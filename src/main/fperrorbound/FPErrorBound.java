package edu.baj.fperrorbound;
import java.util.Random;
import java.math.BigDecimal;
import java.util.stream.DoubleStream;
import org.apache.commons.math3.distribution.UniformRealDistribution;

public class FPErrorBound { 

    public static void main(String[] args) {
        String program = "";
        try {
            program = FPJavaCodeGenerator.generateHarness("target/input.java");
            System.out.println(program);
        } catch (Exception e) {
            System.err.println("Error: Could not generate test harness.");
        }

        FPInMemoryCompiler imc = new FPInMemoryCompiler();
        try {
            imc.compileInMemory("TestHarness", program);
            imc.loadCompiledClass();
        } catch (Exception e) {
            System.err.println("Error compiling the TestHarness");
        }

        try {
            double res = (double) imc.getMethod("test").invoke(null);
            System.out.println("Error is " + res);
        } catch (Exception e) {
            System.err.println("Error invoking the test harness");
        }
    }
}