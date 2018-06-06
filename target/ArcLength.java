public class ArcLength {
    @ConfidenceAnnotation(
            epsilon=0.03,
            confidence = 95.0,
            precision = 0.1,
            min = {0.0009,140.89},
            max = {2.5,360.0}
    )
    public static double probCalc(float diameter,
                            float angle)
    {
        float pi = (float) (22.0 / 7.0);
        float arc;

        if (angle >= 360) {
            System.out.println("Angle cannot"
                    + " be formed");
            return 0;
        }
        else {
            arc = (float) ((pi * diameter) * (angle / 360.0));
            return arc;
        }
    }
}
