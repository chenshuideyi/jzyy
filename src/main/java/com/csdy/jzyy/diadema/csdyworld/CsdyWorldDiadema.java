package com.csdy.jzyy.diadema.csdyworld;


import com.csdy.jzyy.entity.boss.entity.SwordManCsdy;
import com.csdy.tcondiadema.diadema.api.ranges.HalfSphereDiademaRange;
import com.csdy.tcondiadema.frames.diadema.Diadema;
import com.csdy.tcondiadema.frames.diadema.DiademaType;
import com.csdy.tcondiadema.frames.diadema.movement.DiademaMovement;
import com.csdy.tcondiadema.frames.diadema.range.DiademaRange;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.csdy.jzyy.modifier.util.CsdyModifierUtil.isFromDummmmmmyMod;
import static com.csdy.jzyy.ms.util.LivingEntityUtil.forceSetAllCandidateHealth;
import static com.csdy.jzyy.ms.util.LivingEntityUtil.setAbsoluteSeveranceHealth;

public class CsdyWorldDiadema extends Diadema {
    final static double RADIUS = 8;
    private final SwordManCsdy holder = (SwordManCsdy) getCoreEntity();

    public CsdyWorldDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    private final HalfSphereDiademaRange range = new HalfSphereDiademaRange(this, RADIUS);

    private static final int MAX_LIVING_ENTITIES_THRESHOLD = 5;

    @Override
    public @NotNull DiademaRange getRange() {
        return range;
    }



    @Override
    protected void perTick() {

        var affectingBlocks = range.getAffectingBlocks(); // 假设 range.getAffectingBlocks() 返回 Collection<BlockPos>

        double coreY = holder.getY() + 10;

        affectingBlocks.forEach(blockPos -> {
            // 检查方块的Y轴是否高于核心的Y轴
            // 注意：blockPos.getY() 是整数，coreY 可能是小数。
            // 如果 coreY 是精确坐标，直接比较即可。
            // 如果你想比较方块格子，确保 coreY 也被当作格子的Y来理解。
            if (blockPos.getY() > coreY) {
                BlockState currentState = getLevel().getBlockState(blockPos);
                Block currentBlock = currentState.getBlock();

                // 如果已经是空气，则不做任何操作
                if (currentBlock == Blocks.AIR) {
                    return; // Stream.forEach 中的 return 行为像 continue
                }


                // 将符合条件的方块设置为空气
                // 使用 setBlockAndUpdate 或更底层的 setBlock 来改变方块
                // setBlockAndUpdate 会处理方块更新和通知
                getLevel().setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);

                getLevel().playSound(null, blockPos, currentState.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.5f, 1f);

            } else {
                // （保留你之前的逻辑）对于Y轴不高于核心的方块，如果不是空气或金块，则变成金块
                // 注意：这个逻辑现在只对 Y <= coreY 的方块生效
                BlockState currentState = getLevel().getBlockState(blockPos);
                Block currentBlock = currentState.getBlock();

                if (currentBlock == Blocks.AIR) {
                    return;
                }

                getLevel().setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);

            }
        });

        if (affectingEntities == null || affectingEntities.isEmpty()) {
            return;
        }

        // 1. 筛选出范围内的所有 LivingEntity (不包括holder自身)
        List<LivingEntity> livingEntitiesInRange = new ArrayList<>();
        List<Player> playersForCooldown = new ArrayList<>();

        for (Entity entity : affectingEntities) {
            if (entity.equals(holder)) continue;

            if (entity instanceof LivingEntity living) {
                ((Mob)holder).doHurtTarget(living);
                livingEntitiesInRange.add(living);
                if (living instanceof Player player) {
                    playersForCooldown.add(player);
                    if (this.holder.isReal()) {
                        setAbsoluteSeveranceHealth(living,0);
                        forceSetAllCandidateHealth(living,0);
                    }
                }
                else {
                    if (!isFromDummmmmmyMod(living)) {
                        float oldHealth = living.getHealth();
                        float reHealth = living.getHealth() - living.getMaxHealth() * 0.01f;
                        setAbsoluteSeveranceHealth(living,reHealth);
                        forceSetAllCandidateHealth(living,reHealth);
                        if (living.getHealth() >= oldHealth || living.getHealth() > reHealth || living.getHealth() <= 0){
//                            setCategory(living,csdykill);
//                            superKillEntity(living);
                        }
                    }
                }
            }
        }

        if (livingEntitiesInRange.size() > MAX_LIVING_ENTITIES_THRESHOLD) {
            for (LivingEntity entityToKill : livingEntitiesInRange) {
                if (entityToKill.isAlive()) {
                    entityToKill.setHealth(0);
                    setAbsoluteSeveranceHealth(entityToKill,0);
                    forceSetAllCandidateHealth(entityToKill,0);
                    ((Mob) holder).heal(750);
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
            if (!core.isAlive() || player.isCreative() || player.isSpectator()) {
                // 核心死亡时允许飞行和建造
                player.getAbilities().mayfly = true;
                player.getAbilities().mayBuild = true;
                player.onUpdateAbilities(); // 同步能力到客户端
            } else {
                // 拉力逻辑
                player.getAbilities().mayfly = true;
                player.getAbilities().mayBuild = true;
                player.onUpdateAbilities(); // 同步能力到客户端
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

//        player.teleportTo(core.getX(),core.getY(),core.getZ());

        core.doHurtTarget(player);

        // 1. 计算方向向量（从玩家指向核心）
        Vec3 direction = core.position()
                .subtract(player.position())
                .normalize(); // 单位化向量

        // 2. 设置拉力参数
        double pullStrength = 15; // 拉力强度（可调整）

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
