package fperrorbound;

import java.util.Comparator;

public class FPTestProgram {
    public String harnessClass;
    public String program;
    public long estimatedRunTime;

    public FPTestProgram(String p, String hc) {
        program = p;
        harnessClass = hc;
    }

    public static Comparator<FPTestProgram> ProgramComparator = new Comparator<FPTestProgram>() {
        @Override
        public int compare(FPTestProgram o1, FPTestProgram o2) {
            return Long.compare(o1.estimatedRunTime,o2.estimatedRunTime);
        }
    };

}