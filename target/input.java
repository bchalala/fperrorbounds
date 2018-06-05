
public class Test {
    @ConfidenceAnnotation(
            epsilon=0.01,
            confidence = 95.0,
            precision = 0.005,
            min = .2,
            max = 1000000.0
    )
    public static float probCalc(float a) {
        for (int i = 0; i < 100; i++) {
            a = a / 60.0f;
            a = a * 59.0f;
        }
        return a;
    }
}