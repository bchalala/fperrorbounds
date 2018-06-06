public class Test {
    @ConfidenceAnnotation(
            epsilon=0.03,
            confidence = 95.0,
            precision = 0.000001,
            min = {199.0},
            max = {201.0}
    )
    public static float probCalc(float b) {
        float a = 1.0f;
        float c = -0.0015f;
        float x1 = (-b  + (float)Math.sqrt(b * b - 4f * a * c))/ (2f * a);
        System.out.println(x1);
        return x1;
    }
}