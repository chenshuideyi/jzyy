package com.csdyms.test;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class SystemVolume {

    public interface WinMM extends Library {
        WinMM INSTANCE = Native.load("winmm", WinMM.class);
        int waveOutGetVolume(Pointer hwo, IntByReference dwVolume);
    }

    public static int getCurrentVolume() {
        IntByReference volumeRef = new IntByReference();
        WinMM.INSTANCE.waveOutGetVolume(null, volumeRef);
        int volume = volumeRef.getValue();
        int leftVolume = volume & 0xFFFF;
        int rightVolume = volume >> 16;
        return (leftVolume + rightVolume);
    }

}