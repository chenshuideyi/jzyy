package com.csdy.item.util;

import com.google.common.base.Suppliers;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum ModTiers implements Tier {
    REDSTONE(2, 450, 5.0F, 3.5F, 14, () -> Ingredient.of(Items.REDSTONE_BLOCK)),
    //EMBER_METAL(5, 0, 10.0f, 1.0f, 22, () -> Ingredient.of(ModItems.EMBER_METAL_INGOT));
    LAPIS(2, 250, 7.0F, 3.5F, 24, () -> Ingredient.of(Items.LAPIS_BLOCK)),
    COAL(1, 128, 1.0F, 2.5F, 4, () -> Ingredient.of(Items.COAL)),
    EMERALD(1, 2048, 1.0F, 5.5F, 14, () -> Ingredient.of(Items.EMERALD)),
    PURPUR(1, 512, 1.0F, 5F, 14, () -> Ingredient.of(Items.PURPUR_BLOCK)),
    AMETHYST(9991, 751, 1162261467f, 1162261467f, 9992, () -> Ingredient.of(Items.AMETHYST_SHARD));
    //Lapis Lazuli
//    EMBER_METAL(5, 0, 10.0f, 1.0f, 22, () -> Ingredient.of(Items.));
    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

    ModTiers(
            int level, int uses, float speed, float damage, int enchantmentValue, @NotNull Supplier<Ingredient> supplier
    ) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = Suppliers.memoize(supplier::get);
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
