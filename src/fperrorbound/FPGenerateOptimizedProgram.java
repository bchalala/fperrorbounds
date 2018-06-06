package fperrorbound;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FPGenerateOptimizedProgram {

    public static List<FPTestProgram> getValidPrograms(List<FPTestProgram> synthesizedPrograms,FPErrorAnnotation annotation) throws Exception{
        List<FPTestProgram> validPrograms = new ArrayList<>();
        for(FPTestProgram program: synthesizedPrograms){
                program.estimatedRunTime = getEstimatedRunTime(program,annotation,"fnFloat");
                validPrograms.add(program);
        }
        return validPrograms;
    }

    public static FPTestProgram getOptimizedProgram(List<FPTestProgram> programs){
        Collections.sort(programs,FPTestProgram.ProgramComparator);
        return programs.get(0);
    }

    public static long getEstimatedRunTime(FPTestProgram program, FPErrorAnnotation annotation, String methodName) throws Exception {
        Method fnFloat = FPErrorBound.returnCompiledMethod(methodName, program);
        long estimatedTime = 0;
        int numberOfSamples = FPSamples.generateSampleNumber(annotation.epsilon, annotation.confidence);
        int numberOfPassSamples = (int) ((annotation.confidence * numberOfSamples) / 100);

        if (FPErrorBound.verifyProgram(program, annotation)) {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < numberOfPassSamples; i++) {
                fnFloat.invoke(null);
            }
            estimatedTime = System.currentTimeMillis() - startTime;

        }
        return estimatedTime;
    }
}
