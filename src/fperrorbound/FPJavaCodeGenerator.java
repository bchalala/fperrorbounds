package fperrorbound;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.util.EnumSet;
import java.util.ArrayList;



public class FPJavaCodeGenerator {

    private static GetMethodVisitor getmethodvisitor = new GetMethodVisitor();
    private static DoubleReplacementVisitor doublereplacementvisitor = new DoubleReplacementVisitor();
    
    private MethodDeclaration origMethodDecl;
    private MethodDeclaration allDoublesMethodDecl;
    private CompilationUnit template;
    private FPErrorAnnotation annotation;

    private int harnessNumber;

    public FPJavaCodeGenerator(String file, FPErrorAnnotation annotation) throws Exception {
        // Parse the template file
        var in = new FileInputStream("target/template.java");
        template = JavaParser.parse(in);
        
        // Parse the input
        in = new FileInputStream(file);
        CompilationUnit cu = JavaParser.parse(in);
        origMethodDecl = getMethod(cu);
        allDoublesMethodDecl = createMethodDoubleClone(origMethodDecl);

        harnessNumber = 0;

        this.annotation = annotation;
    }

    private FPTestProgram genHarness(MethodDeclaration fnFloat) {
        CompilationUnit templateClone = template.clone();
        ClassOrInterfaceDeclaration testharness = templateClone.getClassByName("TestHarness").get();
        var harnessName = "TestHarness" + harnessNumber;
        testharness.setName(harnessName);
        harnessNumber++;

        // Adds modified methods to the test harness
        testharness.addMember(fnFloat);
        testharness.addMember(allDoublesMethodDecl);

        // Sample all arguments
        MethodDeclaration testMethod = testharness.getMethodsByName("test").get(0);
        BlockStmt testbody = testMethod.getBody().get();
        sampleArgs(testbody, fnFloat.getParameters().size());
        buildTestCalls(testbody, fnFloat.getParameters().size());

        System.out.println(templateClone.toString());
        System.out.println(harnessName);
        return new FPTestProgram(templateClone.toString(), harnessName);
    } 

    public FPTestProgram genStandardHarness() throws Exception {
        return genHarness(origMethodDecl);
    }

    public ArrayList<FPTestProgram> generateAllProgramPermutations() {
        return null;
    }

    public void sampleArgs(BlockStmt body, int numSamples) {
        if (numSamples <= 0)
            return;
        
        MethodCallExpr sample = new MethodCallExpr("sample");
        sample.addArgument(String.valueOf(annotation.min.get(annotation.min.size() - numSamples)));
        sample.addArgument(String.valueOf(annotation.max.get(annotation.max.size() - numSamples)));
        VariableDeclarationExpr decl = new VariableDeclarationExpr(PrimitiveType.doubleType(), "a" + numSamples);
        AssignExpr assignexpr = new AssignExpr(decl, sample, AssignExpr.Operator.ASSIGN);
        body.addStatement(0, assignexpr);

        sampleArgs(body, numSamples - 1);
    }
    
    public static void buildTestCalls(BlockStmt body, int argnums) {
        AddArgumentsVisitor doubleArguments = new AddArgumentsVisitor("fnDouble", argnums, false);
        doubleArguments.visit(body, null);

        AddArgumentsVisitor floatArguments = new AddArgumentsVisitor("fnFloat", argnums, true);
        floatArguments.visit(body, null);
    }

    // Gets the method we are trying to test
    public static MethodDeclaration getMethod(CompilationUnit cu) {
        EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PUBLIC);
        modifiers.add(Modifier.STATIC);
        getmethodvisitor.visit(cu, null);
        getmethodvisitor.methodDecl.setModifiers(modifiers);
        getmethodvisitor.methodDecl.remove(getmethodvisitor.methodDecl.getAnnotation(0));
        return getmethodvisitor.methodDecl.clone();
    }

    public static MethodDeclaration createMethodDoubleClone(MethodDeclaration md) {
        md = md.clone();
        md.setName("fnDouble");
        doublereplacementvisitor.visit(md, null);
        return md;
    }

    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
    private static class GetMethodVisitor extends VoidVisitorAdapter<Void> {
        protected MethodDeclaration methodDecl;

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            methodDecl = n;
            methodDecl.setName("fnFloat");
        }
    }

    private static class DoubleReplacementVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(PrimitiveType n, Void arg) {
            if (n.getType() == PrimitiveType.Primitive.FLOAT) {
                n.setType(PrimitiveType.Primitive.DOUBLE);
            }
        }
    }

    private static class GetDoubleVariables extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(PrimitiveType n, Void arg) {
            if (n.getType() == PrimitiveType.Primitive.DOUBLE) {
                System.out.println(n.getParentNode());
            }
        }
    }

    private static class AddArgumentsVisitor extends VoidVisitorAdapter<Void> {
        private String method;
        private int num;
        private boolean castFloat;

        public AddArgumentsVisitor(String searchMethod, int argnums, boolean castFloat) {
            this.method = searchMethod;
            this.num = argnums;
            this.castFloat = castFloat;
        }

        @Override
        public void visit(MethodCallExpr n, Void arg) {
            if (n.getNameAsString().equals(method)) {
                String argname = "a";
                if (castFloat) {
                    argname = "(float) " + argname;
                }

                for (int i = 1; i <= num; i++) {
                    n.addArgument(argname + i);
                }
            }
        }
    }
}
