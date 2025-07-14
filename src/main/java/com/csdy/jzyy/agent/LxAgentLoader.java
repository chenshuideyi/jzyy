package com.csdy.jzyy.agent;

public class LxAgentLoader {
    public static void premain(String agentArgs, java.lang.instrument.Instrumentation inst) {
        inst.addTransformer(new LxAgent());
    }
}
