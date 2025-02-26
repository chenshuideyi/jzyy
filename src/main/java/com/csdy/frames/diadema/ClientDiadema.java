package com.csdy.frames.diadema;

import com.csdy.frames.diadema.packets.DiademaUpdatePacket;
import lombok.Getter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public abstract class ClientDiadema {
    public ClientDiadema() {

    }

    void remove() {
        //todo: 未实现
    }


    // properties
    @Getter private Vec3 position = Vec3.ZERO;
    @Getter private ResourceKey<Level> dimension;


    // virtual methods

    /// 在客户端每帧执行一次的实例方法，把视觉效果之类的放在这里吧！
    /// 警告：dimension可能为null
    protected void perTick() {
    }

    /// 同步更新服务端领域的实例方法，覆写此方法以更新自定义信息
    protected void updating(byte[] byteBuf) {
    }


    // updating
    void update(DiademaUpdatePacket packet) {
        this.position = packet.position();
        this.dimension = packet.dimension();
        updating(packet.customData());
    }


    // event handlers
    @SubscribeEvent
    public final void onClientTick(TickEvent.ClientTickEvent e) {
        perTick();
    }
}
