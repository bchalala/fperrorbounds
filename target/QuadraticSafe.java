public class Test {
    @ConfidenceAnnotation(
            epsilon=0.03,
            confidence = 95.0,
            precision = 0.02,
            min = 1.9,
            max = 2.9
    )
    public static float probCalc(float b) {
        float a = 1.0f;
        float c = -0.0015f;
        System.out.println(b);
        System.out.println((-b  - (float)Math.sqrt(b * b - 4f * a * c))/ (2f * a));
        return (-b  - (float)Math.sqrt(b * b - 4f * a * c))/ (2f * a);
    }
}