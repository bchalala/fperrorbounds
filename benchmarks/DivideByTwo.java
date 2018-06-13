public class DivideByTwo {
    @ConfidenceAnnotation(
            epsilon=0.03,
            confidence = 95.0,
            precision = 0.001,
            distribution = "uniform",
            min = {22.0},
            max = {100000.0}
    )
    public static float probCalc(float param)
    {
        float b = (float) 0x1.fffffffffffffp-1;
        float x = 1 / b;
        return x*param;
    }
}

