package com.csdy.jzyy.modifier.modifier.etsh;

public class FoliGear {
    Runtime rt = Runtime.getRuntime();
    int byteToMb = 1024 * 1024;

    long vmMax = rt.maxMemory()/byteToMb;
}
