package fperrorbound;
import java.lang.reflect.Method;

public class FPErrorBound {
    public static void main(String[] args) {

        boolean codeGenerationMode = false;
        try {
            System.out.println("Input file: " + args[0]);
            FPErrorAnnotation annotation = new FPErrorAnnotation(args[0]);
            System.out.println("Annotations successfully parsed.");
            FPJavaCodeGenerator gen = new FPJavaCodeGenerator(args[0], annotation);
            System.out.println("Code generation is initialized.");

            if (codeGenerationMode) {
                try {
                    var programList = gen.generateAllProgramPermutations();
                } catch (Exception e) { System.out.println("Code generation failed."); }
            } 
            else {
                try {
                    var testHarness = gen.genStandardHarness();
                    System.out.println("Test harness successfully generated");
                    System.out.println(testHarness.program);
                    if (verifyProgram(testHarness, annotation)) {
                        System.out.println("Q.E.D.");
                    } else {
                        System.out.println("Unable to verify with current annotation.");
                    }
                } catch (Exception e) { System.out.println("Error generating and running test harness."); }
            }

        } catch (Exception e) {
            System.err.println("Error initializing code generation.");
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