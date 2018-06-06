public class Test {
    @ConfidenceAnnotation(
            epsilon=0.03,
            confidence = 95.0,
            precision = 0.000001,
            min = {0.8},
            max = {1.2}
    )
    public static float probCalc(float a) {
        float y;
        float z;
        y = a - 1.0f;
        z = (float)Math.exp(y);
        if(z != 1.0f)
            z = y / (z - 1.0f);
        return z;
    }
}