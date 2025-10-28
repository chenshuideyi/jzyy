package com.csdy.jzyy;

import net.minecraftforge.common.ForgeConfigSpec;

///为什么没有forge:config????
public class JzyyConfig {
    public static final ForgeConfigSpec.Builder CONFIG = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue I_KNOW_WHAT_I_AM_DOING = CONFIG.comment("我知道我在干什么", "开启后不会检查java版本和光影状况").define("i_know_what_i_am_doing", false);

    public static final ForgeConfigSpec JZYY_CONFIG = CONFIG.build();
}
