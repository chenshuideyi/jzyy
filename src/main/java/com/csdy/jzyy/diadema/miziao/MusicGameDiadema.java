package com.csdy.jzyy.diadema.miziao;

import com.csdy.jzyy.network.JzyySyncing;
import com.csdy.jzyy.network.packets.PlaySoundPacket;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import com.csdy.tcondiadema.diadema.api.ranges.SphereDiademaRange;
import com.csdy.tcondiadema.frames.diadema.Diadema;
import com.csdy.tcondiadema.frames.diadema.DiademaType;
import com.csdy.tcondiadema.frames.diadema.movement.DiademaMovement;
import com.csdy.tcondiadema.frames.diadema.range.DiademaRange;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;
import java.util.List;

public class MusicGameDiadema extends Diadema {

    private static final double RADIUS = 16.0; // 效果范围
    private static final int SOUND_INTERVAL = 200; // 200 ticks = 10秒
    private static final int DAMAGE_DELAY = 25; // 25 ticks = 1.25秒

    private int soundTickCounter = 0;
    private int damageTickCounter = -1; // -1 表示未激活


    private final Entity holder = getCoreEntity();

    public MusicGameDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }
    private final SphereDiademaRange range = new SphereDiademaRange(this, RADIUS);

    @Override
    public @NonNull DiademaRange getRange() {
        return range;
    }


    @Override
    protected void perTick() {
        // 确保只在服务器端运行
        if (getLevel().isClientSide) {
            return;
        }

        Entity holder = getCoreEntity();
        if (holder == null) {
            return; // 如果核心实体不存在，直接返回
        }

        // 计时器，用于播放音效
        soundTickCounter++;
        if (soundTickCounter >= SOUND_INTERVAL) {
            soundTickCounter = 0;

            // 注意：这里我们传递的是一个包含了所有潜在目标的列表
            playSoundToEntities(affectingEntities);

            // 激活伤害延迟计时器
            damageTickCounter = 0;
        }

        // 伤害延迟计时器
        if (damageTickCounter != -1) {
            damageTickCounter++;
            if (damageTickCounter >= DAMAGE_DELAY) {
                damageTickCounter = -1; // 重置计时器

                // 3. 将同一个列表传递给伤害和效果施加方法
                applyDamageAndSlowness();
            }
        }
    }





    private void applyDamageAndSlowness() {
        Entity holder = getCoreEntity();
        if (holder == null) return;

        // 获取范围内的所有LivingEntity
        for (Entity entity : affectingEntities) {
            if (entity.equals(holder)) continue;
            if (!(entity instanceof LivingEntity living)) continue;

            if (isEntityOnGround(living)) {
                living.kill();
            }

        }
    }

    private boolean isEntityOnGround(LivingEntity entity) {
        BlockPos belowPos = entity.blockPosition().below();
        BlockState blockStateBelow = getLevel().getBlockState(belowPos);
        // 检查脚下的方块是否是一个完整的方块并且不是空气
        return blockStateBelow.isSolidRender(getLevel(), belowPos) && !blockStateBelow.is(Blocks.AIR);
    }


    private void playSoundToEntities(Collection<? extends Entity> entitiesToNotify) {
        Entity holder = getCoreEntity();
        if (holder == null || holder.level().isClientSide()) {
            return;
        }

        for (Entity entity : entitiesToNotify) {
            if (entity instanceof ServerPlayer serverPlayer) {
                JzyySyncing.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new PlaySoundPacket(
                                serverPlayer.getEyePosition(),
                                JzyySoundsRegister.DISCONNECTED.get().getLocation(),
                                2.0f,
                                1.0f
                        )
                );
            }
        }
    }
}
