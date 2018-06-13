package fperrorbound;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Arrays;

public class FPErrorBound {
    public static boolean debug = false;

    public static void main(String[] args) {
        boolean genPrecision = false;
        for (String s : args) {
            if(s.equals("genPrecision")) {
                genPrecision = true;
            }
            if(s.equals("debug")) {
                debug = true;
            }
        }

        try {
            System.out.println("Input file: " + args[0]);
            FPErrorAnnotation annotation = new FPErrorAnnotation(args[0]);
            System.out.println("Annotations successfully parsed.");
            FPJavaCodeGenerator gen = new FPJavaCodeGenerator(args[0], annotation);
            System.out.println("Code generation is initialized.");

            try {
                var testHarness = gen.genStandardHarness();
                System.out.println("Test harness successfully generated");
                log(testHarness.program);

                if (genPrecision) {
                    double precision = getPrecision(testHarness, annotation);
                    System.out.println("Program verified for precision: " + Double.toString(precision));
                } else {
                    if (verifyProgram(testHarness, annotation)) {
                        System.out.println("Q.E.D.");
                    } else {
                        System.out.println("Unable to verify with current annotation.");
                    }
                }
            } catch (VerificationException e){ System.err.println(e.getMessage()); }
        } catch (Exception e) {
            System.err.println("Error initializing code generation.");
        }
    }

    public static Method returnCompiledMethod(String methodName, FPTestProgram testProgram) throws Exception{
        FPInMemoryCompiler imc = new FPInMemoryCompiler();
        imc.compileInMemory(testProgram.harnessClass, testProgram.program);
        imc.loadCompiledClass();
        Method method = imc.getMethod(methodName);
        return method;
    }

    public static String getOptimisedProgram(List<FPTestProgram> candidatePrograms, FPErrorAnnotation annotation) throws Exception {
        List<FPTestProgram> verifiedPrograms = FPGenerateOptimizedProgram.getValidPrograms(candidatePrograms,annotation);
        return FPGenerateOptimizedProgram.getOptimizedProgram(verifiedPrograms).program;
    }

    public static double getPrecision(FPTestProgram testHarness, FPErrorAnnotation annotation) throws Exception {
        Method method = returnCompiledMethod("test",testHarness);

        int numberOfSamples = FPSamples.generateSampleNumber(annotation.epsilon,annotation.confidence);
        int numberOfPassSamples = (int) ((annotation.confidence*numberOfSamples)/100);
        int currentPassCount = 0;

        double[] sampleArray = new double[numberOfSamples];

        for(int i = 0; i < numberOfSamples; i++) {
            double res = (double) method.invoke(null);
            sampleArray[i] = res;
        }

        Arrays.sort(sampleArray);
        var verifiedPrecision = sampleArray[numberOfPassSamples + 1];
        return verifiedPrecision;
    }

    public static boolean verifyProgram(FPTestProgram testHarness, FPErrorAnnotation annotation) throws Exception {
        Method method = returnCompiledMethod("test",testHarness);

        int numberOfSamples = FPSamples.generateSampleNumber(annotation.epsilon,annotation.confidence);
        int numberOfPassSamples = (int) ((annotation.confidence*numberOfSamples)/100);
        int currentPassCount = 0;

        System.out.println("Harness: " + testHarness.harnessClass);
        System.out.println("Samples required to verify precision: " + numberOfPassSamples);
        System.out.println("Attempting to verify for precision: " + annotation.precision);

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
    }

    public static void log(Object s) {
        if (debug) {
            System.out.println(s);
        }
    }
}