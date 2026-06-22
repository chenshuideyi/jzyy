package com.csdy.jzyy;

import net.minecraftforge.common.ForgeConfigSpec;

public class JzyyConfig {
    public static final ForgeConfigSpec.Builder CONFIG = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue I_KNOW_WHAT_I_AM_DOING = CONFIG.comment("我知道我在干什么", "开启后不会检查java版本和光影状况").define("i_know_what_i_am_doing", false);

    public static final ForgeConfigSpec.BooleanValue DISABLE_LXTRACK = CONFIG.comment("禁用LxTrack", "开启后将不会启用LxTrack").define("disable_lxtrack", false);

    public static final ForgeConfigSpec JZYY_CONFIG = CONFIG.build();
}