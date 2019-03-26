package ist.meic.pa.FunctionalProfiler;

import javassist.*;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.Mnemonic;
import javassist.bytecode.CodeIterator;
import java.util.*;

import java.io.*;

class WithFunctionalProfiler {

    public static void main(String[] args) {
        //System.out.println(System.getProperty("java.class.path")); //This prints the classpaths
        
        loadByteCode("ist.meic.pa.FunctionalProfiler.FunctionalCounter");
        loadByteCode("ist.meic.pa.FunctionalProfiler.ImperativeCounter");
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
            CtClass cc = cp.get(className);
            //System.out.println(cc);
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
        try{
            for(int i = 0;i< methodsNames.size();i++){
                CtMethod method = cc.getDeclaredMethod(methodsNames.get(i));
                MethodInfo methodInfo = method.getMethodInfo();
                CodeAttribute ca = methodInfo.getCodeAttribute();
                CodeIterator ci = ca.iterator();
                while(ci.hasNext()){
                    int index = ci.next();
                    int op = ci.byteAt(index);
                    System.out.println("OpCOde: " + op);
                    System.out.println("Iterator: " + Mnemonic.OPCODE[op]);
                    if (op == 180) {
                        int a1 = ci.s16bitAt(index + 1);
                        String fieldName = " " + cc.getClassFile().getConstPool().getFieldrefName(a1); 
                        System.out.println("field name: " + fieldName);
                    }
                }
                LocalVariableAttribute table = (LocalVariableAttribute) methodInfo.getCodeAttribute().getAttribute(javassist.bytecode.LocalVariableAttribute.tag);
                System.out.println("Parametrs: " + method.getMethodInfo());
                System.out.println("Table length: " + table.tableLength());
                for(int j = 0; j < table.tableLength();j++){
                    System.out.println(table.variableName(j));
                }
            }
        }
        catch(Exception e){
            System.out.println("Dont have methods...");
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