package fperrorbound;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.util.HashMap;

public class FPSamples {

    public static int generateSampleNumber(double epsilon, double confidence) throws Exception{
        return hoeffdingSamples(epsilon, confidence);
    }

    public static void printSampleNumbers(HashMap<String,Integer> methodSampleMap){
        for(String method: methodSampleMap.keySet()){
            System.out.println("Method Name: "+method+" , #Samples: "+methodSampleMap.get(method));
        }
    }

    public static int hoeffdingSamples(double epsilon, double confidence){
        int numberOfSamples = 0;
        double alpha = 1 - confidence/100;
        numberOfSamples = (int) (-1*(1/(2*epsilon*epsilon))*Math.log(alpha/2));
        return numberOfSamples;
    }
}

