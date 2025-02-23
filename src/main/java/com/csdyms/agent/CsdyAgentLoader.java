package com.csdyms.agent;

import java.lang.instrument.Instrumentation;

public class CsdyAgentLoader {
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        CsdyAgent agent = new CsdyAgent();
        instrumentation.addTransformer(agent);
    }
}
