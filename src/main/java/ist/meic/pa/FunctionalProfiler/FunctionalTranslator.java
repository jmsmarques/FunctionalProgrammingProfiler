package ist.meic.pa.FunctionalProfiler;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.FieldAccess;

public class FunctionalTranslator implements Translator {
    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException {
        CtClass ctClass = pool.get(classname);
        findWriteRead(ctClass);
    }

    private void findWriteRead(CtClass cc) {
        for (CtMethod method : cc.getDeclaredMethods()) {
            try {
                method.instrument(
                        new ExprEditor() {
                            public void edit(FieldAccess m) throws CannotCompileException {
                                //CtMethod method = m.getMethod();
                                //CtClass declaringClass = method.getDeclaringClass();
                                //System.out.println("Class name: " + m.getClassName());
                                //System.out.println("Method name: " + m.getMethodName());
                                /*if (m.getClassName().equals("ist.meic.pa.FunctionalProfiler.Example1") 
                                && m.getMethodName().equals("test")) {
                                    m.replace("{ $1 = 3; $_ = $proceed($$); }");
                                    //System.out.println("ola");
                                }*/
                                if(m.isWriter()) {
                                    System.out.println("write");
                                }
                                else if(m.isReader()) {
                                    System.out.println("read");
                                }                                
                            }
                        });
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }
}
}