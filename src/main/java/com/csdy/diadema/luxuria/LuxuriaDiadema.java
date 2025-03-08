package com.csdy.diadema.luxuria;

import com.csdy.diadema.api.ranges.SphereDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class LuxuriaDiadema extends Diadema {
    private final Player player = getPlayer();
    private int timer = 0; // 计时器变量

    public LuxuriaDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }

    @Override
    public @NotNull DiademaRange getRange() {
        return new SphereDiademaRange(this, 8);
    }

    @Override
    protected void perTick() {
        for (Entity entity : affectingEntities) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (!entity.equals(player)) {
                living.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.END_ROD));
                living.setHealth(living.getHealth()-living.getMaxHealth()*0.001f);
                if (timer >= 180){
                    ItemEntity itemEntity = new ItemEntity(getLevel(),living.getX(),living.getY(),living.getZ(), Items.MILK_BUCKET.getDefaultInstance());
                    living.level.addFreshEntity(itemEntity);
                    timer = 0;
                }
                else if (timer % 40 == 0){
                    spawnExperienceOrb(living);
                    living.setHealth(living.getHealth()-10);
                }
            }
            timer++;
        }
    }

    private void spawnExperienceOrb(LivingEntity entity) {
        Level level = getLevel();
        ExperienceOrb experienceOrb = new ExperienceOrb(level, entity.getX(), entity.getY(), entity.getZ(), 20);
        level.addFreshEntity(experienceOrb);

    }
}
