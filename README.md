# Building and Running FPErrorBound
## Build Instructions
1. First install `ant`
2. `ant install-ivy` to get the ivy dependency manager
3. `ant resolve` to resolve all dependencies
4. `ant` to build

## Running FPErrorBound
To run the basic verification, use the following command `ant run -Dinput=[filename]`

Set the flag `-DgenPrecision=genPrecision` to use the precision generation features which geturns the highest verifiable precision for the given confidence and epsilon in the input program.

Additionally, use the `-Ddebug=debug` flag in order to turn on debug print messages.

## Example Invocation
`ant run -Dinput=benchmarks/ArcLength.java -DgenPrecision=genPrecision`

# Writing input programs

Epsilon and confidence determine the number of samples (by Hoeffding's Inequality) required to verify input programs for a given confidence. The sample field is for specifying what distribution inputs must be drawn from. We currently allow for "uniform", "gaussian", or user defined distributions (which are defined in probabilistic programs). This annotation must always be present, and must at least specify the epsilon, confidence, precision, and sample fields. 

Additionally, the input function is required to declared as `public static` and must have the name `probCalc`. Additionally, arguments must be either of type `float` or of type `double`. The return type must also be `float` or `double`. The class name of the defined function does not matter.

The following annotation specifies that the two arguments for the function that we are verifying are drawn from uniform distributions, the arg1 being drawn from a uniform distribution from 0.0009 to 2.5, and arg2 being drawn from a uniform distribution from 140.89 to 360.0. 

```java
    @ConfidenceAnnotation(
            epsilon=0.03,
            confidence = 95.0,
            precision = 0.1,
            sample = "uniform",
            min = {0.0009,140.89},
            max = {2.5,360.0}
    )
    public static float probCalc(float arg1, float arg2) { ... }
```

The following annotation specifies that the two arguments for the function that we are verifying are drawn from gaussian distributions, the arg1 being drawn from a gaussian with mean 0.0 and standard deviation 1.0, and arg2 being drawn from a gaussian with mean 50.0 and standard deviation 15.0. 

```java
    @ConfidenceAnnotation(
            epsilon=0.05,
            confidence = 98.0,
            precision = 0.01,
            sample = "gaussian",
            mean = {0.0,50.0},
            sd = {1.0,15.0}
    )
    public static float probCalc(float arg1, float arg2) { ... }
```

We also allow for input distributions to be specified by users, and all arguments will be drawn from the input distribution. The sample parameter must contain the string name of the distribution that is being sampled from. The following is an example of a user defined input distribution.

``` java
public class DrawingFromUserDistribution {
    @ConfidenceAnnotation(
            epsilon = 0.01,
            confidence = 95.0,
            precision = 0.005,
            sample = "sampleUniformEvens"
    )
    public static float probCalc(float a) { ... }

    public static double sampleUniformEvens() {
        UniformRealDistribution urd = new UniformRealDistribution(0, 100);
        double s = urd.sample();
        int sint = (int) s;
        if (sint % 2 == 0) { return s; }
        else { return sampleUniformEvens(); }
    }
}
```