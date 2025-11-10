package com.csdy.jzyy.cheat;

import com.csdy.jzyy.ms.util.SoundPlayer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;

public class UI {
    private static Point initialClick;
    private static int initialX;
    private static int initialY;
    private static JFrame frame;

    private static final Color COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = Color.BLACK;
   private static final Color BUTTON_COLOR = new Color(0x27AE60);

   public static void main(String[] args) {
       try {
           UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
       } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                UnsupportedLookAndFeelException e) {
           e.printStackTrace();
       }

       SwingUtilities.invokeLater(UI::createAndShowGUI);
   }

   private static void createAndShowGUI() {
        frame = new JFrame("LXiSZH - 匠战妖域官方外挂");
        frame.setUndecorated(true);
        frame.setShape(new RoundRectangle2D.Double(0, 0, 800, 600, 20, 20));
        frame.setResizable(true);
        frame.setAlwaysOnTop(false);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(0, 0);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (screenSize.width - 800) / 2;
        int centerY = (screenSize.height - 600) / 2;
        frame.setLocation(centerX + 400, centerY + 300);

        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(COLOR);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.dispose();
            }
        };
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.setContentPane(contentPanel);

        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                initialX = frame.getX();
                initialY = frame.getY();
            }
        });

        frame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = frame.getX();
                int thisY = frame.getY();

                int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
                int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

                frame.setLocation(initialX + xMoved, initialY + yMoved);
            }
        });

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setBackground(COLOR);

        JPanel function1Panel = createFunction1Panel();
        function1Panel.setBackground(COLOR);
        JLabel function1Label = new JLabel("<html>" + ("Function1") + "</html>");
        tabbedPane.addTab(null, function1Panel);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(function1Panel), function1Label);


        JPanel SettingPanel = createSettingPanel();
        SettingPanel.setBackground(COLOR);
        JLabel SettingLanel = new JLabel("<html>" + ("Setting") + "</html>");
        tabbedPane.addTab(null, SettingPanel);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(SettingPanel), SettingLanel);

        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        frame.setVisible(true);

        Timer expandTimer = new Timer(10, e -> {
            int currentWidth = frame.getWidth();
            int currentHeight = frame.getHeight();
            int newWidth = currentWidth + 20;
            int newHeight = currentHeight + 15;

            if (newWidth >= 800 && newHeight >= 600) {
                frame.setSize(800, 600);
                frame.setLocation(centerX, centerY);
                ((Timer) e.getSource()).stop();
            } else {
                frame.setSize(newWidth, newHeight);
                frame.setLocation(centerX + (800 - newWidth) / 2, centerY + (600 - newHeight) / 2);
            }
            frame.repaint();
        });
        expandTimer.start();
    }



    private static JPanel createFunction1Panel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel functionLabel = new JLabel();
        functionLabel.setText("<html>" + ("功能") + "</html>");

        JCheckBox enableFlight = new JCheckBox();
        enableFlight.setText("<html>" + ("飞行") + "</html>");
        enableFlight.setSelected(FieldList.Fly);
        enableFlight.addActionListener(e -> FieldList.Fly = enableFlight.isSelected());

        JCheckBox enableFastSpeed = new JCheckBox();
        enableFastSpeed.setText("<html>" + ("飞速") + "</html>");
        enableFastSpeed.setSelected(FieldList.Speed);
        enableFastSpeed.addActionListener(e -> FieldList.Speed = enableFastSpeed.isSelected());
        JCheckBox ZF = new JCheckBox();
        ZF.setText("<html>" + ("自改高跳") + "</html>");
        ZF.setSelected(false);
        ZF.setEnabled(false);
        ZF.addActionListener(e -> {
            FieldList.ZFly = false;
            ZF.setSelected(false);
            JOptionPane.showMessageDialog(null, "<html>" + ("自改高跳功能已禁用") + "</html>");
        });
    //    JCheckBox ZF = new JCheckBox();
   //     ZF.setText("<html>" + ("自改高跳") + "</html>");
   //     ZF.setSelected(FieldList.ZFly);
   //     ZF.addActionListener(e -> {
    //        FieldList.ZFly = ZF.isSelected();
    //        if (FieldList.ZFly) {
    //            String input = JOptionPane.showInputDialog("<html>" + ("高跳高度") + "</html>");
    //            if (input != null) {
    //                try {
    //                    double height = Double.parseDouble(input);
    //                    FieldList.ZFlyHeight = height;
    //                    JOptionPane.showMessageDialog(null, "<html>" + ("高跳高度已设置为: " + height) + "</html>");
    //                } catch (NumberFormatException ex) {
    //                    JOptionPane.showMessageDialog(null, "<html>" + ("输入无效，请输入有效的数字。") + "</html>", "<html>" + ("错误") + "</html>", JOptionPane.ERROR_MESSAGE);
    //                    FieldList.ZFly = false;
    //                    ZF.setSelected(false);
    //                }
    //            } else {
    //                FieldList.ZFly = false;
    //                ZF.setSelected(false);
    //            }
    //        }
    //    });

        JCheckBox jumpFar = new JCheckBox();
        jumpFar.setText("<html>" + ("跳远") + "</html>");
        jumpFar.setSelected(FieldList.JumpFar);
        jumpFar.addActionListener(e -> FieldList.JumpFar = jumpFar.isSelected());

        JCheckBox headOutAccelerateBox = new JCheckBox();
        headOutAccelerateBox.setText("<html>" + ("挥拳加速") + "</html>");
        headOutAccelerateBox.setSelected(FieldList.HeadOutAccelerate);
        headOutAccelerateBox.addActionListener(e -> FieldList.HeadOutAccelerate = headOutAccelerateBox.isSelected());

        JCheckBox noFallDamageBox = new JCheckBox();
        noFallDamageBox.setText("<html>" + ("摔落无伤") + "</html>");
        noFallDamageBox.setSelected(FieldList.NoFallDamage);
        noFallDamageBox.addActionListener(e -> FieldList.NoFallDamage = noFallDamageBox.isSelected());

        // 添加播放音乐按钮
        // 添加随机播放音乐按钮
        JButton randomMusicButton = new JButton();
        randomMusicButton.setText("<html>" + ("随机播放") + "</html>");
        randomMusicButton.addActionListener(e -> SoundPlayer.tryPlayRandom());

        // 添加停止播放按钮
        JButton stopMusicButton = new JButton();
        stopMusicButton.setText("<html>" + ("停止播放") + "</html>");
        stopMusicButton.addActionListener(e -> SoundPlayer.stopPlay());

        panel.add(functionLabel);
        panel.add(enableFlight);
        panel.add(enableFastSpeed);
        panel.add(ZF);
        panel.add(headOutAccelerateBox);
        panel.add(noFallDamageBox);
        panel.add(jumpFar);
        panel.add(randomMusicButton);
        panel.add(stopMusicButton);

        return panel;
    }


    private static JPanel createSettingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(COLOR);
        JButton safeExitButton = new JButton("<html>" +  ("安全退出") + "</html>");
        safeExitButton.setBackground(BUTTON_COLOR);
        safeExitButton.setForeground(TEXT_COLOR);
        // 为按钮添加点击事件监听器
        safeExitButton.addActionListener(e -> {
            // 关闭所有功能
            disableAllFunctions();
            // 隐藏窗口
            if (frame != null) {
                frame.setVisible(false);
            }
        });
        panel.add(safeExitButton);
        return panel;
    }

    private static void disableAllFunctions() {
        FieldList.Fly = false;
        FieldList.Speed = false;
        FieldList.ZFly = false;
        FieldList.JumpFar = false;
        FieldList.HeadOutAccelerate = false;
        FieldList.NoFallDamage = false;
    }
}
