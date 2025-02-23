package com.csdyms.test;

import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public interface EntitySlotAccess {

    ItemStack get();

    void set(ItemStack stack);

    default void modify(Function<ItemStack, ItemStack> func) {
        set(func.apply(get()));
    }

    String getID();

}
