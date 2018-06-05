public class Test {
    @ConfidenceAnnotation(
            epsilon=0.03,
            confidence = 95.0,
            precision = 0.02,
            min = 1.9,
            max = 2.9
    )
    public static float probCalc(float a) {
        float f = 12.34f;
        return f * a + f * f * a;
    }
}