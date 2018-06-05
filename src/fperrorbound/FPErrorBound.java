package fperrorbound;
import java.lang.reflect.Method;

public class FPErrorBound {
    public static void main(String[] args) {
        try {
            System.out.println(args[0]);
            // Generate the test harness program
            FPErrorAnnotation annotation = new FPErrorAnnotation(args[0]);
            FPJavaCodeGenerator gen = new FPJavaCodeGenerator(args[0], annotation);
            var testHarness = gen.genStandardHarness();
            System.out.println(testHarness);
            
            // Compile the test harness in memory and get the test method
            FPInMemoryCompiler imc = new FPInMemoryCompiler();
            imc.compileInMemory("TestHarness", testHarness);
            imc.loadCompiledClass();
            Method method = imc.getMethod("test");
            
            int numberOfSamples = FPSamples.generateSampleNumber(annotation.epsilon,annotation.confidence);
            int numberOfPassSamples = (int) ((annotation.confidence*numberOfSamples)/100);
            int currentPassCount = 0;
            for(int i=0;i<numberOfSamples;i++) {
                double res = (double) method.invoke(null);
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