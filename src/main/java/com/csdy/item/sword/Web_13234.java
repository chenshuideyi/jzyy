package com.csdy.item.sword;

import com.csdy.diadema.DiademaRegister;
import com.csdy.diadema.warden.SonicBoomUtil;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.movement.FollowDiademaMovement;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Web_13234 extends SwordItem {
    public Web_13234(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 1162261466, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "Weapon modifier", 1162261463, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.csdy.web_13234.name").withStyle(ChatFormatting.YELLOW));
    }


    // 好吧，那就这里是具体使用
    private boolean state0 = true;
    private Diadema testDiadema;

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> use = super.use(level, player, hand);//能使用物品的一定是玩家 所以改成player
        System.out.println("111 玩家使用Web_13234");
        if (level instanceof ServerLevel serverLevel) {
            if (state0) {
                System.out.println("222 并且领域正在添加……");
                testDiadema = DiademaRegister.ABYSS.get().CreateInstance(new FollowDiademaMovement(player));
                System.out.println("222 并且领域添加了");
                state0 = false;
            } else {
                testDiadema.kill();
                System.out.println("222 并且领域移除了");
                state0 = true;
            }
        }
        return use;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof LivingEntity living) {
            SonicBoomUtil.performSonicBoom(player.level,living,player);
        }
        return super.onLeftClickEntity(stack, player, entity);
    }
}
//    @Override
//    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
//        DamageSource Death = (new DamageSource(player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FELL_OUT_OF_WORLD), player));
//        entity.hurt(Death,Float.MAX_VALUE);
//        return super.onLeftClickEntity(stack, player, entity);
//    }

