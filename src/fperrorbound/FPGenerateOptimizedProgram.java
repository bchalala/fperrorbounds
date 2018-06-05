package fperrorbound;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FPGenerateOptimizedProgram {

    public static List<FPTestProgram> getValidPrograms(List<FPTestProgram> synthesizedPrograms,FPErrorAnnotation annotation) throws Exception{
        List<FPTestProgram> validPrograms = new ArrayList<>();
        for(FPTestProgram program: synthesizedPrograms){
            FPInMemoryCompiler imc = new FPInMemoryCompiler();
            imc.compileInMemory(program.harnessClass, program.program);
            imc.loadCompiledClass();
            Method method = imc.getMethod("test");
            int numberOfSamples = FPSamples.generateSampleNumber(annotation.epsilon,annotation.confidence);
            int numberOfPassSamples = (int) ((annotation.confidence*numberOfSamples)/100);
            int currentPassCount = 0;
            System.out.println("Samples required: " + numberOfPassSamples);
            long startTime = System.nanoTime();
            for(int i=0;i<numberOfSamples;i++) {
                double res = (double) method.invoke(null);
                System.out.println("Error is: " + res);
                if(Math.abs(res) <= annotation.precision){
                    currentPassCount++;
                }
            }
            long estimatedTime = System.nanoTime() - startTime;
            if(currentPassCount >= numberOfPassSamples){
                program.estimatedRunTime = estimatedTime;
                validPrograms.add(program);
                System.out.println(String.format("Passed with %d / %d", currentPassCount, numberOfSamples));
            }
        }
        return validPrograms;
    }


    public static FPTestProgram getOptimizedProgram(List<FPTestProgram> programs){
        Collections.sort(programs,FPTestProgram.ProgramComparator);
        return programs.get(0);
    }
}
