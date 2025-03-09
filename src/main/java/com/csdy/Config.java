package com.csdy;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec.Builder EXBUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue EXTREME = EXBUILDER.comment("完全体模式").define("EXmode",false);

    public static final ForgeConfigSpec EXMODE = EXBUILDER.build();
}
