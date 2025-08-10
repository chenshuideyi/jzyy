package com.csdy.jzyy.cheat;

import javax.swing.*;
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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("是否启用落雪追踪？");
        panel.add(titleLabel);
        JLabel userAgreementLabel = new JLabel("<html><a href=''>《用户守则》</a></html>");
        userAgreementLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        userAgreementLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JTextArea agreementArea = new JTextArea(15, 40);
                agreementArea.setText("1. 本追踪仅用于“匠战妖域”整合包单人或与朋友联机使用，无法用于任何服务器和其他整合包。\n2. 本追踪不提供任何形式的技术支持或保证。");
                agreementArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(agreementArea);
                JOptionPane.showMessageDialog(KeyPassNetworkValidation.this, scrollPane, "用户守则", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panel.add(userAgreementLabel);
        JCheckBox agreeCheckBox = new JCheckBox("我已阅读并同意用户守则");
        panel.add(agreeCheckBox);
        JLabel versionLabel = new JLabel("选择版本：");
        panel.add(versionLabel);
        JRadioButton lightRadio = new JRadioButton("精简版");
//        JRadioButton fullRadio = new JRadioButton("完整版");
        lightRadio.setSelected(true);
        ButtonGroup versionGroup = new ButtonGroup();
        versionGroup.add(lightRadio);
//        versionGroup.add(fullRadio);
        panel.add(lightRadio);
//        panel.add(fullRadio);
        int choice = JOptionPane.showOptionDialog(this, panel, "提示", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");

        if (choice == JOptionPane.OK_OPTION) {
            if (!agreeCheckBox.isSelected()) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "请勾选同意用户守则", "提示", JOptionPane.WARNING_MESSAGE));
                showDownloadDialog(targetFile);
            }
            executor.execute(() -> {
                try {
                    if (lightRadio.isSelected()) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "精简版已启用.。", "提示", JOptionPane.INFORMATION_MESSAGE));
                        UI.main(new String[]{});
                    } else {
                        copyJarToMods(targetFile);
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "完整版文件已复制到mods目录，重启游戏后生效。", "提示", JOptionPane.INFORMATION_MESSAGE));
                    }
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "完整版复制失败，请检查cheat目录和mods目录是否存在！", "错误", JOptionPane.ERROR_MESSAGE));
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
