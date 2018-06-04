import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface ConfidenceAnnotation {
    double epsilon = 0.03;
    double confidence = .95;
}

