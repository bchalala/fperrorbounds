package fperrorbound;
import java.lang.reflect.Method;
import java.util.List;

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
            }
            
        } catch (VerificationException e){
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error generating test harness");
        }
    }

    public static Method returnCompiledMethod(String methodName, FPTestProgram testProgram) throws Exception{
        FPInMemoryCompiler imc = new FPInMemoryCompiler();
        imc.compileInMemory(testProgram.harnessClass, testProgram.program);
        imc.loadCompiledClass();
        Method method = imc.getMethod("test");
        return method;
    }

    public static String getOptimisedProgram(List<FPTestProgram> candidatePrograms, FPErrorAnnotation annotation) throws Exception{
        List<FPTestProgram> verifiedPrograms = FPGenerateOptimizedProgram.getValidPrograms(candidatePrograms,annotation);
        return FPGenerateOptimizedProgram.getOptimizedProgram(verifiedPrograms).program;
    }

    public static boolean verifyProgram(FPTestProgram testHarness, FPErrorAnnotation annotation) throws Exception{
        Method method = returnCompiledMethod("test",testHarness);

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
            throw new VerificationException("Unable to verify the program for given arguments.");
        }
    }
}