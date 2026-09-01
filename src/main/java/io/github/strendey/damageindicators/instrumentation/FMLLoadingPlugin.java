package io.github.strendey.damageindicators.instrumentation.transformer;

import javassist.*;
import net.minecraft.launchwrapper.IClassTransformer;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class EntityPlayerTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("wn".equals(name) || "yz".equals(name) || "net.minecraft.entity.player.EntityPlayer".equals(transformedName)) {
            ClassPool pool = ClassPool.getDefault();
            CtClass classFile = null;

            try {
                classFile = pool.getOrNull(transformedName);
                if (classFile == null) {
                    classFile = pool.getOrNull(name);
                }

                if (classFile != null) {
                    if (classFile.isFrozen()) {
                        classFile.defrost();
                    }
                    classFile.detach(); 
                }

                classFile = pool.makeClass(new ByteArrayInputStream(basicClass));

                CtMethod targetMethod;
                try {
                    targetMethod = classFile.getDeclaredMethod("getDisplayName");
                } catch (NotFoundException e) {
                    targetMethod = classFile.getDeclaredMethod("getDisplayNameString");
                }

                targetMethod.insertBefore("this.displayname = null;");
                return classFile.toBytecode();

            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                if (classFile != null) {
                    classFile.detach();
                }
            }
        }

        return basicClass;
    }
}
