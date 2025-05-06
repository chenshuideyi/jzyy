package com.csdy.jzyy.modifier.modifier.dx.tool;

import com.c2h6s.etstlib.register.EtSTLibHooks;
import com.c2h6s.etstlib.tool.hooks.LeftClickModifierHook;
import com.csdy.jzyy.modifier.register.ModifierRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.RequirementsModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

public class AwakenTiamat extends NoLevelsModifier implements LeftClickModifierHook, RequirementsModifierHook, ValidateModifierHook {

    @Override
    public void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {

        playSweepAnimation(player);
    }

    @Override
    public void onLeftClickBlock(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {

        playSweepAnimation(player);
    }

    @Override
    public @org.jetbrains.annotations.Nullable Component validate(IToolStackView tool, ModifierEntry entry) {
        if (tool.getModifierLevel(ModifierRegister.TIAMAT.getId()) > 0) return null;
        return requirementsError(entry);
    }

    @Nullable
    @Override
    public Component requirementsError(ModifierEntry entry) {
        return Component.translatable("modifier.jzyy.awaken_tiamat.requirements") ;
    }

    private void playSweepAnimation(Player player) {
        Level level = player.level();

        // 客户端只处理视觉效果
        if (level.isClientSide) {
            Minecraft mc = Minecraft.getInstance();
            mc.gameRenderer.itemInHandRenderer.itemUsed(player.getUsedItemHand());

            Vec3 look = player.getLookAngle().normalize();
            Vec3 up = new Vec3(0, 1, 0);
            Vec3 right = look.cross(up).normalize();

            double attackRange = 4.0;
            int particleCount = 32;
            double startAngle = -90.0;
            double endAngle = 90.0;

            for (int i = 0; i < particleCount; i++) {
                double angle = Math.toRadians(startAngle + (endAngle - startAngle) * (i / (double)(particleCount - 1)));
                Vec3 dir = new Vec3(
                        look.x * Math.cos(angle) - right.x * Math.sin(angle),
                        0,
                        look.z * Math.cos(angle) - right.z * Math.sin(angle)
                ).normalize();

                Vec3 pos = player.position()
                        .add(0, player.getEyeHeight() * 0.7, 0)
                        .add(dir.scale(attackRange));

                level.addParticle(ParticleTypes.SWEEP_ATTACK,
                        pos.x, pos.y, pos.z,
                        dir.x * 0.4, dir.y * 0.4 + 0.1, dir.z * 0.4);
            }

            player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP,
                    1.2F, 0.9F + player.getRandom().nextFloat() * 0.3F);
        }
        // 服务端处理伤害逻辑
        else {
            // 1. 定义扇形攻击参数
            float radius = 4.0f;
            float angleRange = 180.0f;

            // 2. 获取玩家前方扇形区域
            Vec3 playerPos = player.position().add(0, player.getEyeHeight() * 0.7, 0);
            Vec3 lookDir = player.getLookAngle().normalize();

            // 3. 检测范围内所有生物
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(radius))) {

                if (entity == player) continue; // 排除玩家自己

                Vec3 entityPos = entity.position().add(0, entity.getBbHeight()/2, 0);
                Vec3 toEntity = entityPos.subtract(playerPos);

                // 4. 检查距离和角度
                float distance = (float) toEntity.length();
                if (distance <= radius) {
                    toEntity = toEntity.normalize();
                    float angle = (float) Math.toDegrees(Math.acos(lookDir.dot(toEntity)));

                    if (angle <= angleRange/2) {
                        // 5. 应用伤害
                        player.attack(entity);
                    }
                }
            }
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, EtSTLibHooks.LEFT_CLICK);
        hookBuilder.addHook(this, ModifierHooks.REQUIREMENTS);
        hookBuilder.addHook(this, ModifierHooks.VALIDATE);
    }


}
