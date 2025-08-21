package com.csdy.jzyy.modifier.modifier.ying_yang;

import com.csdy.jzyy.modifier.register.ModifierRegister;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

public class Yin extends NoLevelsModifier implements InventoryTickModifierHook {

    private static final ModifierId[] POSSIBLE_MODIFIERS = {
            ModifierRegister.RECEPTIVE_AS_A_HOLLOW_VALLEY_STATIC_MODIFIER.getId(),
            ModifierRegister.HE_WHO_CONQUERS_HIMSELF_IS_STRONG_STATIC_MODIFIER.getId(),
            ModifierRegister.APPARENT_SEAL_UP_STATIC_MODIFIER.getId(),
            ModifierRegister.TO_CYCLE_WITHOUT_CEASE_STATIC_MODIFIER.getId(),
            ModifierRegister.CLEANSING_THE_DARK_MIRROR_OF_THE_HEART_STATIC_MODIFIER.getId(),
            ModifierRegister.TO_EMBRACE_THE_ONE_AND_GUARD_THE_ORIGIN_STATIC_MODIFIER.getId(),
    };

    private final String yinMaterialId = "yin";
    private final String yangMaterialId = "yang";

    public void transformerRealForm(ToolStack tool, @Nullable LivingEntity holder) {
        if (!(holder instanceof Player player)) return;
        if (player.level().isClientSide) return;

        // 检查工具上是否没有yin和yang材料
        boolean hasYinOrYang = false;
        MaterialNBT materials = tool.getMaterials();

        for (int i = 0; i < materials.size(); i++) {
            MaterialVariant variant = materials.get(i);
            String materialPath = variant.getVariant().getId().getPath();
            if (materialPath.equals(yinMaterialId) || materialPath.equals(yangMaterialId)) {
                hasYinOrYang = true;
                break; // 只要找到一个就跳出循环
            }
        }

        // 只有在没有yin和yang材料时才触发
        if (!hasYinOrYang) {
            boolean addedAny = false;

            // 一次性添加所有缺失的modifier
            for (ModifierId modifier : POSSIBLE_MODIFIERS) {
                if (tool.getModifier(modifier) == ModifierEntry.EMPTY) {
                    tool.addModifier(modifier, 1);
                    addedAny = true;
                }
            }

            // 无论是否添加了modifier，都移除自身这个modifier
            tool.removeModifier(getId(), 1);
            tool.removeModifier(ModifierRegister.YANG_STATIC_MODIFIER.getId(), 1);
        }
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
