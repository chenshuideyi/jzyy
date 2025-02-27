package com.csdy.diadema.warden;

import com.csdy.ModMain;
import com.csdy.diadema.DiademaRegister;
import com.csdy.diadema.ranges.HalfSphereDiademaRange;
import com.csdy.effect.register.EffectRegister;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.events.EntityEnteredDiademaEvent;
import com.csdy.frames.diadema.events.EntityExitedDiademaEvent;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

// 如你所见，这个是领域的服务端类型，带个Client的是客户端类型，一般而言两个都要重写一份。然后拿去注册
@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WardenDiadema extends Diadema {
    Player player = GetPlayer();
    static final double RADIUS = 6;

    public WardenDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);

    }

    private final HalfSphereDiademaRange range = new HalfSphereDiademaRange(this, RADIUS);

    @Override public DiademaRange getRange() {
        return range;
    }

    // 大部分逐帧事件（比如把所有范围内实体丢进虚空）就都可以写在这里，有方法能获取所有影响到的方块和实体
    // 需要针对实体进出的时间点的就监听事件，参考那俩事件处理器。
    @Override protected void perTick() {
        if (player != null) {
            Vec3 playerpos = player.position();
            double range = 6;
            Vec3 start = new Vec3(playerpos.x - range, playerpos.y - range, playerpos.z - range);
            Vec3 finish = new Vec3(playerpos.x + range, playerpos.y + range, playerpos.z + range);
            List<LivingEntity> moblist = GetPlayer().level.getEntitiesOfClass(LivingEntity.class,
                    new AABB(start, finish));
            for (LivingEntity targets : moblist) {
                if (targets != null && targets.position().distanceTo(playerpos) <= 4 && targets != player) {
                    targets.addEffect(new MobEffectInstance(EffectRegister.SCARED.get(), 100, 0));
                }
            }
        }
    }


    // 不知道你打算怎么实现，总之这两个事件监听能监听到实体进出的时候，写在这里就好
//    @SubscribeEvent
//    public static void onEntityEnteredDiadema(EntityEnteredDiademaEvent e) {
//        if (!e.getDiadema().isOfType(DiademaRegister.WARDEN.get())) return; //检测领域类型
//
//        System.out.printf("什么进了%s: %s \n", e.getDiadema(), e.getEntity());
//
//        if (!(e.getEntity() instanceof Player)) return; //仅影响玩家
//
//        // todo: 在这里进行你的影响操作，那行输出想删就删吧
//    }
//
//    @SubscribeEvent
//    public static void onEntityExitedDiadema(EntityExitedDiademaEvent e) {
//        if (!e.getDiadema().isOfType(DiademaRegister.WARDEN.get())) return; //检测领域类型
//        if (DiademaRegister.WARDEN.get().isAffected(e.getEntity())) return; //这个是为了防止玩家从多个重叠的Warden领域中的一嗝离开而被误识别为完全离开
//
//        System.out.printf("什么离开了%s: %s \n", e.getDiadema(), e.getEntity());
//
//        if (!(e.getEntity() instanceof Player)) return; //仅影响玩家
////        SonicBoomUtil.performSonicBoom(e.getEntity().level(), (LivingEntity) e.getEntity(),e.getDiadema().player);
//        // todo: 在这里进行你的取消操作，那行输出快删掉
//    }

    protected void onEntityEnter(Entity entity){
        if ((entity instanceof Player) && !entity.equals(player)) Warden.addWarden((Player) entity);
    }

    protected void onEntityExit(Entity entity){
        if (player != null && !(entity.equals(player)))
            if (entity instanceof LivingEntity living) SonicBoomUtil.performSonicBoom(entity.level, living,GetPlayer());
        if (entity instanceof Player player) Warden.removeWarden(player);
    }
}
