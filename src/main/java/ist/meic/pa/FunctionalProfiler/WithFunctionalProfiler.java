package ist.meic.pa.FunctionalProfiler;

import javassist.*;
import java.util.*;
import java.io.*;

class WithFunctionalProfiler {

    public static void main(String[] args) {
        //printResult();
        //String path = Paths.get("C:\\Users\\Jorge\\Desktop\\IST\\AP\\Project1\\build\\classes\\java\\main\\ist\\meic\\pa\\FunctionalProfiler\\FunctionalCounter");
        //path = path.replaceAll("\\\\", "/");

        loadByteCode("FunctionalCounter");
    }

    private static void printResult() {
        System.out.println(String.format("Total reads: %s Total writes: %s", 6, 1));
        System.out.println(String.format("class FunctionalCounter -> reads: %s writes: %s", 3, 0));
        System.out.println(String.format("class ImperativeCounter -> reads: %s writes: %s", 3, 1));
    }

    private static void loadByteCode(String className) {
        ClassPool cp = ClassPool.getDefault();
        try {
            CtClass cc = cp.get(className);
            cc.writeFile();
        }
        catch (NotFoundException e) {
            System.out.println("Class doesnt exist...");
        }
        catch (IOException | CannotCompileException e1) {
            System.out.println("Error writing file");
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