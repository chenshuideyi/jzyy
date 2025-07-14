package com.csdy.jzyy.diadema.slow;

import com.csdy.tcondiadema.diadema.api.ranges.HalfSphereDiademaRange;
import com.csdy.tcondiadema.frames.diadema.Diadema;
import com.csdy.tcondiadema.frames.diadema.DiademaType;
import com.csdy.tcondiadema.frames.diadema.movement.DiademaMovement;
import com.csdy.tcondiadema.frames.diadema.range.DiademaRange;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SlowDiadema extends Diadema {
    final static double RADIUS = 8;
    private final Entity holder = getCoreEntity();

    private final HalfSphereDiademaRange range = new HalfSphereDiademaRange(this, RADIUS);

    public SlowDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    private static final Map<UUID, Boolean> limitedPlayers = new HashMap<>();

    public static void limitPlayerFPS(Player player, int fpsLimit) {
        if (player.level().isClientSide) {
            limitedPlayers
                    .put(player.getUUID(), true);
            Minecraft.getInstance().options.framerateLimit().set(fpsLimit);
        }
    }

    public static void resetPlayerFPS(Player player) {
        if (player.level().isClientSide) {
            limitedPlayers
                    .remove(player.getUUID());
            Minecraft.getInstance().options.framerateLimit().set(Minecraft.getInstance().options.framerateLimit().get());
        }
    }

    public static boolean isFPSLimited(Player player) {
        return limitedPlayers.containsKey(player.getUUID());
    }

    @Override
    public @NotNull DiademaRange getRange() {
        return range;
    }

    @Override
    protected void perTick() {
        for (Entity entity : affectingEntities) {
            if (entity instanceof Player player && !entity.equals(holder)) {
                limitPlayerFPS(player, 10);
            }
        }
    }

    @Override
    protected void onEntityExit(Entity entity) {
        var core = getCoreEntity();
        if (core == null || entity.equals(core)) return;
        if (entity instanceof Player player) {
            resetPlayerFPS(player);
        }
    }
}
