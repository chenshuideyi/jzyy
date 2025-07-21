package com.csdy.jzyy.cheat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KeyPassNetworkValidation extends JFrame {
    private JTextField codeField;
    private JButton buyKeyButton;

    private static final String APP_ID = "10117";
    private static final String API_HOST = "http://yz.xywyz.cn/api.php?api=kmlogon";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public KeyPassNetworkValidation() {
        setTitle("卡密验证 (任何字符即可)");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 2));

        add(new JLabel("输入卡密："));
        codeField = new JTextField();
        add(codeField);

        JButton validateButton = new JButton("验证");
        validateButton.addActionListener(this::validateActivationCode);
        add(validateButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void validateActivationCode(ActionEvent e) {
        String authCode = codeField.getText().trim();

        if (authCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入卡密", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        validateActivationCode(authCode);
    }

    private void validateActivationCode(String authCode) {
        if (authCode.isEmpty()) {
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, "未找到有效卡密", "错误", JOptionPane.ERROR_MESSAGE)
            );
            return;
        }

        long timeStamp = System.currentTimeMillis() / 1000;
        String deviceId = getDeviceId();

        String finalUrl = String.format("%s&app=%s&kami=%s&markcode=%s&t=%d",
                API_HOST, APP_ID, authCode, deviceId, timeStamp);

        executor.execute(() -> {
            try {
                String response = sendValidationRequest(finalUrl);
                System.out.println("原始响应内容: " + response);
                Map<String, Object> result = parseJsonResponse(response);

                SwingUtilities.invokeLater(() -> {
                    try {
                        int code = Integer.parseInt(result.get("code").toString());

                        if (code == 200) {
                            Map<String, Object> msg = (Map<String, Object>) result.get("msg");
                            String vipTimestamp = (String) msg.get("vip");
                            long vipTime = Long.parseLong(vipTimestamp) * 1000;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String vipDate = sdf.format(new Date(vipTime));
                            System.out.println("自动验证成功 到期时间 : " + vipDate);
                            UI.main(new String[]{});
                            dispose();
                        } else {
                            String msg = result.containsKey("msg") ?
                                    result.get("msg").toString() : "未知错误";
                            JOptionPane.showMessageDialog(this, "验证失败：" + msg, "错误", JOptionPane.ERROR_MESSAGE);
                            setVisible(true);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "响应解析失败：" + response, "错误", JOptionPane.ERROR_MESSAGE);
                        setVisible(true);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "网络错误，请检查连接", "错误", JOptionPane.ERROR_MESSAGE)
                );
                setVisible(true);
            }
        });
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

    private String getDeviceId() {
        String userName = System.getProperty("user.name");
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        return String.format("%s-%s-%s", userName, osName, osVersion).hashCode() + "";
    }

    private Map<String, Object> parseJsonResponse(String response) {
        try {
            Gson gson = new Gson();
            if (response != null && !response.trim().isEmpty() && response.trim().startsWith("<html>")) {
                Map<String, Object> errorMap = new HashMap<>();
                errorMap.put("code", -2);
                errorMap.put("msg", "收到HTML响应，服务器可能已移动或不可用");
                return errorMap;
            }
            if (response == null || response.trim().isEmpty()) {
                Map<String, Object> errorMap = new HashMap<>();
                errorMap.put("code", -4);
                errorMap.put("msg", "空响应内容");
                return errorMap;
            }
            JsonReader reader = new JsonReader(new StringReader(response));
            reader.setLenient(true);
            Map<String, Object> result = new HashMap<>();
            reader.beginObject();
            while (reader.hasNext()) {
                String key = reader.nextName();
                if (key.equals("code")) {
                    result.put(key, reader.nextInt());
                } else if (key.equals("msg")) {
                    reader.beginObject();
                    Map<String, Object> msgMap = new HashMap<>();
                    while (reader.hasNext()) {
                        String msgKey = reader.nextName();
                        if (msgKey.equals("kami")) {
                            msgMap.put(msgKey, reader.nextString());
                        } else if (msgKey.equals("vip")) {
                            msgMap.put(msgKey, reader.nextString());
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    result.put(key, msgMap);
                } else if (key.equals("time")) {
                    result.put(key, reader.nextLong());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            if (result.containsKey("msg") && result.get("msg") instanceof Map) {
                return result;
            } else {
                Map<String, Object> errorMap = new HashMap<>();
                errorMap.put("code", -3);
                errorMap.put("msg", "响应格式错误，缺少msg字段或格式不正确: " + response);
                return errorMap;
            }
        } catch (JsonSyntaxException e) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("code", -1);
            errorMap.put("msg", "JSON 解析失败: " + response);
            return errorMap;
        } catch (IOException e) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("code", -4);
            errorMap.put("msg", "JSON 解析失败（IO异常）: " + response);
            return errorMap;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(KeyPassNetworkValidation::new);
    }
}
