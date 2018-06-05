package fperrorbound;
import java.lang.reflect.Method;

public class FPErrorBound {
    public static void main(String[] args) {
        try {
            System.out.println("Input file: " + args[0]);
            
            FPErrorAnnotation annotation = new FPErrorAnnotation(args[0]);
            System.out.println("Annotations successfully parsed.");

            FPJavaCodeGenerator gen = new FPJavaCodeGenerator(args[0], annotation);
            System.out.println("FPJavaCodeGenerator initialized.");
            var testHarness = gen.genStandardHarness();
            System.out.println("Test harness successfully generated");

            if (verifyProgram(testHarness, annotation)) {
                System.out.println("Q.E.D.");
            } else {
                System.out.println("Unable to verify.");
            }
            
        } catch (Exception e) {
            System.err.println("Error generating test harness");
        }
    }

    public static boolean verifyProgram(FPTestProgram testHarness, FPErrorAnnotation annotation) {
        try {
            FPInMemoryCompiler imc = new FPInMemoryCompiler();
            imc.compileInMemory(testHarness.harnessClass, testHarness.program);
            imc.loadCompiledClass();
            Method method = imc.getMethod("test");
            
            int numberOfSamples = FPSamples.generateSampleNumber(annotation.epsilon,annotation.confidence);
            int numberOfPassSamples = (int) ((annotation.confidence*numberOfSamples)/100);
            int currentPassCount = 0;

            System.out.println("Harness: " + testHarness.harnessClass);
            System.out.println("Samples required: " + numberOfPassSamples);

            for(int i = 0; i < numberOfSamples; i++) {
                double res = (double) method.invoke(null);
                System.out.println("Error is: " + res);
                if(Math.abs(res) <= annotation.precision){
                    currentPassCount++;
                }
            }

            if(currentPassCount >= numberOfPassSamples){
                System.out.println(String.format("Passed with %d / %d", currentPassCount, numberOfSamples));
                return true;
            }
            else
            {
                System.out.println(String.format("Failed with %d / %d, needed %d", currentPassCount, numberOfSamples, numberOfPassSamples));
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error verifying test harness");
            return false;
        }
    }
}