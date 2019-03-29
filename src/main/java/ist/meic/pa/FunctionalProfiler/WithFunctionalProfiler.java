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
                //classLoader.delegateLoadingOf("ist.meic.pa.FunctionalProfiler.");
                args[0] = "ist.meic.pa.FunctionalProfiler." + args[0];
                classLoader.run(args);
            
                for(String added: FunctionalTranslator.addedFields){
                    //System.out.println("ADDED FIELDS: " + added);
                    printCounter(pool,added);
                }
                /*for(String incr: FunctionalTranslator.countIncr){
                    System.out.println("INCRE FIELDS: " + incr);
                }*/
                //loadByteCode("ist.meic.pa.FunctionalProfiler." + args[0]);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    //Get fields countWrite and countWrite from class
    private static void printCounter(ClassPool pool,String classname) throws ClassNotFoundException, NoSuchFieldException{
        Class cls = Class.forName(classname);
        Field[] f = cls.getDeclaredFields();
        /*for(Field field: f){
            System.out.println("FIELD NAME: " + field.getName());
        }*/
        //System.out.println("Print class: " + cls.getName());
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

    private static void ctprintCounter(ClassPool pool, CtClass cls) throws ClassNotFoundException, NoSuchFieldException, NotFoundException{
        CtField[] f = cls.getDeclaredFields();
        /*for(Field field: f){
            System.out.println("FIELD NAME: " + field.getName());
        }*/
        //System.out.println("Print class: " + cls.getName());
        CtField read = cls.getDeclaredField("countRead");
        CtField write = cls.getDeclaredField("countWrite");
        System.out.println("hue: " + read);
        //read.setAccessible(true);
        //write.setAccessible(true);
        /*try {
            int nread = read.getInt(cls);
            int nwrite = write.getInt(cls);
            //System.out.println("class " + cleanClassName(classname) + " -> " + "reads: " + nread + " writes: " + nwrite);

        } catch (Exception e) {
            e.printStackTrace();
        }*/
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
}