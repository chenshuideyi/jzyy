package com.csdy.jzyy.entity.boss.ai.mizi;

import com.csdy.jzyy.entity.boss.entity.MiziAo;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MiziDrinkGoal extends Goal{
    private final MiziAo miziAo;

    public MiziDrinkGoal(MiziAo miziAo) {
        this.miziAo = miziAo;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
    }

    /**
     * 此Goal的启动条件：当MiziAo正在喝饮料时。
     */
    @Override
    public boolean canUse() {
        return this.miziAo.isDrinking();
    }

    /**
     * 此Goal启动时可以执行的逻辑（可选）。
     * 我们可以在这里强制停止任何当前的移动。
     */
    @Override
    public void start() {
        super.start();
        this.miziAo.getNavigation().stop(); // 立即停止当前的寻路
    }
}
