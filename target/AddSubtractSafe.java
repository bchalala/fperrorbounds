public class AddSubtractSafe {
    @ConfidenceAnnotation(
            epsilon=0.03,
            confidence = 95.0,
            precision = 0.000001,
            sample = "Uniform",
            min = {0.5},
            max = {0.7}
    )
    public static float probCalc(float a) {
        float f1 = a;
        float f = 0.0f;
        for(int i = 0; i < 100; i++)
        {
            f = f + f1;
            f = f - f1;
        }
        return f;
    }
}