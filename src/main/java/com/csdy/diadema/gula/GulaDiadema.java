package com.csdy.diadema.gula;

import com.csdy.ModMain;
import com.csdy.diadema.api.ranges.SphereDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GulaDiadema extends Diadema {
    static final double RADIUS = 8;
    private final Player player = getPlayer();

    public GulaDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override protected void removed() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    private final SphereDiademaRange range = new SphereDiademaRange(this,RADIUS);

    @Override
    public @NotNull DiademaRange getRange() {
        return range;
    }

    @SubscribeEvent
    public void gula(LivingDeathEvent e) {
        LivingEntity living = e.getEntity();
        if (this.affectingEntities.contains(living)) {
            AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            double originalMaxHealth = maxHealthAttr.getBaseValue();
            double reducedMaxHealth = originalMaxHealth + living.getMaxHealth()*0.8;
            maxHealthAttr.setBaseValue(reducedMaxHealth);

            AttributeInstance AttackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
            double originalAttack = AttackAttr.getBaseValue();
            double reducedAttack = originalAttack + living.getAttributes().getBaseValue(Attributes.ATTACK_DAMAGE)*0.8;
            AttackAttr.setBaseValue(reducedAttack);

            player.heal(10);
            player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()+5);
            player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel()+5);
        }
    }
}
