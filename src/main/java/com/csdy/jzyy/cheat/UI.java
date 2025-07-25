package com.csdy.jzyy.cheat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;

public class UI {
    private static int baseHue = 0;
    private static Timer colorTimer;
    private static Point initialClick;
    private static int initialX;
    private static int initialY;
    private static JTabbedPane tabbedPane;
    private static JFrame frame;
    public static boolean Fly = false;
    public static boolean Speed = false;
    public static boolean HeadOutAccelerate = false;
    public static boolean NoFallDamage = false;
    private static final Color PRIMARY_COLOR = Color.WHITE;
    private static final Color SECONDARY_COLOR = new Color(0x3498DB);
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BUTTON_COLOR = new Color(0x27AE60);

    public static void main(String[] args) {
        if (KeyPassNetworkValidation.IsLogin) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            Font smoothFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
            UIManager.put("Label.font", smoothFont);
            UIManager.put("CheckBox.font", smoothFont);
            UIManager.put("TextField.font", smoothFont);
            UIManager.put("Button.font", smoothFont);
            UIManager.put("Slider.font", smoothFont);
            UIManager.put("Panel.background", PRIMARY_COLOR);
            UIManager.put("Label.foreground", TEXT_COLOR);
            UIManager.put("CheckBox.foreground", TEXT_COLOR);
            UIManager.put("TextField.background", new Color(0x34495E));
            UIManager.put("TextField.foreground", TEXT_COLOR);
            UIManager.put("Slider.foreground", TEXT_COLOR);
            UIManager.put("Slider.background", PRIMARY_COLOR);
            UIManager.put("Button.background", BUTTON_COLOR);
            UIManager.put("Button.foreground", TEXT_COLOR);

            SwingUtilities.invokeLater(UI::createAndShowGUI);
            colorTimer = new Timer(5, e -> {
                baseHue = (baseHue + 1) % 360;
                if (frame != null) {
                    frame.repaint();
                }
            });
            colorTimer.start();
        }
    }
    private static void createAndShowGUI() {
        frame = new JFrame("LXLOVESZH");
        frame.setUndecorated(true);
        frame.setShape(new RoundRectangle2D.Double(0, 0, 800, 600, 20, 20));
        frame.setResizable(true);
        frame.setAlwaysOnTop(false);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(800, 600);
        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(PRIMARY_COLOR);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.setColor(SECONDARY_COLOR);
                g2d.setStroke(new BasicStroke(3));
                g2d.draw(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
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
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("宋体", Font.PLAIN, 14));
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setBackground(PRIMARY_COLOR);
        JPanel functionPanel = createFunctionPanel();
        functionPanel.setBackground(PRIMARY_COLOR);
        JLabel functionLabel = new JLabel("Function");
        tabbedPane.addTab(null, functionPanel);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(functionPanel), functionLabel);
        JPanel SettingPanel = createSettingPanel();
        SettingPanel.setBackground(PRIMARY_COLOR);
        JLabel SettingLanel = new JLabel("Setting");
        tabbedPane.addTab(null, SettingPanel);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(SettingPanel), SettingLanel);
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static JPanel createFunctionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel functionLabel = new JLabel("功能");
        JCheckBox enableFlight = new JCheckBox("飞行");
        enableFlight.setSelected(Fly);
        enableFlight.addActionListener(e -> Fly = enableFlight.isSelected());
        JCheckBox enableFastSpeed = new JCheckBox("飞速");
        enableFastSpeed.setSelected(Speed);
        enableFastSpeed.addActionListener(e -> Speed = enableFastSpeed.isSelected());
        JCheckBox noFallDamageBox = new JCheckBox("摔落无伤");
        noFallDamageBox.setSelected(NoFallDamage);
        noFallDamageBox.addActionListener(e -> NoFallDamage = noFallDamageBox.isSelected());
        panel.add(functionLabel);
        panel.add(enableFlight);
        panel.add(enableFastSpeed);
        panel.add(noFallDamageBox);
        return panel;
    }

    private static JPanel createSettingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(PRIMARY_COLOR);
        JButton safeExitButton = new JButton("安全退出");
        safeExitButton.setBackground(BUTTON_COLOR);
        safeExitButton.setForeground(TEXT_COLOR);
        safeExitButton.addActionListener(e -> {
            disableAllFunctions();
            if (frame != null) {
                frame.setVisible(false);
            }
        });
        panel.add(safeExitButton);
        return panel;
    }

    private static void disableAllFunctions() {
        Fly = false;
        Speed = false;
        NoFallDamage = false;
    }
}
