import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface ConfidenceAnnotation {
    double epsilon = 0.03;
    double confidence = .95;
    double precision = 0.01;
    double[] min = {-1000.0};
    double[] max = {1000.0};
}

