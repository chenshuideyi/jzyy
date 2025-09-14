package com.csdy.jzyy.cheat;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KeyPassNetworkValidation extends JFrame {

    private static final String JAR_FILE_NAME_FULL = "libLxTrack.jar";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public KeyPassNetworkValidation() {
        checkModsFile();
    }
    private void checkModsFile() {
        File modsDir = new File("mods");
        File targetFile = new File(modsDir, "libLxTrack.jar");
        if (!targetFile.exists()) {
            showDownloadDialog(targetFile);
        }
    }
    private void showDownloadDialog(File targetFile) {
        // 获取当前活动窗口作为父窗口
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
//        if (parentWindow == null) {
//            // 如果没有父窗口，使用应用程序主窗口
//            parentWindow = frame; // 假设frame是你的主窗口引用
//        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("是否启用落雪追踪 - 匠战妖域官方外挂？");
        panel.add(titleLabel);

        JLabel userAgreementLabel = new JLabel("<html><a href=''>《用户守则》</a></html>");
        userAgreementLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        userAgreementLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JTextArea agreementArea = new JTextArea(15, 40);
                agreementArea.setText("1. 本追踪仅用于“匠战妖域”整合包单人或与朋友联机使用，无法用于任何服务器和其他整合包。\n2. 本追踪不提供任何形式的技术支持或保证。\n3. 若遇到飞行抽搐问题，请前往按键设置中删除来自“疯狂觉醒”的飞行按键");
                agreementArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(agreementArea);

                // 用户守则对话框也设置在最上层
                JOptionPane.showMessageDialog(parentWindow, scrollPane, "用户守则",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panel.add(userAgreementLabel);

        JCheckBox agreeCheckBox = new JCheckBox("我已阅读并同意用户守则");
        panel.add(agreeCheckBox);

        JLabel versionLabel = new JLabel("选择版本：");
        panel.add(versionLabel);

        JRadioButton lightRadio = new JRadioButton("精简版");
        lightRadio.setSelected(true);
        ButtonGroup versionGroup = new ButtonGroup();
        versionGroup.add(lightRadio);
        panel.add(lightRadio);

        // 创建自定义的JOptionPane以确保显示在最上层
        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null, new Object[]{"确定", "安全退出"}, "确定");

        // 创建对话框并设置属性
        JDialog dialog = optionPane.createDialog(parentWindow, "提示");
        dialog.setModal(true);
        dialog.setAlwaysOnTop(true); // 关键：设置为始终在最上层
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        // 显示对话框
        dialog.setVisible(true);

        // 获取用户选择
        Object selectedValue = optionPane.getValue();
        int choice = JOptionPane.CLOSED_OPTION;

        if (selectedValue != null) {
            if ("确定".equals(selectedValue)) {
                choice = JOptionPane.OK_OPTION;
            } else if ("安全退出".equals(selectedValue)) {
                choice = JOptionPane.CANCEL_OPTION;
            }
        }

        if (choice == JOptionPane.OK_OPTION) {
            if (!agreeCheckBox.isSelected()) {
                // 警告对话框也设置在最上层
                JOptionPane warningPane = new JOptionPane("请勾选同意用户守则",
                        JOptionPane.WARNING_MESSAGE);
                JDialog warningDialog = warningPane.createDialog(parentWindow, "提示");
                warningDialog.setAlwaysOnTop(true);
                warningDialog.setVisible(true);

                // 递归调用自身
                showDownloadDialog(targetFile);
                return;
            }

            executor.execute(() -> {
                try {
                    if (lightRadio.isSelected()) {
                        // 信息对话框设置在最上层
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane infoPane = new JOptionPane("精简版已启用。",
                                    JOptionPane.INFORMATION_MESSAGE);
                            JDialog infoDialog = infoPane.createDialog(parentWindow, "提示");
                            infoDialog.setAlwaysOnTop(true);
                            infoDialog.setVisible(true);
                        });
                        UI.main(new String[]{});
                    } else {
                        copyJarToMods(targetFile);
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane infoPane = new JOptionPane(
                                    "完整版文件已复制到mods目录，重启游戏后生效。",
                                    JOptionPane.INFORMATION_MESSAGE);
                            JDialog infoDialog = infoPane.createDialog(parentWindow, "提示");
                            infoDialog.setAlwaysOnTop(true);
                            infoDialog.setVisible(true);
                        });
                    }
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane errorPane = new JOptionPane(
                                "完整版复制失败，请检查cheat目录和mods目录是否存在！",
                                JOptionPane.ERROR_MESSAGE);
                        JDialog errorDialog = errorPane.createDialog(parentWindow, "错误");
                        errorDialog.setAlwaysOnTop(true);
                        errorDialog.setVisible(true);
                    });
                }
            });
        }
    }
    private void copyJarToMods(File targetFile) throws IOException {
        File sourceFile = new File("cheat", JAR_FILE_NAME_FULL);
        if (!sourceFile.exists()) {
            throw new FileNotFoundException("未找到cheat目录下的libLxTrack.jar文件");
        }
        File modsDir = targetFile.getParentFile();
        if (!modsDir.exists() && !modsDir.mkdirs()) {
            throw new IOException("无法创建mods目录");
        }
        Files.copy(
                sourceFile.toPath(),
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(KeyPassNetworkValidation::new);
    }
}
