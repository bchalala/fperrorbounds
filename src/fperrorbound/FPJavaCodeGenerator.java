package fperrorbound;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.YamlPrinter;

import java.lang.RuntimeException;
import java.io.FileInputStream;
import java.util.EnumSet;
import java.util.ArrayList;



public class FPJavaCodeGenerator {

    private static DoubleReplacementVisitor doublereplacementvisitor = new DoubleReplacementVisitor();
    
    private CompilationUnit inputCU;
    private MethodDeclaration origMethodDecl;
    private MethodDeclaration allDoublesMethodDecl;
    private CompilationUnit template;
    private FPErrorAnnotation annotation;

    private int harnessNumber;

    private YamlPrinter printer = new YamlPrinter(true);

    public FPJavaCodeGenerator(String file, FPErrorAnnotation annotation) throws Exception {

        // Parse the template file
        var in = new FileInputStream("target/template.java");
        template = JavaParser.parse(in);
        
        // Parse the input
        in = new FileInputStream(file);
        inputCU = JavaParser.parse(in);
        origMethodDecl = getMethod(inputCU);
        if (origMethodDecl == null) {
            System.out.println("Error: Input program needs to define probCalc");
            throw new RuntimeException();
        }
        
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
        sampleArgs(testharness, testbody, fnFloat.getParameters().size());
        buildTestCalls(testbody, fnFloat.getParameters().size());

        return new FPTestProgram(templateClone.toString(), harnessName);
    } 

    public FPTestProgram genStandardHarness() throws Exception {
        return genHarness(origMethodDecl);
    }

    public ArrayList<FPTestProgram> generateAllProgramPermutations() {
        var floatMethod = allDoublesMethodDecl.clone();
        floatMethod.setName("fnFloat");
        System.out.println(printer.output(floatMethod));

        // Things that can be floats are: Return statement (+ output type), Variables

        GetDoubleVariables gdv = new GetDoubleVariables();
        gdv.visit(allDoublesMethodDecl.getBody().get(), null);
        for (Node n : gdv.doubleNodes) {
            String s = printer.output(n.getParentNode().get());
            System.out.println(n.getParentNode().get());
            System.out.println(s);
        }
        return null;
    }

    public void sampleArgs(ClassOrInterfaceDeclaration testharness, BlockStmt body, int numSamples) {
        String methodCall = "";
        if (annotation.sampleMethod.equals("uniform")) {
            methodCall = "uniform";
        } else if (annotation.sampleMethod.equals("gaussian")) {
            methodCall = "gaussian";
        } else {
            methodCall = annotation.sampleMethod;
            GetMethodVisitor gmv = new GetMethodVisitor(methodCall, methodCall);
            gmv.visit(inputCU, null);
            testharness.addMember(gmv.methodDecl);
        }

        
        for (int i = numSamples - 1; i >= 0; i--) {
            MethodCallExpr sample = new MethodCallExpr(methodCall);
            if (annotation.sampleMethod.equals("uniform") || annotation.sampleMethod.equals("gaussian")) {
                sample.addArgument(String.valueOf(annotation.min.get(i)));
                sample.addArgument(String.valueOf(annotation.max.get(i)));
            }
            VariableDeclarationExpr decl = new VariableDeclarationExpr(PrimitiveType.doubleType(), "a" + i);
            AssignExpr assignexpr = new AssignExpr(decl, sample, AssignExpr.Operator.ASSIGN);
            body.addStatement(0, assignexpr);
        }
    }
    
    public static void buildTestCalls(BlockStmt body, int argnums) {
        AddArgumentsVisitor doubleArguments = new AddArgumentsVisitor("fnDouble", argnums, false);
        doubleArguments.visit(body, null);

        AddArgumentsVisitor floatArguments = new AddArgumentsVisitor("fnFloat", argnums, true);
        floatArguments.visit(body, null);
    }

    // Gets the method we are trying to test
    public static MethodDeclaration getMethod(CompilationUnit cu) {
        GetMethodVisitor getmethodvisitor = new GetMethodVisitor("probCalc", "fnFloat");
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
        ChangeFloatingPointLiteralVisitor cfp = new ChangeFloatingPointLiteralVisitor(true);
        cfp.visit(md, null);
        return md;
    }

    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
    private static class GetMethodVisitor extends VoidVisitorAdapter<Void> {
        protected MethodDeclaration methodDecl = null;
        private String m;
        private String setName;

        public GetMethodVisitor(String methodString, String setName) {
            m = methodString;
            this.setName = setName;
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (n.getName().toString().equals(m))
            {
                methodDecl = n;
                methodDecl.setName(setName);
            }
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

                for (int i = 0; i < num; i++) {
                    n.addArgument(argname + i);
                }
            }
        }
    }

    private static class GetDoubleVariables extends VoidVisitorAdapter<Void> {
        protected ArrayList<Node> doubleNodes = new ArrayList<>();

        @Override
        public void visit(PrimitiveType n, Void arg) {
            if (n.getType() == PrimitiveType.Primitive.DOUBLE) {
                doubleNodes.add(n);
            }
        }
    }

    private static class ChangeFloatingPointLiteralVisitor extends VoidVisitorAdapter<Void> {
        boolean floatToDouble;

        public ChangeFloatingPointLiteralVisitor(boolean floatToDouble) {
            this.floatToDouble = floatToDouble;
        }

        @Override
        public void visit(DoubleLiteralExpr n, Void arg) {
            if (n.getValue().contains("x")) {
                return;
            }

            char toReplace = 'd';
            char replaceStr = 'f';
            if (floatToDouble) {
                toReplace = 'f';
                replaceStr = 'd';
            }
            String newValue = n.getValue().replace(toReplace, replaceStr);
            if (!newValue.contains("f") && !floatToDouble) {
                newValue = newValue + "f";
            }

            n.setValue(newValue);
        }
    }
}
