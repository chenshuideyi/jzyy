package com.csdy.jzyy.entity.boss.entity;

import com.csdy.jzyy.entity.boss.BossEntity;
import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Web13234 extends BossEntity implements GeoEntity {

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.model.idle");

    private final ServerBossEvent bossEvent;
    public Web13234(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bossEvent = new ServerBossEvent(
                this.getDisplayName(),
                BossEvent.BossBarColor.YELLOW, // 血条颜色
                BossEvent.BossBarOverlay.PROGRESS); // 血条样式
    }

    private final List<ItemStack> stolenItems = new ArrayList<>();
    private boolean isDying = false;

    @Override
    public boolean hurt(DamageSource source, float damage) {
        // 限制伤害为1点
        boolean result = super.hurt(source, 1);

        // 检查伤害来源是否是玩家
        if (source.getEntity() instanceof Player player) {
            stealRandomItem(player);
        }

        return result;
    }

    @Override
    public void checkDespawn() {
        if (this.getHealth() <= 0 && !isDying) {
            isDying = true;
            dropStolenItems();
        }
        super.checkDespawn();
    }

    private void stealRandomItem(Player player) {
        Inventory inventory = player.getInventory();

        // 收集所有非空物品槽位
        List<Integer> nonEmptySlots = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (!inventory.getItem(i).isEmpty()) {
                nonEmptySlots.add(i);
            }
        }

        // 如果有物品可偷
        if (!nonEmptySlots.isEmpty()) {
            Random random = new Random();
            int slotToSteal = nonEmptySlots.get(random.nextInt(nonEmptySlots.size()));
            ItemStack stolenStack = inventory.getItem(slotToSteal).copy();

            // 只偷一个物品
            stolenStack.setCount(1);
            stolenItems.add(stolenStack);

            // 从玩家背包移除
            inventory.getItem(slotToSteal).shrink(1);
            player.displayClientMessage(Component.literal("死后一定还你！"),true);
        }
    }

    private void dropStolenItems() {
        if (!stolenItems.isEmpty()) {
            for (ItemStack stack : stolenItems) {
                // 在生物位置掉落物品
                ItemEntity itemEntity = new ItemEntity(
                        this.level(),
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        stack.copy()
                );
                itemEntity.setDefaultPickUpDelay();
                this.level().addFreshEntity(itemEntity);
            }
            stolenItems.clear();
        }
    }

    @Override
    public void setHealth(float health) {
        float currentHealth = this.getHealth();
        float damage = currentHealth - health;

        if (damage > 1.0f && health > 0) {
            super.setHealth(currentHealth - 1.0f);
        } else {
            super.setHealth(health);
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        this.bossEvent.addPlayer(pPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossEvent.removePlayer(pPlayer);
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        this.bossEvent.removeAllPlayers();
    }

    @Override
    public SoundEvent getBossMusic() { return JzyySoundsRegister.GOM.get(); }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main_controller", 0, this::predicate));
    }

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    private PlayState predicate(AnimationState<Web13234> state) {
        return state.setAndContinue(IDLE_ANIM);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.MAX_HEALTH, 13234)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 0)
                .add(Attributes.ATTACK_SPEED, 1.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

}
