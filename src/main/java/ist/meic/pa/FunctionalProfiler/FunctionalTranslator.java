package ist.meic.pa.FunctionalProfiler;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.FieldAccess;
import javassist.bytecode.MethodInfo;
import java.util.*;

public class FunctionalTranslator implements Translator {
    public static Set<String> addedFields = new HashSet<String>();
    public static Set<String> countIncr = new HashSet<String>();

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException {
        CtClass ctClass = pool.get(classname);
        
        //System.out.println("Find Methods");
        //findMethods(pool,ctClass);
        //System.out.println("Find Read Write");
        System.out.println("Class Name onLoad(): " + ctClass.getName());
        findWriteRead(pool,ctClass,addedFields,countIncr);
        // for(String added: this.addedFields){
        //     System.out.println("ADDED FIELDS: " + added);
        // }
    }



    private void findMethods(ClassPool pool,CtClass cc){
        for(CtMethod method: cc.getDeclaredMethods()){
            try {
                System.out.println("Method: " + method.getName());
                method.instrument(new ExprEditor() {
                    public void edit(MethodCall mc) throws CannotCompileException {
                        try {
                            CtClass ctclass = pool.get(mc.getClassName());
                            CtMethod ctmethod = mc.getMethod();
                            System.out.println("ClassName: " + ctclass.getName() + " | MethodName: " + ctmethod.getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void addCounters(FieldAccess fa,CtMethod cmethod,Set<String> countInc) throws CannotCompileException{
        if (fa.isReader()){
            if(countInc.add(fa.getClassName() + cmethod.getName() + "read")){
                System.out.println("CountReader added!");
                cmethod.insertBefore("{$0.countRead = $0.countRead + 1;}");
            }
        }
        else if(fa.isWriter()){
            if(countInc.add(fa.getClassName() + cmethod.getName() + "write")){
                cmethod.insertBefore("{$0.countWrite = $0.countWrite + 1;}");
                System.out.println("CountWriter added!");
            }
        }
    }

    private void findWriteRead(ClassPool pool,CtClass cc,Set<String> addedFields,Set<String> countInc) {
        int lastEleIndex = cc.getDeclaredMethods().length -1;
        for (CtMethod method : cc.getDeclaredMethods()) {
            try {
                method.instrument(
                        new ExprEditor() {
                            public void edit(FieldAccess fa) throws CannotCompileException {
                                try {
                                    if (!fa.getClassName().toLowerCase().contains("java".toLowerCase())){
                                        
                                        CtClass ctclass = pool.get(fa.getClassName());
                                        System.out.println("Class: " + ctclass.getName());
                                        if(addedFields.add(fa.getClassName())){
                                            CtField countReadField = CtField.make("public static int countRead = 0;",ctclass);
                                            CtField countWriteField = CtField.make("public static int countWrite = 0;",ctclass);
                                            ctclass.addField(countReadField);
                                            ctclass.addField(countWriteField);
                                        }
                                        CtMethod cmethod = ctclass.getDeclaredMethod(method.getName());
                                        addCounters(fa,cmethod,countInc);
                                        System.out.println("method: "  + cmethod.getName());
                                    }
                                    
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }                            
                            }
                        });
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }
}
}