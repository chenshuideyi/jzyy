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
import top.theillusivec4.curios.api.CuriosApi;

public class CsdyWorldDiadema extends Diadema {
    final static double RADIUS = 10;
    private final Entity holder = getCoreEntity();

    public CsdyWorldDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    private final SphereDiademaRange range = new SphereDiademaRange(this, RADIUS);

    @Override
    public @NotNull DiademaRange getRange() {
        return range;
    }

    @Override
    protected void perTick() {
        for (Entity entity : affectingEntities) {
            if (!entity.equals(holder)) {
                if (!(entity instanceof Player player)) continue;
                forceCooldownOnPlayer(player);
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
                pullPlayerToCore(player, core);
                mob.doHurtTarget(player);
            }
        }
    }

    /**
     * 将玩家拉向核心实体
     */
    private void pullPlayerToCore(Player player, Entity core) {
        if (!player.isAlive()) return;

        player.displayClientMessage(
                Component.literal("想逃？").withStyle(ChatFormatting.RED),
                false
        );

        // 1. 计算方向向量（从玩家指向核心）
        Vec3 direction = core.position()
                .subtract(player.position())
                .normalize(); // 单位化向量

        // 2. 设置拉力参数
        double pullStrength = 70; // 拉力强度（可调整）

        Vec3 currentMotion = player.getDeltaMovement();
        Vec3 newMotion = currentMotion.add(
                direction.x * pullStrength,
                direction.y * pullStrength,
                direction.z * pullStrength
        );
        player.setDeltaMovement(newMotion);
        player.hurtMarked = true;

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
