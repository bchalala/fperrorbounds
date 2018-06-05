package fperrorbound;
import java.lang.reflect.Method;
import java.util.Random;
import java.math.BigDecimal;
import java.util.stream.DoubleStream;
import org.apache.commons.math3.distribution.UniformRealDistribution;

public class FPErrorBound { 

    public static void main(String[] args) {
        String program = "";
        try {
            program = FPJavaCodeGenerator.generateHarness(args[0]);
            System.out.println(program);
            FPInMemoryCompiler imc = new FPInMemoryCompiler();
            imc.compileInMemory("TestHarness", program);
            imc.loadCompiledClass();
            Method method = imc.getMethod("test");
            FPErrorAnnotation annotation = new FPErrorAnnotation("target/Quadratic.java");
            int numberOfSamples = FPSamples.generateSampleNumber(annotation.epsilon,annotation.confidence);
            for(int i=0;i<numberOfSamples;i++) {
                double res = (double) method.invoke(null);
                System.out.println("Error is " + res);
            }

        } catch (Exception e) {
            System.err.println("Error invoking the test harness");
        }
    }
}