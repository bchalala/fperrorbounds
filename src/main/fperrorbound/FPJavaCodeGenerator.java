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
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;



public class FPJavaCodeGenerator {

    private static GetMethodVisitor getmethodvisitor = new GetMethodVisitor();
    private FPJavaCodeGenerator() {}

    public static String generateHarness(String file) throws Exception {
        var in = new FileInputStream(file);
        CompilationUnit cu = JavaParser.parse(in);

        in = new FileInputStream("target/template.java");
        CompilationUnit templateCu = JavaParser.parse(in);
        ClassOrInterfaceDeclaration testharness = templateCu.getClassByName("TestHarness").get();

        // Adds modified methods to the test harness
        var origMethodDecl = getMethod(cu);
        var methodDeclDoubleClone = createMethodDoubleClone(origMethodDecl);
        testharness.addMember(origMethodDecl);
        testharness.addMember(methodDeclDoubleClone);

        // prints the resulting compilation unit to default system output
        return(templateCu.toString());
    }

    // Gets the method we are trying to test
    public static MethodDeclaration getMethod(CompilationUnit cu) {
        EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PUBLIC);
        modifiers.add(Modifier.STATIC);
        getmethodvisitor.visit(cu, null);
        getmethodvisitor.methodDecl.setModifiers(modifiers);
        return getmethodvisitor.methodDecl.clone();
    }

    public static MethodDeclaration createMethodDoubleClone(MethodDeclaration md) {
        md = md.clone();
        md.setName(md.getName() + "DoubleClone");
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
        }
    }
}

