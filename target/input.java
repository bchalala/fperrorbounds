public class Test {
    public static float probCalc(float a) {
        a = a + 1000;
        for (int i = 0; i < 1000; i++) {
            a = a + ((float) .00001);
        }

        return a;
    }
}