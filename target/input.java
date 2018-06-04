
public class Test {
    @ConfidenceAnnotation(epsilon=0.03,
            confidence = 95.0)
    public static float probCalc(float a) {
        a = a + 1000;
        for (int i = 0; i < 1000; i++) {
            a = a + ((float) .00001);
        }

        return a;
    }
}