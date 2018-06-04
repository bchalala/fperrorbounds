package edu.baj.fperrorbound;

import java.io.FileInputStream;
import java.util.Optional;
import java.util.EnumSet;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.YamlPrinter;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.AssignExpr;



public class FPJavaCodeGenerator {

    private static GetMethodVisitor getmethodvisitor = new GetMethodVisitor();
    private static DoubleReplacementVisitor doublereplacementvisitor = new DoubleReplacementVisitor();

    public static String generateHarness(String file) throws Exception {
        // Parse the input
        var in = new FileInputStream(file);
        CompilationUnit cu = JavaParser.parse(in);

        // Parse the template file
        in = new FileInputStream("target/template.java");
        CompilationUnit templateCu = JavaParser.parse(in);
        ClassOrInterfaceDeclaration testharness = templateCu.getClassByName("TestHarness").get();

        // Adds modified methods to the test harness
        var origMethodDecl = getMethod(cu);
        testharness.addMember(origMethodDecl);
        var methodDeclDoubleClone = createMethodDoubleClone(origMethodDecl);
        testharness.addMember(methodDeclDoubleClone);

        // Sample all arguments
        MethodDeclaration testMethod = testharness.getMethodsByName("test").get(0);
        BlockStmt testbody = testMethod.getBody().get();
        sampleArgs(testbody, origMethodDecl.getParameters().size());
        buildTestCalls(testbody, origMethodDecl.getParameters().size());

        return(templateCu.toString());
    }

    public static void sampleArgs(BlockStmt body, int numSamples) {
        if (numSamples <= 0)
            return;
        
        MethodCallExpr sample = new MethodCallExpr("sample");
        sample.addArgument("-1000");
        sample.addArgument("1000");
        
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
