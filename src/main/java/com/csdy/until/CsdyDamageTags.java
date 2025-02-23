package com.csdy.until;

import com.csdy.ModMain;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
public class CsdyDamageTags extends TagsProvider<DamageType> {
    public CsdyDamageTags(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, ModMain.MODID, existingFileHelper);
    }
    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        tag(DamageTypeTags.BYPASSES_ARMOR).add(
                //CsdyDamage.BYPASS_EVERYTHING_PLAYER_ATTACK
        );
        tag(DamageTypeTags.BYPASSES_INVULNERABILITY).add(
                //CsdyDamage.BYPASS_EVERYTHING_PLAYER_ATTACK
        );
    }

    @NotNull
    @Override
    public String getName() {
        return "Damage Type Tags";
    }
}
