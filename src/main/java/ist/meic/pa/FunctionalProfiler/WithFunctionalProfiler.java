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
        //Loads a class and runs it
        if (args.length < 1) {
            System.out.println("Invalid arguments\ngradle run --args='your arguments here'");
        } else {
            try {
                Translator translator = new FunctionalTranslator();
                ClassPool pool = ClassPool.getDefault();
                Loader classLoader = new Loader(pool);
                classLoader.addTranslator(pool, translator);
                args[0] = "ist.meic.pa.FunctionalProfiler." + args[0];
                classLoader.run(args);
            
                for(String added: FunctionalTranslator.addedFields){
                    printCounter(pool,added);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    //Get fields countWrite and countWrite from class
    private static void printCounter(ClassPool pool,String classname) throws ClassNotFoundException, NoSuchFieldException{
        Class cls = Class.forName(classname);
        Field[] f = cls.getDeclaredFields();
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
        return (parts[parts.length -1]);
    }
}