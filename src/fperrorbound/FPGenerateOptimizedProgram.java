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
        Method sample = FPErrorBound.returnCompiledMethod("sample",program);
        long estimatedTime = 0;
        int numberOfSamples = FPSamples.generateSampleNumber(annotation.epsilon, annotation.confidence);
        int numberOfPassSamples = (int) ((annotation.confidence * numberOfSamples) / 100);

        if (FPErrorBound.verifyProgram(program, annotation)) {
            for (int i = 0; i < numberOfPassSamples; i++) {
                Double[] functionArgs = new Double[annotation.min.size()];
                for(int j=0;j<annotation.min.size();j++){
                    //var a = sample.getParameterTypes();
                    //Arrays.stream(a).forEach(x -> System.out.println(x.getName()));
                    functionArgs[j] = (Double) sample.invoke(null,annotation.min.get(j),annotation.max.get(j));
                }
                long startTime = System.nanoTime();
                fnFloat.invoke(null);
                estimatedTime = estimatedTime+ (System.nanoTime() - startTime);

            }


        }
        return estimatedTime;
    }
}
