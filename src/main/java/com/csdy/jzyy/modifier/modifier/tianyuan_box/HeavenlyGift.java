package com.csdy.jzyy.modifier.modifier.tianyuan_box;

import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.modifier.register.JzyyModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolPartsHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;
import java.util.Random;

public class HeavenlyGift extends JzyyModifier {

    @Override
    public boolean isNoLevels() {
        return true;
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity entity, int index, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        EvolutionTool((ToolStack) tool);
    }
    public List<MaterialId> getMaterialId(MaterialStatsId statType) {
        IMaterialRegistry registry = MaterialRegistry.getInstance();
        return MaterialRegistry
                .getInstance()
                .getAllMaterials()
                .stream()
                .filter(material -> {
                    MaterialId id = material.getIdentifier();
                    return  !material.isHidden()
                            && registry.getMaterialStats(id, statType).isPresent();
                })
                .map(IMaterial::getIdentifier)
                .toList();
    }
    private final Random RANDOM = new Random();
    public MaterialVariantId getRandomMaterialVariantId(MaterialStatsId statType) {
        if (getMaterialId(statType).isEmpty()) {
            throw new IllegalStateException("No materials available");
        }
        return getMaterialId(statType).get(RANDOM.nextInt(getMaterialId(statType).size()));
    }

    public void EvolutionTool(ToolStack tool) {
        MaterialNBT materials = tool.getMaterials();
        for (int i = 0; i < materials.size(); i++) {
            MaterialVariantId materialVariantId = getRandomMaterialVariantId(ToolPartsHook.parts(tool.getDefinition()).get(i).getStatType());
            MaterialVariant variant = materials.get(i);
            if (variant.getVariant().getId().getPath().equals("tianyuan_box") && variant.getVariant().getId().getNamespace().equals(JzyyModMain.MODID)) {
                tool.replaceMaterial(i, materialVariantId);
            }
        }
    }
}
