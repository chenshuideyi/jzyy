package com.csdy.jzyy.modifier.modifier;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class Truck extends Modifier implements InventoryTickModifierHook, AttributesModifierHook {

    // 存储玩家移动方向数据的Map
    private static final Map<UUID, Direction> playerDirectionMap = new HashMap<>();

    // 配置参数
    private static final int COLLECTION_RADIUS = 5; // 物品收集半径
    private static final int BLOCK_BREAK_RADIUS = 3; // 方块破坏半径
    private static final int ENTITY_PUSH_RADIUS = 4; // 实体推动半径


    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry entry, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            UUID uuid = UUID.nameUUIDFromBytes(("tool_speed_boost_" + slot.getName()).getBytes());
            AttributeModifier modifier = new AttributeModifier(
                    uuid,
                    "track_speed_boost",
                    0.1 * entry.getLevel(),
                    AttributeModifier.Operation.ADDITION
            );
            consumer.accept(Attributes.MAX_HEALTH, modifier);
        }
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide || !(holder instanceof Player player) || !isCorrectSlot) {
            return;
        }

        // 更新玩家移动方向
        updatePlayerDirection(player);

        // 执行卡车效果
        if (player.isSprinting() || player.zza > 0) { // 前进或冲刺时触发
            executeTruckEffects(player, world);
        }
    }

    private void updatePlayerDirection(Player player) {
        UUID playerId = player.getUUID();
        Direction facing = player.getDirection();
        playerDirectionMap.put(playerId, facing);
    }

    private void executeTruckEffects(Player player, Level world) {
        Direction facing = playerDirectionMap.getOrDefault(player.getUUID(), player.getDirection());

        // 1. 收集物品
        collectItems(player, world, facing);

        // 2. 破坏前方方块
        breakBlocks(player, world);

        // 3. 推动实体
        pushEntities(player, world, facing);

        // 4. 创建视觉效果（可选）
        createVisualEffects(player, world, facing);
    }

    private void collectItems(Player player, Level world, Direction facing) {
        BlockPos playerPos = player.blockPosition();
        AABB collectionArea = createAreaInFront(playerPos, facing, COLLECTION_RADIUS);

        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, collectionArea);

        for (ItemEntity item : items) {
            // 将物品传送到玩家位置
            item.setPos(player.getX(), player.getY(), player.getZ());
            item.setPickUpDelay(0); // 立即可拾取
        }
    }

    private void breakBlocks(Player player, Level world) {
        if (!(world instanceof ServerLevel serverWorld)) return;

        BlockPos playerPos = player.blockPosition();
        BlockPos.MutableBlockPos breakPos = new BlockPos.MutableBlockPos();

        // 以玩家为中心创建立方体破坏区域
        for (int x = -BLOCK_BREAK_RADIUS; x <= BLOCK_BREAK_RADIUS; x++) {
            for (int y = -1; y <= 2; y++) { // 从脚下到头顶上方
                for (int z = -BLOCK_BREAK_RADIUS; z <= BLOCK_BREAK_RADIUS; z++) {
                    breakPos.set(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);

                    // 跳过玩家脚下的位置，防止玩家掉下去
                    if (x == 0 && y == -1 && z == 0) {
                        continue;
                    }

                    // 跳过不可破坏的方块
                    BlockState state = world.getBlockState(breakPos);

                    // 破坏方块并掉落物品
                    if (!state.isAir() && state.getDestroySpeed(world, breakPos) >= 0) {
                        world.destroyBlock(breakPos, true, player);
                    }
                }
            }
        }
    }

    private void pushEntities(Player player, Level world, Direction facing) {
        BlockPos playerPos = player.blockPosition();
        AABB pushArea = createAreaInFront(playerPos, facing, ENTITY_PUSH_RADIUS);

        List<Entity> entities = world.getEntitiesOfClass(Entity.class, pushArea);

        for (Entity entity : entities) {
            if (entity == player || !entity.isAlive()) continue;

            // 计算推动向量
            Vec3 pushVec = calculatePushVector(facing, 2.0); // 推动力量

            // 应用推动
            entity.setDeltaMovement(
                    entity.getDeltaMovement().x + pushVec.x,
                    entity.getDeltaMovement().y + 0.3, // 稍微向上
                    entity.getDeltaMovement().z + pushVec.z
            );
            entity.hurtMarked = true; // 强制更新运动

            // 对生物造成轻微伤害
            if (entity instanceof LivingEntity living) {
                living.hurt(player.damageSources().playerAttack(player),10000);
            }
        }
    }

    private void createVisualEffects(Player player, Level world, Direction facing) {
        // 这里可以添加粒子效果、声音等
        // 例如：播放破坏音效、生成灰尘粒子等

        if (world instanceof ServerLevel serverWorld) {
            BlockPos playerPos = player.blockPosition();

            // 在玩家前方生成一些粒子效果
            for (int i = 1; i <= 3; i++) {
                BlockPos effectPos = switch (facing) {
                    case NORTH -> playerPos.offset(0, 0, -i);
                    case SOUTH -> playerPos.offset(0, 0, i);
                    case EAST -> playerPos.offset(i, 0, 0);
                    case WEST -> playerPos.offset(-i, 0, 0);
                    default -> playerPos;
                };

            }
        }
    }

    private AABB createAreaInFront(BlockPos center, Direction facing, int radius) {
        return switch (facing) {
            case NORTH -> new AABB(
                    center.getX() - radius, center.getY() - radius, center.getZ() - radius * 2,
                    center.getX() + radius, center.getY() + radius, center.getZ()
            );
            case SOUTH -> new AABB(
                    center.getX() - radius, center.getY() - radius, center.getZ(),
                    center.getX() + radius, center.getY() + radius, center.getZ() + radius * 2
            );
            case EAST -> new AABB(
                    center.getX(), center.getY() - radius, center.getZ() - radius,
                    center.getX() + radius * 2, center.getY() + radius, center.getZ() + radius
            );
            case WEST -> new AABB(
                    center.getX() - radius * 2, center.getY() - radius, center.getZ() - radius,
                    center.getX(), center.getY() + radius, center.getZ() + radius
            );
            default -> new AABB(center).inflate(radius);
        };
    }

    private Vec3 calculatePushVector(Direction facing, double strength) {
        return switch (facing) {
            case NORTH -> new Vec3(0, 0, -strength);
            case SOUTH -> new Vec3(0, 0, strength);
            case EAST -> new Vec3(strength, 0, 0);
            case WEST -> new Vec3(-strength, 0, 0);
            default -> new Vec3(0, 0, 0);
        };
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
        hookBuilder.addHook(this, ModifierHooks.ATTRIBUTES);
    }
}
