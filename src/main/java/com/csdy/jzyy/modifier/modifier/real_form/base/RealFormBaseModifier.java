package com.csdy.jzyy.modifier.modifier.real_form.base;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

///所有真实形态都继承这个
public abstract class RealFormBaseModifier extends NoLevelsModifier implements InventoryTickModifierHook{

    private final String materialId;
    private final MaterialVariantId reMaterialId;
    private final String text;


    /**
     * @param materialId   基础材料的ID
     * @param reMaterialId 真实形态的ID
     * @param text 转变时的文本
     */
    public RealFormBaseModifier(String materialId, MaterialVariantId reMaterialId,String text) {
        this.materialId = materialId;
        this.reMaterialId = reMaterialId;
        this.text= text;
    }

    /**
     * 是否满足条件
     * @param tool 当前工具
     * @return 是否满足条件
     */
    protected abstract boolean shouldRevealRealForm(ToolStack tool, @Nullable LivingEntity holder);

    /**
     * 转变为真实形态
     */
    public void transformerRealForm(ToolStack tool,@Nullable LivingEntity holder) {
        if (!(holder instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (!shouldRevealRealForm(tool,holder)) return;
        MaterialNBT materials = tool.getMaterials();
        for (int i = 0; i < materials.size(); i++) {
            MaterialVariant variant = materials.get(i);
            if (variant.getVariant().getId().getPath().equals(this.materialId)) {
                tool.replaceMaterial(i, this.reMaterialId);
                ServerLevel level = (ServerLevel) player.level();
                playSound(level,player);
                sendParticles(level,player);

                player.displayClientMessage(Component.translatable(text), true);

            }
        }

    }

    protected void playSound(Level level,LivingEntity holder){
        level.playSound(
                null,
                holder.getX(), holder.getY(), holder.getZ(),
                SoundEvents.TOTEM_USE,
                SoundSource.PLAYERS,
                0.5F,
                1F
        );
    }

    protected void sendParticles(ServerLevel level,LivingEntity holder){
        level.sendParticles(
                ParticleTypes.TOTEM_OF_UNDYING,
                holder.getX(), holder.getY() + 1.0, holder.getZ(),
                100, // 粒子数量
                0.5, 0.5, 0.5,
                0.2
        );
    }


    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        transformerRealForm((ToolStack) tool,holder);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }

}
