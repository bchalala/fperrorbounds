
public class Test {
    @ConfidenceAnnotation(
            epsilon=0.01,
            confidence = 95.0,
            precision = 0.005,
            min = .2,
            max = 100000000000000000.0
    )
    public static float probCalc(float a) {
        for (int i = 0; i < 1000; i++) {
            a = a / 60;
            a = a * 59;
        }
        return a;
    }
}