package com.csdy.until;

import com.csdy.until.method.KillPlayerMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.swing.*;

public class CsdyJFrame extends JFrame {
//    Minecraft mc = Minecraft.getInstance();
//    PlayerList list = mc.player.getServer().getPlayerList();
//    ServerPlayer serverPlayer = list.getPlayer(mc.player.getUUID());
    public CsdyJFrame() {
        setTitle("Csdy妙妙小菜单");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 100);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        JButton KillPlayer = new JButton("快速回城");
        panel.add(KillPlayer);
        add(panel);
        KillPlayer.addActionListener(e -> {
//            Util.DeathPlayer(serverPlayer);
        });
    }
    public static void run () {
        CsdyJFrame swingHelper = new CsdyJFrame();
        swingHelper.setVisible(true);
    }
}