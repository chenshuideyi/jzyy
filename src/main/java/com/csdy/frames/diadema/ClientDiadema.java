package com.csdy.frames.diadema;

import com.csdy.frames.diadema.packets.DiademaUpdatePacket;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/// 客户端的领域实例类型，用于处理显示效果之类的东西
public abstract class ClientDiadema {
    public ClientDiadema() {
        // 实例上的事件处理器需要手动注册
        MinecraftForge.EVENT_BUS.register(this);
    }

    void remove() {
        // 同样实例在删除时候需要手动取消注册。不然因为注册也是个引用，可能导致内存泄漏或者null引用异常
        MinecraftForge.EVENT_BUS.unregister(this);
    }


    // properties
    @Getter private Vec3 position = Vec3.ZERO;
    @Getter private ResourceLocation dimension;


    // virtual methods

    /// 在客户端每帧执行一次的实例方法，把视觉效果之类的放在这里吧！
    /// 警告：dimension可能为null
    protected void perTick() {
    }

    /// 同步更新服务端领域的实例方法，覆写此方法以更新自定义信息
    protected void updating(byte[] byteBuf) {
    }


    // updating
    final void update(DiademaUpdatePacket packet) {
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
