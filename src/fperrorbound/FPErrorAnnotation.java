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
import java.util.ArrayList;
import java.util.HashMap;

public class FPErrorAnnotation {
    public double epsilon = 0.03;
    public double confidence = .95;
    public double precision = 0.01;
    public String sampleMethod = "";
    public ArrayList<Double> min = new ArrayList<>();
    public ArrayList<Double> max = new ArrayList<>();

    FPErrorAnnotation(String file) throws Exception{
        var in = new FileInputStream(file);
        CompilationUnit cu = JavaParser.parse(in);
        MethodAnnotationVisitor methodAnnotationVisitor = new MethodAnnotationVisitor();
        methodAnnotationVisitor.visit(cu,null);
        epsilon = methodAnnotationVisitor.methodAnnotations.get(methodAnnotationVisitor.method).get(0).getValue().asDoubleLiteralExpr().asDouble();
        confidence = methodAnnotationVisitor.methodAnnotations.get(methodAnnotationVisitor.method).get(1).getValue().asDoubleLiteralExpr().asDouble();
        precision = methodAnnotationVisitor.methodAnnotations.get(methodAnnotationVisitor.method).get(2).getValue().asDoubleLiteralExpr().asDouble();
        sampleMethod = methodAnnotationVisitor.methodAnnotations.get(methodAnnotationVisitor.method).get(3).getValue().asStringLiteralExpr().asString();
        System.out.println(sampleMethod);
        if (sampleMethod.equals("uniform") || sampleMethod.equals("gaussian"))
        {
            int size = methodAnnotationVisitor.methodAnnotations.get(methodAnnotationVisitor.method).get(4).getValue().asArrayInitializerExpr().getValues().size();
            for(int j=0;j<size;j++){
                min.add(methodAnnotationVisitor.methodAnnotations.get(methodAnnotationVisitor.method).get(4).getValue().asArrayInitializerExpr().getValues().get(j).asDoubleLiteralExpr().asDouble());
                max.add(methodAnnotationVisitor.methodAnnotations.get(methodAnnotationVisitor.method).get(5).getValue().asArrayInitializerExpr().getValues().get(j).asDoubleLiteralExpr().asDouble());
            }
        }
    }

    private static class MethodAnnotationVisitor extends VoidVisitorAdapter<Void> {
        protected HashMap<String, NodeList<MemberValuePair>> methodAnnotations = new HashMap<>();
        protected String method = "";
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            String m = n.getName().asString();
            if (!m.equals("probCalc")) {
                return;
            } else {
                method = m;
            }
            if(n.getAnnotations() != null ){
                for(AnnotationExpr annotationExpr : n.getAnnotations()){
                    NormalAnnotationExpr expr = (NormalAnnotationExpr) annotationExpr;
                    methodAnnotations.put(n.getName().toString(),expr.getPairs());
                }
            }
        }
    }
}

