package com.csdy.diadema.fakeprojecte;

import com.csdy.diadema.api.ranges.SphereDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class FakeProjectEDiadema extends Diadema {
    static final double RADIUS = 6;
    private final Player player = getPlayer();

    public FakeProjectEDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    private final SphereDiademaRange range = new SphereDiademaRange(this,RADIUS);

    @Override
    public @NotNull DiademaRange getRange() {
        return range;
    }

    @Override protected void perTick() {
        for (Entity entity : affectingEntities) {
            if (!(entity instanceof LivingEntity)) continue;
            if (!entity.equals(player)){
                entity.invulnerableTime = 0;
                entity.hurt(new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK), player), 1);
            }
        }
    }
}
