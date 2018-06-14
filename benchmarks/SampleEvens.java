
public class SampleEvens {
    @ConfidenceAnnotation(
            epsilon = 0.01,
            confidence = 95.0,
            precision = 0.005,
            sample = "sampleUniformEvens"
    )
    public static float probCalc(float a) {
        for (int i = 0; i < 100; i++) {
            a = a / 60.0f;
            a = a * 59.0f;
        }
        return a;
    }

    public static double sampleUniformEvens() {
        UniformRealDistribution urd = new UniformRealDistribution(0, 100);
        double s = urd.sample();
        int sint = (int) s;
        if (sint % 2 == 0) { return s; }
        else { return sampleUniformEvens(); }
    }
}