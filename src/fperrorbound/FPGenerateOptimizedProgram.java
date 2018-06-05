package fperrorbound;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FPGenerateOptimizedProgram {

    public static List<FPTestProgram> getValidPrograms(List<FPTestProgram> synthesizedPrograms,FPErrorAnnotation annotation) throws Exception{
        List<FPTestProgram> validPrograms = new ArrayList<>();
        for(FPTestProgram program: synthesizedPrograms){
            Method fnFloat = FPErrorBound.returnCompiledMethod("fnFloat",program);

            int numberOfSamples = FPSamples.generateSampleNumber(annotation.epsilon,annotation.confidence);
            int numberOfPassSamples = (int) ((annotation.confidence*numberOfSamples)/100);

            if(FPErrorBound.verifyProgram(program,annotation)){
                long startTime = System.nanoTime();
                for(int i = 0; i < numberOfPassSamples; i++){
                    fnFloat.invoke(null);
                }
                long estimatedTime = System.nanoTime() - startTime;
                program.estimatedRunTime = estimatedTime;
                validPrograms.add(program);
            }
        }
        return validPrograms;
    }

    public static FPTestProgram getOptimizedProgram(List<FPTestProgram> programs){
        Collections.sort(programs,FPTestProgram.ProgramComparator);
        return programs.get(0);
    }
}
