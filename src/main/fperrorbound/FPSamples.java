package edu.baj.fperrorbound;

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

    public static HashMap<String, Integer> generateSampleNumber(String file) throws Exception{
        HashMap<String, Integer> methodSampleMap = new HashMap<>();
        var in = new FileInputStream(file);
        CompilationUnit cu = JavaParser.parse(in);
        MethodAnnotationVisitor methodAnnotationVisitor = new MethodAnnotationVisitor();
        methodAnnotationVisitor.visit(cu,null);

        for(String key: methodAnnotationVisitor.methodAnnotations.keySet()){
            double epsilon = methodAnnotationVisitor.methodAnnotations.get(key).get(0).getValue().asDoubleLiteralExpr().asDouble();
            double confidence = methodAnnotationVisitor.methodAnnotations.get(key).get(1).getValue().asDoubleLiteralExpr().asDouble();
            methodSampleMap.put(key,hoeffdingSamples(epsilon,confidence));
        }
        return methodSampleMap;
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

    private static class MethodAnnotationVisitor extends VoidVisitorAdapter<Void> {
        protected HashMap<String, NodeList<MemberValuePair>> methodAnnotations = new HashMap<>();
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if(n.getAnnotations() != null){
                for(AnnotationExpr annotationExpr : n.getAnnotations()){
                    System.out.println(annotationExpr.getClass());
                    NormalAnnotationExpr expr = (NormalAnnotationExpr) annotationExpr;
                    methodAnnotations.put(n.getName().toString(),expr.getPairs());
                }
            }
        }
    }
}

