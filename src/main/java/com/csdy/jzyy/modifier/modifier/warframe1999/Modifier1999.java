package com.csdy.jzyy.modifier.modifier.warframe1999;

import com.csdy.jzyy.ModMain;
import com.csdy.jzyy.modifier.modifier.real_form.base.RealFormBaseModifier;
import com.csdy.jzyy.modifier.register.ModifierRegister;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierTraitHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class Modifier1999 extends RealFormBaseModifier{

    public Modifier1999(String materialId, MaterialVariantId reMaterialId , String text) {
        super(materialId, reMaterialId,text);
    }

    @Override
    protected boolean shouldRevealRealForm(ToolStack tool, @Nullable LivingEntity holder) {
        return true;
    }

    @Override
    protected void playSound(Level level, LivingEntity holder){
        level.playSound(
                null,
                holder.getX(), holder.getY(), holder.getZ(),
                SoundEvents.WITHER_DEATH,
                SoundSource.PLAYERS,
                0.5F,
                1F
        );
    }

    @Override
    protected void sendParticles(ServerLevel level, LivingEntity holder){

    }

    Random random = new Random();
    ModifierId[] array = {
            ModifierRegister.EAT_STONE_STATIC_MODIFIER.getId(),
            ModifierRegister.ABSOLUTE_SEVERANCE_STATIC_MODIFIER.getId(),
            ModifierRegister.BLADE_RELEASE2_STATIC_MODIFIER.getId(),
            ModifierRegister.SCARY_SKY_STATIC_MODIFIER.getId(),
            ModifierRegister.BEDROCK_BREAK.getId(),
            ModifierRegister.CONVERGENT_WORLDLINE_STATIC_MODIFIER.getId()
    };



    @Override
    public void transformerRealForm(ToolStack tool,@javax.annotation.Nullable LivingEntity holder) {
        int randomIndex = random.nextInt(array.length);
        tool.addModifier(array[randomIndex], 1);
        super.transformerRealForm(tool,holder);

    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        super.registerHooks(hookBuilder);
    }


}
