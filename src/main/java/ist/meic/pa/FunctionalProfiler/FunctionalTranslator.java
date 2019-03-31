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

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException {
        CtClass ctClass = pool.get(classname);
        findWriteRead(pool,ctClass,addedFields,countIncr);
    }

    public void addCounters(CtClass ctclass,FieldAccess fa,CtMethod cmethod) throws CannotCompileException,NotFoundException,IOException{
        if (fa.isReader()){
            if (countIncr.add(fa.getClassName() + cmethod.getName() + "read")){
                cmethod.insertBefore("{countRead++;}");
            }
        }
        else if(fa.isWriter()){
            if(countIncr.add(fa.getClassName() + cmethod.getName() + "write")){
                cmethod.insertBefore("{countWrite++;}");
            }            
        }
    }

    private void findWriteRead(ClassPool pool,CtClass cc,Set<String> addedFields,Set<String> countIncr) {
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
                                        if(addedFields.add(fa.getClassName())){
                                            CtField countReadField = CtField.make("public static int countRead = 0;",ctclass);
                                            CtField countWriteField = CtField.make("public static int countWrite = 0;",ctclass);
                                            ctclass.addField(countReadField);
                                            ctclass.addField(countWriteField);
                                        }
                                        CtMethod cmethod = ctclass.getDeclaredMethod(method.getName());
                                        addCounters(ctclass,fa,cmethod);
                                        ctclass.toClass();
                                        ctclass.defrost();
                                    }
                                    
                                } catch (Exception e) {
                                    //e.printStackTrace();
                                }                            
                            }
                        });
            } catch (CannotCompileException e) {
                //e.printStackTrace();
            }
        }
    }
}