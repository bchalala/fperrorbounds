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

public class FPErrorAnnotation {
    public double epsilon = 0.03;
    public double confidence = .95;
    public double precision = 0.01;
    public double[] min = {-1000.0};
    public double[] max = {1000.0};

    FPErrorAnnotation(String file) throws Exception{
        HashMap<String, Integer> methodSampleMap = new HashMap<>();
        String method = null;
        var in = new FileInputStream(file);
        CompilationUnit cu = JavaParser.parse(in);
        MethodAnnotationVisitor methodAnnotationVisitor = new MethodAnnotationVisitor();
        methodAnnotationVisitor.visit(cu,null);
        epsilon = methodAnnotationVisitor.methodAnnotations.get(methodAnnotationVisitor.method).get(0).getValue().asDoubleLiteralExpr().asDouble();
        confidence = methodAnnotationVisitor.methodAnnotations.get(methodAnnotationVisitor.method).get(1).getValue().asDoubleLiteralExpr().asDouble();
        precision = methodAnnotationVisitor.methodAnnotations.get(methodAnnotationVisitor.method).get(2).getValue().asDoubleLiteralExpr().asDouble();
//        min = methodAnnotationVisitor.methodAnnotations.get(methodAnnotationVisitor.method).get(3).getValue().asDoubleLiteralExpr().asDouble()
//        max = methodAnnotationVisitor.methodAnnotations.get(methodAnnotationVisitor.method).get(4).getValue().asDoubleLiteralExpr().asDouble();

    }

    private static class MethodAnnotationVisitor extends VoidVisitorAdapter<Void> {
        protected HashMap<String, NodeList<MemberValuePair>> methodAnnotations = new HashMap<>();
        protected String method;
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            method = n.getName().asString();
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

