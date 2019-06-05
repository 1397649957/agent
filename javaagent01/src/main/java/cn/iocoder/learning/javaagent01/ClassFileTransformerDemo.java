package cn.iocoder.learning.javaagent01;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class ClassFileTransformerDemo implements ClassFileTransformer {

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (!className.equalsIgnoreCase("cn/iocoder/learning/Dog")) {
            return null;
        }
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = null;
        try {
            ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
        } catch (IOException e) {
            e.printStackTrace();
        }
        CtBehavior[] declaredBehaviors = ctClass.getDeclaredBehaviors();
        for (CtBehavior declaredBehavior : declaredBehaviors) {
            System.err.println(declaredBehavior.getName());
            if (declaredBehavior.getName().equals("helloWord")){
                System.err.println("1111");
                try {
                    declaredBehavior.insertBefore("System.out.println(\"before\");");
                    declaredBehavior.insertAfter("System.out.println(\"after\");",true);
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                } finally {
                }
            }
        }
        try {
            return ctClass.toBytecode();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getBytesFromFile(String fileName) {
        File file = new File(fileName);
        try (InputStream is = new FileInputStream(file)) {
            // precondition

            long length = file.length();
            byte[] bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int numRead = 0;
            while (offset <bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset < bytes.length) {
                throw new IOException("Could not completely read file "
                        + file.getName());
            }
            is.close();
            return bytes;
        } catch (Exception e) {
            System.out.println("error occurs in _ClassTransformer!"
                    + e.getClass().getName());
            return null;
        }
    }

}
