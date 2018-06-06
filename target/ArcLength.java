public class ArcLength {
    @ConfidenceAnnotation(
            epsilon=0.03,
            confidence = 95.0,
            precision = 0.1,
            sample = "Uniform",
            min = {0.0009,140.89},
            max = {2.5,360.0}
    )
    public static float probCalc(float diameter, float angle)
    {
        float pi = (22.0f / 7.0f);
        float arc;

        if (angle >= 360f) {
            System.out.println("Angle cannot"
                    + " be formed");
            return 0f;
        }
        else {
            arc = ((pi * diameter) * (angle / 360.0f));
            return arc;
        }
    }
}
