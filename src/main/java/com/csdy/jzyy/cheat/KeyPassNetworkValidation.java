package com.csdy.jzyy.cheat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KeyPassNetworkValidation extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JCheckBox autoLoginCheckBox;
    Minecraft mc = Minecraft.getInstance();

    private static final String APP_ID = "10117";
    private static final String LOGIN_API_HOST = "http://yz.xywyz.cn/api.php?api=userlogon";
    private static final String REGISTER_API_HOST = "http://yz.xywyz.cn/api.php?api=userreg";
    private static final String SAVE_FILE_PATH = "login_info.dat";
    private static final String ENCRYPTION_KEY = "LxTrackAutoLogin";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public KeyPassNetworkValidation() {
        setTitle("账号登录");
        setSize(350, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 2));

        add(new JLabel("用户名："));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("密码："));
        passwordField = new JPasswordField();
        add(passwordField);


        autoLoginCheckBox = new JCheckBox("自动登录");
        add(autoLoginCheckBox);
        add(new JLabel());

        JButton loginButton = new JButton("登录");
        loginButton.addActionListener(this::login);
        add(loginButton);

        JButton registerButton = new JButton("注册");
        registerButton.addActionListener(this::register);
        add(registerButton);

        setLocationRelativeTo(null);
        setVisible(true);

        tryAutoLogin();
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

    private void tryAutoLogin() {
        File saveFile = new File(SAVE_FILE_PATH);
        if (saveFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(saveFile))) {
                String encryptedUsername = br.readLine();
                String encryptedPassword = br.readLine();
                String username = decrypt(encryptedUsername);
                String password = decrypt(encryptedPassword);

                if (username != null && password != null) {
                    usernameField.setText(username);
                    passwordField.setText(password);
                    autoLoginCheckBox.setSelected(true);
                    login(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveLoginInfo(String username, String password) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SAVE_FILE_PATH))) {
            String encryptedUsername = encrypt(username);
            String encryptedPassword = encrypt(password);
            bw.write(encryptedUsername);
            bw.newLine();
            bw.write(encryptedPassword);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String encrypt(String data) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String decrypt(String encryptedData) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void login(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            if (e != null) {
                JOptionPane.showMessageDialog(this, "请输入用户名和密码", "错误", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        long timeStamp = System.currentTimeMillis() / 1000;
        String finalUrl = String.format("%s&app=%s&user=%s&password=%s&t=%d",
                LOGIN_API_HOST, APP_ID, username, password, timeStamp);

        executor.execute(() -> {
            try {
                String response = sendValidationRequest(finalUrl);
                System.out.println("登录响应内容: " + response);
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

                        if (code == 200) {
                            System.out.println("登录成功");
                            if (autoLoginCheckBox.isSelected()) {
                                saveLoginInfo(username, password);
                            } else {
                                File saveFile = new File(SAVE_FILE_PATH);
                                if (saveFile.exists()) {
                                    saveFile.delete();
                                }
                            }
                            UI.main(new String[]{});
                            dispose();
                        } else {
                            String msg = result.containsKey("msg") ?
                                    result.get("msg").toString() : "未知错误";
                            JOptionPane.showMessageDialog(this, "登录失败：" + msg, "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        System.err.println("登录响应处理异常: " + ex.getMessage());
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "响应处理失败：" + response, "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "网络错误，请检查连接", "错误", JOptionPane.ERROR_MESSAGE)
                );
            }
        });
    }

    private void register(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String nickname = mc.player != null ? mc.player.getName().getString() : "";

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名和密码", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long timeStamp = System.currentTimeMillis() / 1000;
            String finalUrl = String.format("%s&app=%s&user=%s&password=%s&name=%s&t=%d",
                    REGISTER_API_HOST, APP_ID, username, password, nickname, timeStamp);

        executor.execute(() -> {
            try {
                String response = sendValidationRequest(finalUrl);
                System.out.println("注册响应内容: " + response);
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

                        if (code == 200) {
                            String msg = result.containsKey("msg") ?
                                    result.get("msg").toString() : "注册成功";
                            JOptionPane.showMessageDialog(this, msg, "成功", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            String msg = result.containsKey("msg") ?
                                    result.get("msg").toString() : "未知错误";
                            JOptionPane.showMessageDialog(this, "注册失败：" + msg + "您的ID不能含有特殊字符", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        System.err.println("注册响应处理异常: " + ex.getMessage());
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "响应处理失败：" + response, "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "网络错误，请检查连接", "错误", JOptionPane.ERROR_MESSAGE)
                );
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


    public static void main(String[] args) {
        SwingUtilities.invokeLater(KeyPassNetworkValidation::new);
    }
}
