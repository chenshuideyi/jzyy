package com.csdy.item.sword;

import com.csdy.diadema.warden.SonicBoomUtil;
import com.csdy.until.CsdyPlayer;
import com.csdy.until.CsdySeverPlayer;
import com.csdy.until.List.GodList;
import com.csdy.until.ReClass.SuperForgeGui;
import com.csdyms.Helper;
import com.csdyms.ReThread;
import com.csdyms.core.EntityUntil;
import com.csdyms.core.enums.EntityCategory;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

public class NightOfKnights extends SwordItem {

    public NightOfKnights(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 14, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "Weapon modifier", 14, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();

    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof LivingEntity living) {
            SonicBoomUtil.performSonicBoom(player.level,living,player);
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> use = super.use(level, player, hand);//能使用物品的一定是玩家 所以改成player
        if (level instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getEntities().getAll()) {
                if (entity != null && !(entity instanceof Player)) {
                    SonicBoomUtil.performSonicBoom(player.level, (LivingEntity) entity,player);
                }
            }
        }
        return use;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.csdy.night_of_knights.line1").withStyle(ChatFormatting.DARK_GRAY));
    }


    @Override
    public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemstack, world, entity, slot, selected);

    }
}
