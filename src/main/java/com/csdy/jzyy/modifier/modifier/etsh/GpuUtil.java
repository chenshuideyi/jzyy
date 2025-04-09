package com.csdy.jzyy.modifier.modifier.etsh;

import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.List;

public class GpuUtil {

    static float getTotalVideoMemory() {
        try {
            SystemInfo si = new SystemInfo();
            HardwareAbstractionLayer hal = si.getHardware();
            List<GraphicsCard> graphicsCards = hal.getGraphicsCards();

            if (graphicsCards.isEmpty()) {
                System.out.println("未检测到显卡");
                return 0;
            }

            long totalVRam = 0;

            System.out.println("检测到的显卡信息:");
            for (GraphicsCard card : graphicsCards) {
                System.out.println(" - 名称: " + card.getName());
                System.out.println("   显存: " + card.getVRam() / (1024 * 1024 * 1024) + " GB");
                System.out.println("   设备ID: " + card.getDeviceId());
                totalVRam += card.getVRam();
            }

            float totalGB = (float) (totalVRam / (1024.0 * 1024.0 * 1024.0));
            System.out.println("显存总和: " + totalGB + " GB");

            return totalGB;

        } catch (Exception e) {
            System.err.println("获取显卡信息时出错: " + e.getMessage());
            return 0;
        }
    }

}
