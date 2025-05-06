package com.csdy.jzyy.modifier.modifier;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.LeftClickModifierHook;
import com.csdy.jzyy.modifier.register.ModifierRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;


import java.util.List;

import static com.csdy.jzyy.modifier.util.BlockInteractionLogic.forceBreakBlock;

public class Test extends NoLevelsModifier implements ToolStatsModifierHook, MeleeHitModifierHook,InventoryTickModifierHook, TooltipModifierHook, LeftClickModifierHook {

    @Override
    public void addToolStats(IToolContext context, ModifierEntry entry, ModifierStatsBuilder builder) {
        float rate = Float.MAX_VALUE;
        ToolStats.DRAW_SPEED.multiply(builder, 1 * rate);
        ToolStats.MINING_SPEED.multiply(builder, 1 * rate);
        ToolStats.DURABILITY.multiply(builder, 1 * rate);
        ToolStats.ATTACK_SPEED.multiply(builder, 1 * rate);
        ToolStats.ATTACK_DAMAGE.multiply(builder, 1 * rate);
        ToolStats.VELOCITY.multiply(builder, 1 * rate);
        ToolStats.ACCURACY.multiply(builder, 1 * rate);
        ToolStats.PROJECTILE_DAMAGE.multiply(builder, 1 * rate);
        ToolStats.ARMOR.multiply(builder, 1 * rate);
        ToolStats.ARMOR_TOUGHNESS.multiply(builder, 1 * rate);
    }

    @Override
    public void onLeftClickBlock(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {

        forceBreakBlock(player.level,pos.getX(),pos.getY(),pos.getZ(),player);
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOL_STATS);
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
        hookBuilder.addHook(this, ModifierHooks.TOOLTIP);
        hookBuilder.addHook(this, EtSTLibHooks.LEFT_CLICK);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry entry, ToolAttackContext context, float damageDealt) {

    }
//        @Override
//        public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
//            if (!(holder instanceof Player player)) return;
//            if (isCorrectSlot){
//                player.noPhysics = true;
//                player.setNoGravity(true);
//                player.fallDistance = 0;
//                player.getAbilities().mayfly = true;
//                player.getAbilities().flying = true;
//            }
//        }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> list, slimeknights.mantle.client.TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        list.add(Component.literal("( •̀ ω •́ )✧"));
    }



    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifierEntry, Level level, LivingEntity livingEntity, int i, boolean b, boolean b1, ItemStack itemStack) {
        EvolutionTool((ToolStack) tool);
    }

    public static void forceDropBlockLoot(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        List<ItemStack> drops = state.getDrops(new LootParams.Builder((ServerLevel) level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.BLOCK_STATE, state));

        for (ItemStack stack : drops) {
            Block.popResource(level, pos, stack);
        }
    }

//    public static int getLevel(int a){
//        if (a==1){
//            return 1;
//        }
//        else if (a==2){
//            return 2;
//        }
//        else return 0;
//    }
//
//    public static int getPersistentLevel(IToolStackView tool){
//        int a = tool.getPersistentData().getInt(authenticationa);
//        return getLevel(a);
//    }

    public static final MaterialVariantId bedrock = MaterialVariantId.create(new MaterialId("jzyy","bedrock"),"default");


    public static void EvolutionTool(ToolStack tool) {
        MaterialNBT materials = tool.getMaterials();
        ModifierId modifierId = ModifierRegister.TEST_STATIC_MODIFIER.getId();
        for (int i = 0; i < materials.size(); i++) {
            MaterialVariant variant = materials.get(i);
            if (variant.getVariant().getId().getPath().equals("harcadium") && variant.getVariant().getId().getNamespace().equals("jzyy")) {
//                System.out.println("Current modifier level: " + tool.getModifierLevel(modifierId));
                tool.replaceMaterial(i,bedrock);
//                tool.removeModifier(modifierId,1);
            }
        }
    }
}
