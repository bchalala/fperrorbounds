package fperrorbound;


import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

public class FPSamplingVerification {

    public static boolean acceptHypothesis(double[] highPrecision, double[] lowPrecision){
        double pValue = -1;
        KolmogorovSmirnovTest kolmogorovSmirnovTest = new KolmogorovSmirnovTest();
        pValue = kolmogorovSmirnovTest.kolmogorovSmirnovStatistic(highPrecision,lowPrecision);

        if(pValue < 0.01){
            return false;
        } else {
            return true;
        }
    }

    public static double[] testKS(){
        double[] samples = new double[1000];
        UniformRealDistribution urd =  new UniformRealDistribution(-1000, 1000);
        for(int i=0;i<1000;i++){
            samples[i] = urd.sample();
        }

        return samples;
    }

    public int checkIfNormal(double[] error){
        KolmogorovSmirnovTest kolmogorovSmirnovTest = new KolmogorovSmirnovTest();


        return 0;
    }
}
