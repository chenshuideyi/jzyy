package com.csdy.item.sword;

import com.csdy.until.CsdyPlayer;
import com.csdy.until.CsdySeverPlayer;
import com.csdy.until.HelloGl;
import com.csdy.until.List.GodList;
import com.csdy.until.RainbowText;
import com.csdy.until.ReClass.ReFont;
import com.csdy.until.ReClass.SuperForgeGui;
import com.csdyms.Helper;
import com.csdyms.ReThread;
import com.csdyms.core.EntityUntil;
import com.csdyms.core.enums.EntityCategory;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import static com.csdy.ModMain.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CsdySword extends SwordItem {
    Minecraft mc = Minecraft.getInstance();

    public static boolean csdy;

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        EntityUntil.setCategory(entity,EntityCategory.csdykill);
        entity.setRemoved(Entity.RemovalReason.KILLED);
        return super.onLeftClickEntity(stack, player, entity);
    }

    public CsdySword(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", Double.POSITIVE_INFINITY, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "Weapon modifier", Double.POSITIVE_INFINITY, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
        this.attackDamage = Float.POSITIVE_INFINITY;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public int getDamage(ItemStack stack) {
        return 0;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.BOW;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> use = super.use(level, player, hand);//能使用物品的一定是玩家 所以改成player
        if (level instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getEntities().getAll()) {
                if (entity != null && !(entity instanceof Player)) {
                    EntityUntil.setCategory(entity,EntityCategory.csdykill);
                }
            }
        }
        return use;
    }
    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity living, int timeLeft) {
        if (level instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getEntities().getAll()) {
                if (entity != null && !(entity instanceof Player)) {
                    EntityUntil.setCategory(entity,EntityCategory.csdykill);
                }
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack p_41454_) {
        return 72000;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, world, list, flag);
        list.add(Component.literal(RainbowText.setColour("天子屁股剑!")));
        list.add(Component.literal(RainbowText.setColour("她在喷屎!")));
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public @Nullable Font getFont(ItemStack stack, IClientItemExtensions.FontContext context) {
                return ReFont.getFont();
            }
        });
    }

    @Override
    public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemstack, world, entity, slot, selected);
        if (!(entity instanceof Player)) return;
        GodList.SetGodPlayer((Player) entity);
        EntityUntil.setCategory(entity,EntityCategory.csdy);
//        if (entity.isShiftKeyDown() && !csdy) csdy = true;
        if (entity instanceof LocalPlayer) Helper.replaceclass(entity, CsdyPlayer.class);
        if (entity instanceof ServerPlayer serverPlayer) Helper.replaceclass(serverPlayer, CsdySeverPlayer.class);
        if (csdy) {
            mc.gui = new SuperForgeGui(mc);
////            if (world instanceof ServerLevel) ((ServerLevel) world).setDayTime(22000);
            ReThread.Thread((LivingEntity)entity);
        }

    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity living) {
//        mc.gui = new SuperForgeGui(mc);
//        for (int index0 = 0; index0 < 100; ++index0) {
            Level level = living.level();
//            if (level instanceof ServerLevel serverLevel) {
//                double x = living.getX();
//                double y = living.getY();
//                double z = living.getZ();
//                Entity entityToSpawn = ((EntityType<?>) EntityRegister.RAINBOW_LIGHTING.get()).spawn(serverLevel, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
//                if (entityToSpawn != null) {
//                    entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing((double) Math.round(x - 50.0 + Math.random() * (x + 50.0 - (x - 50.0))), y, (double) Math.round(z - 50.0 + Math.random() * (z + 50.0 - (z - 50.0))))));
//                    serverLevel.addFreshEntity(entityToSpawn);
//                }
//
//                Entity entityToSpawn2 = ((EntityType<?>) EntityRegister.RAINBOW_LIGHTING2.get()).spawn(serverLevel, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
//                if (entityToSpawn2 != null) {
//                    entityToSpawn2.moveTo(Vec3.atBottomCenterOf(BlockPos.containing((double) Math.round(x - 50.0 + Math.random() * (x + 50.0 - (x - 50.0))), y, (double) Math.round(z - 50.0 + Math.random() * (z + 50.0 - (z - 50.0))))));
//                    serverLevel.addFreshEntity(entityToSpawn2);
//                }
//            }

//        } return false;

//        // 移除旧的移动速度修饰符（如果存在）
//        player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_ID);
//        // 移除旧的飞行速度修饰符（如果存在）
//        player.getAttribute(Attributes.FLYING_SPEED).removeModifier(FLYING_SPEED_MODIFIER_ID);
//
//        // 添加新的移动速度修饰符
//        AttributeModifier speedModifier = new AttributeModifier(
//                SPEED_MODIFIER_ID, // 唯一标识符
//                "Speed Boost", // 修饰符名称
//                4.0, // 增加的值（基础值为 1.0，所以 4.0 + 1.0 = 5.0）
//                AttributeModifier.Operation.MULTIPLY_TOTAL // 乘法操作
//        );
//
//        // 添加新的飞行速度修饰符
//        AttributeModifier flyingSpeedModifier = new AttributeModifier(
//                FLYING_SPEED_MODIFIER_ID, // 唯一标识符
//                "Flying Speed Boost", // 修饰符名称
//                4.0, // 增加的值（基础值为 1.0，所以 4.0 + 1.0 = 5.0）
//                AttributeModifier.Operation.MULTIPLY_TOTAL // 乘法操作
//        );
//
//        player.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(speedModifier);
//        player.getAttribute(Attributes.FLYING_SPEED).addPermanentModifier(flyingSpeedModifier);

        return false;
    }
}

//        GodList.SetList((Player) entity);
//        GodList.isbelovePlayer(entity);
//        entity.setHealth(15);
//        mc.gui=new SuperForgeGui(mc);
//        csdy = false;
//        return false;

//    static class CsdyThread extends Thread {
//        public void run() {
//            while (Minecraft.getInstance().isRunning()) {
//                try {
//                    synchronized (this) {
//                        this.wait(1);
//                    }
//                    Minecraft mc = Minecraft.getInstance();
//                    mc.gui=new SuperForgeGui(mc);
////                    System.out.println("CSDY");
//                } catch (InterruptedException e){
//                    throw new RuntimeException(e);
//                }
//            }
//
//        }
//    }
//
//    }

