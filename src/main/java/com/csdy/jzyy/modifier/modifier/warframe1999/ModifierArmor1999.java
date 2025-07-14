package com.csdy.jzyy.modifier.modifier.warframe1999;

import com.csdy.jzyy.modifier.modifier.real_form.base.RealFormBaseModifier;
import com.csdy.jzyy.modifier.register.ModifierRegister;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ModifierArmor1999 extends RealFormBaseModifier{

    public ModifierArmor1999(String materialId, MaterialVariantId reMaterialId , String text) {
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

    private static final Random random = new Random();
    private static final ModifierId[] POSSIBLE_MODIFIERS = {
            ModifierRegister.MAG_STATIC_MODIFIER.getId(),
            ModifierRegister.NYX_STATIC_MODIFIER.getId(),
            ModifierRegister.TRINITY_STATIC_MODIFIER.getId(),
    };



    @Override
    public void transformerRealForm(ToolStack tool, @Nullable LivingEntity holder) {
        for (int i = 0; i < tool.getModifiers().getLevel(ModifierRegister.MODIFIER_ARMOR_1999_STATIC_MODIFIER.getId()); i++) {
            List<ModifierId> availableModifiers = new ArrayList<>();
            for (ModifierId modifier : POSSIBLE_MODIFIERS) {
                if (tool.getModifier(modifier) == ModifierEntry.EMPTY) {
                    availableModifiers.add(modifier);
                }
            }

            if (!availableModifiers.isEmpty()) {
                int randomIndex = random.nextInt(availableModifiers.size());
                ModifierId selectedModifier = availableModifiers.get(randomIndex);
                tool.addModifier(selectedModifier, 1);
            } else {
                break;
            }
        }

        super.transformerRealForm(tool, holder);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        super.registerHooks(hookBuilder);
    }


}
