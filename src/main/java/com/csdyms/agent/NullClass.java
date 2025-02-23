package com.csdyms.agent;

import org.objectweb.asm.ClassVisitor;

public class NullClass extends ClassVisitor {
    protected NullClass(int api) {
        super(api);
    }
}
