package com.csdy.jzyy.diadema.meltdown;


import com.csdy.tcondiadema.diadema.api.ranges.SphereDiademaRange;
import com.csdy.tcondiadema.frames.diadema.Diadema;
import com.csdy.tcondiadema.frames.diadema.DiademaType;
import com.csdy.tcondiadema.frames.diadema.movement.DiademaMovement;
import com.csdy.tcondiadema.frames.diadema.range.DiademaRange;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class AlexMeltdownDiadema extends Diadema {
    final static double RADIUS = 12;
    private final Entity holder = getCoreEntity();
    public AlexMeltdownDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    private final SphereDiademaRange range = new SphereDiademaRange(this, RADIUS);

    @Override public @NotNull DiademaRange getRange() {
        return range;
    }

    @Override protected void perTick() {
        for (Entity entity : affectingEntities) {
            if (!entity.equals(holder)) {
                if (!(entity instanceof LivingEntity living)) continue;
                DamageSource src = ACDamageTypes.causeRadiationDamage(living.level().registryAccess());
                living.invulnerableTime = 0;
                living.hurt(src, 50);
                living.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(),480000,0));
            }
        }
    }
}
