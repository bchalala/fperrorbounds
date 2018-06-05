package fperrorbound;
import java.lang.reflect.Method;

public class FPErrorBound {
    public static void main(String[] args) {
        try {
            System.out.println("Input file: " + args[0]);
            
            FPErrorAnnotation annotation = new FPErrorAnnotation(args[0]);
            System.out.println("Annotations successfully parsed.");

            FPJavaCodeGenerator gen = new FPJavaCodeGenerator(args[0], annotation);
            var testHarness = gen.genStandardHarness();
            
            // Compile the test harness in memory and get the test method
            FPInMemoryCompiler imc = new FPInMemoryCompiler();
            imc.compileInMemory(testHarness.harnessClass, testHarness.program);
            imc.loadCompiledClass();
            Method method = imc.getMethod("test");
            
            int numberOfSamples = FPSamples.generateSampleNumber(annotation.epsilon,annotation.confidence);
            int numberOfPassSamples = (int) ((annotation.confidence*numberOfSamples)/100);
            int currentPassCount = 0;

            System.out.println("Samples required: " + numberOfPassSamples);

            for(int i=0;i<numberOfSamples;i++) {
                double res = (double) method.invoke(null);
                System.out.println("Error is: " + res);
                if(Math.abs(res) <= annotation.precision){
                    currentPassCount++;
                }
            }
            if(currentPassCount >= numberOfPassSamples){
                System.out.println(String.format("Passed with %d / %d", currentPassCount, numberOfSamples));
            }
            else
            {
                System.out.println(String.format("Failed with %d / %d, needed %d", currentPassCount, numberOfSamples, numberOfPassSamples));
            }

        } catch (Exception e) {
            System.err.println("Error invoking the test harness");
        }
    }
}