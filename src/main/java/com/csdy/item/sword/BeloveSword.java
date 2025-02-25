package com.csdy.item.sword;

import com.csdy.until.CsdyPlayer;
import com.csdy.until.CsdySeverPlayer;
import com.csdy.until.List.GodList;
import com.csdy.until.ReClass.*;
import com.csdyms.Helper;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static com.csdyms.test.SystemVolume.getCurrentVolume;


public class BeloveSword extends SwordItem {
    Minecraft mc= Minecraft.getInstance();
    public BeloveSword(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", Double.POSITIVE_INFINITY, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "Weapon modifier", Double.POSITIVE_INFINITY, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
        this.attackDamage = Float.POSITIVE_INFINITY;

    }
    @Override
    public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemstack, world, entity, slot, selected);
        GodList.SetGodPlayer((Player) entity);
//        Helper.replaceclass(mc.entityRenderDispatcher, LoveEntityRenderDispatcher.class);
//        Helper.replaceclass(mc.player, CsdyPlayer.class);
//        Helper.replaceclass(Minecraft.instance,FuckMinecraft.class);
//        if (entity instanceof ServerPlayer serverPlayer){
//            Helper.replaceclass(serverPlayer, CsdySeverPlayer.class);
//        }
//        ForgeGui forgeGui = new ForgeGui(Minecraft.getInstance());
//        Helper.replaceclass(forgeGui, SuperForgeGui.class);
//        Helper.replaceclass(mc.gameRenderer, FuckGameRender.class);
    }
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public @Nullable Font getFont(ItemStack stack, IClientItemExtensions.FontContext context) {
                return ReFont.getFont();
            }
        });
    }
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
//        mc.gui=new SuperForgeGui(mc);
        int currentVolume = getCurrentVolume();
        ((Player) entity).displayClientMessage(Component.literal(currentVolume+"%"), false);
        return false;
    }
}
