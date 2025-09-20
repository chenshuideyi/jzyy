package com.csdy.jzyy.diadema.meltdown;


import com.csdy.tcondiadema.diadema.api.ranges.SphereDiademaRange;
import com.csdy.tcondiadema.frames.diadema.Diadema;
import com.csdy.tcondiadema.frames.diadema.DiademaType;
import com.csdy.tcondiadema.frames.diadema.movement.DiademaMovement;
import com.csdy.tcondiadema.frames.diadema.range.DiademaRange;


import luckytnt.registry.EffectRegistry;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class LuckyTntMeltdownDiadema extends Diadema {
    final static double RADIUS = 12;
    private final Entity holder = getCoreEntity();
    public LuckyTntMeltdownDiadema(DiademaType type, DiademaMovement movement) {
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
                DamageSources src = new DamageSources(entity.level().registryAccess());
                living.invulnerableTime = 0;
                living.hurt(src.magic(), 100);
                living.addEffect(new MobEffectInstance(EffectRegistry.CONTAMINATED_EFFECT.get(),480000,0));
            }
        }
    }
}
