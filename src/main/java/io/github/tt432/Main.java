package io.github.tt432;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author TT432
 */
public class Main {
    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new ClassExportTransformer(), true);
    }

    static class ClassExportTransformer implements ClassFileTransformer {
        private static final String OUTPUT_DIR = ".class.export/class";

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer)
                throws IllegalClassFormatException {
            if (className != null) {
                String fileName = className.replace("/", File.separator) + ".class";
                File outFile = new File(OUTPUT_DIR, fileName);
                outFile.getParentFile().mkdirs(); // 确保目录存在
                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    fos.write(classfileBuffer);
                } catch (IOException e) {
                    System.err.println("Failed to write class file: " + fileName);
                    e.printStackTrace();
                }
            }
            return classfileBuffer; // 返回原始字节码，不做修改
        }
    }
}