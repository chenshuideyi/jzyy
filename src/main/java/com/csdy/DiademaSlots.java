package com.csdy;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.model.NBTKeyModel;
import slimeknights.tconstruct.library.tools.SlotType;

public class DiademaSlots {
    public static SlotType DIADEMA = SlotType.getOrCreate("diadema");

    public DiademaSlots(){
    }

    public static void init() {
        NBTKeyModel.registerExtraTexture(new ResourceLocation("tconstruct:creative_slot")
                ,DIADEMA.getName(),new ResourceLocation("tcondiadema:item/slot/diadema"));
    }

}
