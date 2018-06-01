package edu.baj.fperrorbounds;


import javax.tools.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//NOTES:
// - Expects there to not be any overloading
// - Making method static makes it much easier but not need

public class InMemoryCompiler{

    protected String className = "";
    private boolean classLoaded = false;
    private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    private DiagnosticCollector<JavaFileObject> diagnostic = new DiagnosticCollector<JavaFileObject>();

    private Class<?> loadedClass;
    public InMemoryCompiler() {}


    public void compileInMemory(String classname, String source)
    {
        this.className = classname;
        JavaFileObject file = new JavaSourceFromString(this.className, source);

        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        JavaCompiler.CompilationTask task = compiler.getTask(null, null, diagnostic, null, null, compilationUnits);

        boolean success = task.call();
        for (Diagnostic diagnostic : diagnostic.getDiagnostics()) {
            System.out.println(diagnostic.getCode());
            System.out.println(diagnostic.getKind());
            System.out.println(diagnostic.getPosition());
            System.out.println(diagnostic.getStartPosition());
            System.out.println(diagnostic.getEndPosition());
            System.out.println(diagnostic.getSource());
            System.out.println(diagnostic.getMessage(null));

        }
    }
    public void loadCompiledClass() throws Exception
    {
        if(classLoaded)
            throw new RuntimeException("Can't reload this class...");
        try
        {
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {new File("").toURI().toURL()});
            this.loadedClass = Class.forName(this.className, true, classLoader);
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    //This method will return a Method class.
    public Method getMethod(final String methodname)
    {
       List<Method> methods =  Arrays.stream(this.loadedClass.getMethods()).filter(x -> x.getName().equals(methodname)).collect(Collectors.toList());
       if(methods.size() != 1)
           throw new RuntimeException("Number of methods with name: " + methodname + " is not 1...");
       return methods.get(0);
    }
    public Class<?> getInstanciatedClass()
    {
        return this.loadedClass;
    }

}

class JavaSourceFromString extends SimpleJavaFileObject {
    final String code;

    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}


//Example code:
// public class Main {
//     public static String source = "public class HelloWorld { public void testInstanced(){System.out.println(\"Instanced!!\");}public static void testStatic(){System.out.println(\"Static!!\");}}";


//     public static void main(String[] args) {
//         try {
//             System.out.println("About to invoke");
//             InMemoryCompiler imc = new InMemoryCompiler();
//             imc.compileInMemory("HelloWorld", source);
//             imc.loadCompiledClass();
//             imc.getMethod("testInstanced").invoke(imc.getInstanciatedClass().getConstructor(null).newInstance(null));
//             imc.getMethod("testStatic").invoke(null);
//         }
//         catch(Exception e)
//         {
//             System.out.println(e.getMessage());
//         }

//     }
// }
