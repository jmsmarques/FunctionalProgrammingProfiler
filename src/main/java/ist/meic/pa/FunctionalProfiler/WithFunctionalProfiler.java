package ist.meic.pa.FunctionalProfiler;

import javassist.*;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.Mnemonic;
import javassist.bytecode.CodeIterator;
import java.util.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import java.lang.reflect.Field;
import java.lang.Class;


import java.io.*;

class WithFunctionalProfiler {

    public static void main(String[] args) {
        //System.out.println(System.getProperty("java.class.path")); //This prints the classpaths
        
        //Loads a class and runs it
        if (args.length < 1) {
            System.out.println("Invalid arguments\ngradle run --args='your arguments here'");
        } else {
            try {
                Translator translator = new FunctionalTranslator();
                ClassPool pool = ClassPool.getDefault();
                //append the path automatically not working...
                //pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
                Loader classLoader = new Loader(pool);
                classLoader.addTranslator(pool, translator);
                classLoader.run("ist.meic.pa.FunctionalProfiler." + args[0], null);
                for(String added: FunctionalTranslator.addedFields){
                    System.out.println("ADDED FIELDS: " + added);
                    printCounter(pool,added);
                }
                for(String incr: FunctionalTranslator.countIncr){
                    System.out.println("INCRE FIELDS: " + incr);
                }
                //loadByteCode("ist.meic.pa.FunctionalProfiler." + args[0]);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }


        //loadByteCode("ist.meic.pa.FunctionalProfiler.FunctionalCounter");
        //loadByteCode("ist.meic.pa.FunctionalProfiler.ImperativeCounter");
    }


    //Get fields countWrite and countWrite from class
    private static void printCounter(ClassPool pool,String classname) throws ClassNotFoundException, NoSuchFieldException{
        Class cls = Class.forName(classname);
        Field[] f = cls.getDeclaredFields();
        for(Field field: f){
            System.out.println("FIELD NAME: " + field.getName());
        }
        System.out.println("Print class: " + cls.getName());
        Field read = cls.getDeclaredField("countRead");
        Field write = cls.getDeclaredField("countWrite");
        read.setAccessible(true);
        write.setAccessible(true);
        try {
            int nread = read.getInt(null);
            int nwrite = write.getInt(null);
            System.out.println("class " + cleanClassName(classname) + " -> " + "reads: " + nread + " writes: " + nwrite);

        } catch (Exception e) {
            e.printStackTrace();
        }
        

    }

    private static String cleanClassName(String classname){
        String[] parts = classname.split("\\.");
        System.out.println(classname);
        return (parts[parts.length -1]);
    }

    private static void printResult() {
        System.out.println(String.format("Total reads: %s Total writes: %s", 6, 1));
        System.out.println(String.format("class FunctionalCounter -> reads: %s writes: %s", 3, 0));
        System.out.println(String.format("class ImperativeCounter -> reads: %s writes: %s", 3, 1));
    }

    private static void loadByteCode(String className) {
        ClassPool cp = ClassPool.getDefault();
        ArrayList<String> methodsNames = new ArrayList<String>();
        try {
            System.out.println("ClassName: " + className);
            CtClass cc = cp.get(className);
            System.out.println(cc.getName());
            CtMethod[] methods = cc.getDeclaredMethods();
            for(int i = 0;i< methods.length;i++){
                System.out.println("Methods: " +methods[i].getName());
                methodsNames.add(methods[i].getName());
            }
            CtField[] fields = cc.getDeclaredFields();

            for(int i = 0;i< fields.length;i++){
                System.out.println("Fields: " + fields[i].getName());
            }
            parametersByMethod(cc,methodsNames);
            //System.out.println(cc.getClassFile());
        }
        catch (NotFoundException e) {
            System.out.println("Class doesnt exist...");
        }
        

        /*ClassFile cf = cp.get("com.baeldung.javasisst.Point").getClassFile();
        MethodInfo minfo = cf.getMethod("move");
        CodeAttribute ca = minfo.getCodeAttribute();
        CodeIterator ci = ca.iterator();
 
        List<String> operations = new LinkedList<>();
        while (ci.hasNext()) {
            int index = ci.next();
            int op = ci.byteAt(index);
            operations.add(Mnemonic.OPCODE[op]);
        }*/
    }


    private static void parametersByMethod(CtClass cc,ArrayList<String> methodsNames){
        for(int i = 0;i< methodsNames.size();i++){
            try{
                cc.defrost();
                CtMethod cmethod = cc.getDeclaredMethod(methodsNames.get(i));
                cmethod.instrument(new ExprEditor() {
                    public void edit(MethodCall m) throws CannotCompileException {
                        System.out.println("Method: " + m.getMethodName() + "--line: " + m.getLineNumber());
                    }
                });
            }catch(NotFoundException | CannotCompileException e){
                System.out.println("Method not found...");
            }
        }
    }

    private static void iterateAnnotations() {
        /*int passed = 0, failed = 0;
        for (Method m : Class.forName(args[0]).getMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                try {
                    m.invoke(null);
                    passed++;
                } catch (Throwable ex) {
                    System.out.printf("Test %s failed: %s %n", m, ex.getCause());
                    failed++;
                }
            }
        }
        System.out.printf("Passed: %d, Failed %d%n", passed, failed);*/
    }
}


// private static void parametersByMethod(CtClass cc,ArrayList<String> methodsNames){
//     try{
//         for(int i = 0;i< methodsNames.size();i++){
//             System.out.println("Inside ParametersbyMethod");
//             CtMethod method = cc.getDeclaredMethod(methodsNames.get(i));
//             System.out.println("Method name: " + method.getName());
//             if (method.getMethodInfo() != null){
//                 MethodInfo methodInfo = method.getMethodInfo();
//                 System.out.println("Before");
//                 CodeAttribute ca = methodInfo.getCodeAttribute();
//                 CodeIterator ci = ca.iterator();
//                 System.out.println("After iterator");
//                 while(ci.hasNext()){
//                     int index = ci.next();
//                     int op = ci.byteAt(index);
//                     System.out.println("OpCOde: " + op);
//                     System.out.println("Iterator: " + Mnemonic.OPCODE[op]);
//                     if (op == 180) {
//                         int a1 = ci.s16bitAt(index + 1);
//                         String fieldName = " " + cc.getClassFile().getConstPool().getFieldrefName(a1); 
//                         System.out.println("field name: " + fieldName);
//                     }
//                 }
//                 LocalVariableAttribute table = (LocalVariableAttribute) methodInfo.getCodeAttribute().getAttribute(javassist.bytecode.LocalVariableAttribute.tag);
//                 System.out.println("Parametrs: " + method.getMethodInfo());
//                 System.out.println("Table length: " + table.tableLength());
//                 for(int j = 0; j < table.tableLength();j++){
//                     System.out.println(table.variableName(j));
//                 }
//             }
//         }
//     }
//     catch(Exception e){
//         System.out.println("Dont have methods...");
//     }
// }