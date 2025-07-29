package com.csdy.jzyy.cheat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KeyPassNetworkValidation extends JFrame {

    private static final String APP_ID = "10117";
    private static final String CURRENT_VERSION = "2025726";
    private static final String CONFIG_API_HOST = "http://yz.xywyz.cn/api.php?api=ini";
    private static final String UPDATE_URL = "https://d.feiliupan.com/t/97474712677912576/libLxTrack.jar";
    private static final String TEMP_FILE_NAME = "libLxTrack_temp.jar";
    private static final String JAR_FILE_NAME = "libLxTrack.jar";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public KeyPassNetworkValidation() {
        checkForUpdate();
    }


    private String sendValidationRequest(String urlStr) throws Exception {
        URL url;
        HttpURLConnection conn = null;
        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("User-Agent", "LxTrackApp/1.0");
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                String newUrl = conn.getHeaderField("Location");
                conn.disconnect();
                return sendValidationRequest(newUrl);
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP错误代码: " + responseCode + "，请求地址: " + urlStr);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            } catch (SocketException e) {
                throw new IOException("网络连接被重置，请检查网络连接", e);
            }
        } catch (IOException e) {
            throw new Exception("网络请求失败: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


    private void checkForUpdate() {
        String finalUrl = String.format("%s&app=%s", CONFIG_API_HOST, APP_ID);

        executor.execute(() -> {
            try {
                String response = sendValidationRequest(finalUrl);
                Map<String, Object> result = parseJsonResponse(response);

                SwingUtilities.invokeLater(() -> {
                    try {
                        int code = -4;
                        if (result.containsKey("code")) {
                            Object codeObj = result.get("code");
                            if (codeObj instanceof Number) {
                                code = ((Number) codeObj).intValue();
                            }
                        }
                        if (code == 200 && result.containsKey("msg")) {
                            Map<String, Object> msg = (Map<String, Object>) result.get("msg");
                            if (msg != null && msg.containsKey("version")) {
                                String serverVersion = (String) msg.get("version");
                                if (!serverVersion.equals(CURRENT_VERSION)) {
                                    showUpdateDialog();
                                }
                            }
                        }
                    } catch (Exception ex) {
                        System.err.println("更新检查异常: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }


    private void showUpdateDialog() {
        String message = "发现新版本！请点击确定下载更新";
        int choice = JOptionPane.showConfirmDialog(this, message, "更新提示", JOptionPane.OK_CANCEL_OPTION);
        if (choice == JOptionPane.OK_OPTION) {
            try {
                downloadUpdate();
                replaceJarFile();
                JOptionPane.showMessageDialog(this, "更新成功，重启游戏生效。", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                System.err.println("更新失败: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "更新失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void downloadUpdate() throws IOException {
        URL url = new URL(UPDATE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(TEMP_FILE_NAME)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private void replaceJarFile() throws IOException {
        File tempFile = new File(TEMP_FILE_NAME);
        File jarFile = new File(JAR_FILE_NAME);

        if (jarFile.exists()) {
            jarFile.delete();
        }

        Files.move(tempFile.toPath(), jarFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    private Map<String, Object> parseJsonResponse(String response) {
        Map<String, Object> result = new HashMap<>();
        try {
            Gson gson = new Gson();
            if (response != null && !response.trim().isEmpty() && response.trim().startsWith("<html>")) {
                result.put("code", -2);
                result.put("msg", "收到HTML响应，服务器可能已移动或不可用");
                return result;
            }
            if (response == null || response.trim().isEmpty()) {
                result.put("code", -4);
                result.put("msg", "空响应内容");
                return result;
            }

            result = gson.fromJson(response, HashMap.class);
            if (!result.containsKey("code")) {
                result.put("code", -3);
                result.put("msg", "响应格式错误，缺少 code 字段: " + response);
            }
        } catch (JsonSyntaxException e) {
            result.put("code", -1);
            result.put("msg", "JSON 解析失败: " + response + ", 错误信息: " + e.getMessage());
        } catch (Exception e) {
            result.put("code", -4);
            result.put("msg", "JSON 解析失败（异常）: " + response + ", 错误信息: " + e.getMessage());
        }
        return result;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(KeyPassNetworkValidation::new);
    }
}
