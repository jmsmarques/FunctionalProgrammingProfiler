package ist.meic.pa.FunctionalProfiler;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.FieldAccess;
import javassist.bytecode.MethodInfo;
import java.util.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.Class;

public class FunctionalTranslator implements Translator {
    public static Set<String> addedFields = new HashSet<String>();
    public static Set<String> countIncr = new HashSet<String>();
    public static Set<CtClass> teste = new HashSet<CtClass>();

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException {
        CtClass ctClass = pool.get(classname);
        
        //System.out.println("Find Methods");
        //findMethods(pool,ctClass);
        //System.out.println("Find Read Write");

        findWriteRead(pool,ctClass,addedFields,countIncr);

        
        // for(String added: this.addedFields){
        //     System.out.println("ADDED FIELDS: " + added);
        // }
    }

    public void addCounters(CtClass ctclass,FieldAccess fa,CtMethod cmethod,Set<String> countInc) throws CannotCompileException,NotFoundException,IOException{
        //System.out.println("METHOD: " + cmethod.getName());

        if (fa.isReader()){
            if(countInc.add(fa.getClassName() + " " + cmethod.getName() + " read")){
                cmethod.insertBefore("{countRead++;System.out.println(\"CountRead:\" + countRead);}");
                //ctclass.toClass();
            }
        }
        else if(fa.isWriter()){
            if(countInc.add(fa.getClassName() + " " + cmethod.getName() + " write")){
                cmethod.insertBefore("{countWrite++;System.out.println(\"CountWrite:\" + countWrite);}");
                //ctclass.toClass();
            }
        }
    }

    private void findWriteRead(ClassPool pool,CtClass cc,Set<String> addedFields,Set<String> countInc) {
        int lastEleIndex = cc.getDeclaredMethods().length - 1;
        for (CtMethod method : cc.getDeclaredMethods()) {
            try {
                method.instrument(
                        new ExprEditor() {
                            public void edit(FieldAccess fa) throws CannotCompileException {
                                try {
                                    if (!fa.getClassName().toLowerCase().contains("java".toLowerCase())){
                                        CtClass ctclass = pool.get(fa.getClassName());
                                        ctclass.defrost();
                                        //System.out.println("Class: " + ctclass.getName());
                                        if(teste.add(cc));
                                        if(addedFields.add(fa.getClassName())){
                                            CtField countReadField = CtField.make("public static int countRead = 0;",ctclass);
                                            CtField countWriteField = CtField.make("public static int countWrite = 0;",ctclass);
                                            //System.out.println("Read: " + countReadField.getName());
                                            ctclass.addField(countReadField);
                                            ctclass.addField(countWriteField);
                                            // System.out.println("New field added...");
                                        }
                                        CtMethod cmethod = ctclass.getDeclaredMethod(method.getName());
                                        addCounters(ctclass,fa,cmethod,countInc);
                                        // System.out.println("method: "  + cmethod.getName());
                                        ctclass.toClass();
                                        ctclass.defrost();
                                    }
                                    
                                } catch (Exception e) {
                                    // e.printStackTrace();
                                }                            
                            }
                        });
            } catch (CannotCompileException e) {
                // e.printStackTrace();
            }
        }
}
}