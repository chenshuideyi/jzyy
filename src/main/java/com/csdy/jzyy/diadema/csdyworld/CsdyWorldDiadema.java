package com.csdy.jzyy.diadema.csdyworld;


import com.csdy.tcondiadema.diadema.api.ranges.SphereDiademaRange;
import com.csdy.tcondiadema.diadema.warden.SonicBoomUtil;
import com.csdy.tcondiadema.diadema.warden.WardenBlindnessEffect;
import com.csdy.tcondiadema.effect.register.EffectRegister;
import com.csdy.tcondiadema.frames.diadema.Diadema;
import com.csdy.tcondiadema.frames.diadema.DiademaType;
import com.csdy.tcondiadema.frames.diadema.movement.DiademaMovement;
import com.csdy.tcondiadema.frames.diadema.range.DiademaRange;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

public class CsdyWorldDiadema extends Diadema {
    final static double RADIUS = 10;
    private final Entity holder = getCoreEntity();

    public CsdyWorldDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    private final SphereDiademaRange range = new SphereDiademaRange(this, RADIUS);

    private static final int MAX_LIVING_ENTITIES_THRESHOLD = 5;

    @Override
    public @NotNull DiademaRange getRange() {
        return range;
    }

    @Override
    protected void perTick() {
        if (affectingEntities == null || affectingEntities.isEmpty()) {
            return;
        }

        // 1. 筛选出范围内的所有 LivingEntity (不包括holder自身)
        List<LivingEntity> livingEntitiesInRange = new ArrayList<>();
        List<Player> playersForCooldown = new ArrayList<>(); // 单独列表用于需要冷却的玩家

        for (Entity entity : affectingEntities) {
            if (entity.equals(holder)) continue; // 跳过holder自身

            if (entity instanceof LivingEntity living) {
                livingEntitiesInRange.add(living);
                if (living instanceof Player player) {
                    playersForCooldown.add(player); // 如果是玩家，也加入到待冷却列表
                }
            }
        }

        // 2. 检查 LivingEntity 数量是否超过阈值
        if (livingEntitiesInRange.size() > MAX_LIVING_ENTITIES_THRESHOLD) {
            // 触发即死逻辑，对所有范围内的 LivingEntity
            for (LivingEntity entityToKill : livingEntitiesInRange) {
                if (entityToKill.isAlive()) {
                    entityToKill.setHealth(0);
                }
            }
        } else {
            // 未触发即死，对范围内的每个玩家执行冷却逻辑
            for (Player player : playersForCooldown) {
                if (player.isAlive()) { // 确保玩家仍然存活
                    forceCooldownOnPlayer(player);
                }
            }
        }
    }

    @Override
    protected void onEntityEnter(Entity entity) {
        var core = getCoreEntity();
        if (core == null || entity.equals(core)) return;
        if (entity instanceof Player player) {
            player.abilities.flying = false;
            player.abilities.mayfly = false;
            player.abilities.mayBuild = false;
            player.onUpdateAbilities(); // 同步能力到客户端
        }
    }

    @Override
    protected void onEntityExit(Entity entity) {
        var core = getCoreEntity();
        if (core == null || entity.equals(core)) return;
        if (!(core instanceof Mob mob)) return;

        if (entity instanceof Player player) {
            if (!core.isAlive()) {
                // 核心死亡时允许飞行和建造
                player.getAbilities().mayfly = true;
                player.getAbilities().mayBuild = true;
                player.onUpdateAbilities(); // 同步能力到客户端
            } else {
                // 拉力逻辑
                pullPlayerToCore(player, mob);

            }
        }
    }

    /**
     * 将玩家拉向核心实体
     */
    private void pullPlayerToCore(Player player, Mob core) {
        if (!player.isAlive()) return;

        player.displayClientMessage(
                Component.literal("想逃？").withStyle(ChatFormatting.RED),
                false
        );

        core.setTarget(player);

        player.teleportTo(core.getX(),core.getY(),core.getZ());


//        // 1. 计算方向向量（从玩家指向核心）
//        Vec3 direction = core.position()
//                .subtract(player.position())
//                .normalize(); // 单位化向量
//
//        // 2. 设置拉力参数
//        double pullStrength = 70; // 拉力强度（可调整）
//
//        Vec3 currentMotion = player.getDeltaMovement();
//        Vec3 newMotion = currentMotion.add(
//                direction.x * pullStrength,
//                direction.y * pullStrength,
//                direction.z * pullStrength
//        );
//        player.setDeltaMovement(newMotion);
//        player.hurtMarked = true;

        player.level().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, // 使用末影人传送音效
                SoundSource.HOSTILE,
                0.5F, 1.0F
        );

    }

    private void forceCooldownOnPlayer(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                player.getCooldowns().addCooldown(stack.getItem(), 60);
            }
        }
    }

}
