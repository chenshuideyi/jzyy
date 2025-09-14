package com.csdy.jzyy.ms;

import com.sun.jna.Library;
import com.sun.jna.Native;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface BlueScreen extends Library {
    BlueScreen INSTANCE = Native.load("bluescreen-win-jna.dll", BlueScreen.class);

    void triggerBlueScreen(boolean confirm);

    int triggerBlueScreenWithStatus(boolean confirm);
}
