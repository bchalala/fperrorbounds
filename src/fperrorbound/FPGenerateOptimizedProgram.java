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
        Method sample = FPErrorBound.returnCompiledMethod(annotation.sampleMethod,program);
        long estimatedTime = 0;
        int numberOfSamples = FPSamples.generateSampleNumber(annotation.epsilon, annotation.confidence);
        int numberOfPassSamples = (int) ((annotation.confidence * numberOfSamples) / 100);

        for (int i = 0; i < numberOfPassSamples; i++) {
            double[] functionArgs = new double[fnFloat.getParameterCount()];
            for(int j=0;j<fnFloat.getParameterCount();j++){
                //var a = sample.getParameterTypes();
                //Arrays.stream(a).forEach(x -> System.out.println(x.getName()));
                if(sample.getParameterCount()>0){
                    functionArgs[j] = (double) sample.invoke(null,annotation.min.get(j),annotation.max.get(j));
                } else{
                    functionArgs[j] = (double) sample.invoke(null);
                }

            }
            long startTime = System.nanoTime();
            if(methodName.equals("fnFloat")) {
                if (fnFloat.getParameterCount() < 2 ) {
                    fnFloat.invoke(null, (float)functionArgs[0]);
                } else {
                    FPErrorBound.log(fnFloat.invoke(null, (float)functionArgs[0], (float)functionArgs[1]));
                }
            } else{
                if (fnFloat.getParameterCount() < 2) {
                    fnFloat.invoke(null, functionArgs[0]);
                } else {
                    fnFloat.invoke(null, functionArgs[0], functionArgs[1]);
                }
            }
            estimatedTime = estimatedTime + (System.nanoTime() - startTime);
        }



        return estimatedTime;
    }
}
