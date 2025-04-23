package com.csdy.jzyy;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.model.NBTKeyModel;
import slimeknights.tconstruct.library.tools.SlotType;

public class DxSlots {
    public static SlotType DX = SlotType.getOrCreate("dx");

    public DxSlots(){
    }
    @OnlyIn(Dist.CLIENT)
    public static void init() {
        NBTKeyModel.registerExtraTexture(new ResourceLocation("tconstruct:creative_slot")
                ,DX.getName(),new ResourceLocation("jzyy:item/slot/dx"));
    }
}
