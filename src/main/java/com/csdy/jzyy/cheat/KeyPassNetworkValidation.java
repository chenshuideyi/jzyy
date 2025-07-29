package com.csdy.jzyy.cheat;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KeyPassNetworkValidation extends JFrame {

    private static final String UPDATE_URL = "https://d.feiliupan.com/t/97474712677912576/libLxTrack.jar";
    private static final String JAR_FILE_NAME = "libLxTrack.jar";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public KeyPassNetworkValidation() {
        checkModsFile();
    }
    private void checkModsFile() {
        File modsDir = new File("mods");
        File targetFile = new File(modsDir, JAR_FILE_NAME);
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
                agreementArea.setText("1. 本追踪仅用于单人使用，不得用于任何服务器。\n" +
                        "2. 用户需自行承担因使用本追踪而产生的所有法律责任。\n" +
                        "3. 本追踪不提供任何形式的技术支持或保证。\n" +
                        "4.本追踪会上传您的游戏ID至服务器用于统计使用人数，如果您不希望上传您的游戏ID，请取消勾选同意用户守则。");
                agreementArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(agreementArea);
                JOptionPane.showMessageDialog(
                        KeyPassNetworkValidation.this,
                        scrollPane,
                        "用户守则",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        panel.add(userAgreementLabel);
        JCheckBox agreeCheckBox = new JCheckBox("我已阅读并同意用户守则");
        panel.add(agreeCheckBox);
        int choice = JOptionPane.showOptionDialog(
                this,
                panel,
                "提示",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"确定", "取消"},
                "确定"
        );

        if (choice == JOptionPane.OK_OPTION) {
            if (!agreeCheckBox.isSelected()) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(
                                this,
                                "请勾选同意用户守则",
                                "提示",
                                JOptionPane.WARNING_MESSAGE
                        )
                );
                showDownloadDialog(targetFile);
            }
            executor.execute(() -> {
                try {
                    downloadUpdate(targetFile);
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(
                                    this,
                                    "下载成功，文件已保存到mods目录，重启游戏后生效。",
                                    "提示",
                                    JOptionPane.INFORMATION_MESSAGE
                            )
                    );
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(
                                    this,
                                    "下载失败,请检查mods目录是否存在！",
                                    "错误",
                                    JOptionPane.ERROR_MESSAGE
                            )
                    );
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            try {
                                desktop.browse(new URI(UPDATE_URL));
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(
                                        this,
                                        "自动打开浏览器失败，请手动访问：" + UPDATE_URL,
                                        "提示",
                                        JOptionPane.WARNING_MESSAGE
                                );
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "请手动访问下载链接：" + UPDATE_URL,
                                "提示",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }
                }
            });
        }
    }

    private void downloadUpdate(File targetFile) throws IOException {
        URL url = new URL(UPDATE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        try (InputStream in = connection.getInputStream();
             OutputStream out = Files.newOutputStream(targetFile.toPath())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            connection.disconnect();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(KeyPassNetworkValidation::new);
    }
}
