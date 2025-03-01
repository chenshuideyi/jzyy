package com.csdy.diadema.fakekillaura;

import com.csdy.diadema.ranges.SphereDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class FakeKillAuraDiadema extends Diadema {
    static final double RADIUS = 6;
    private final Player player = getPlayer();

    public FakeKillAuraDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    private final SphereDiademaRange range = new SphereDiademaRange(this,RADIUS);

    @Override
    public DiademaRange getRange() {
        return range;
    }

    @Override
    protected void onEntityEnter(Entity entity) {
        if (!(entity instanceof LivingEntity)) return;
        if (!entity.equals(player)) {
            entity.invulnerableTime = 0;
            player.attack(entity);
        }
    }

}
