package com.csdy.jzyy.cheat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KeyPassNetworkValidation {
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
    public static boolean IsLogin = false;

    public KeyPassNetworkValidation() {
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        autoLoginCheckBox = new JCheckBox("自动登录");
        tryAutoLoginOrRegister();
    }

    private String getDeviceCode() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (byte b : mac) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private void tryAutoLoginOrRegister() {
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
                    return;
                }
            } catch (IOException e) {

            }
        }
        tryAutoRegister();
    }

    private void tryAutoRegister() {
        String deviceCode = getDeviceCode();
        String password = deviceCode + "LXNB";

        usernameField.setText(deviceCode);
        passwordField.setText(password);
        autoLoginCheckBox.setSelected(true);

        executor.execute(() -> {
            try {
                String nickname = mc.player != null ? mc.player.getName().getString() : "";
                long timeStamp = System.currentTimeMillis() / 1000;
                String finalUrl = String.format("%s&app=%s&user=%s&password=%s&name=%s&t=%d",
                        REGISTER_API_HOST, APP_ID, deviceCode, password, nickname, timeStamp);

                String response = sendValidationRequest(finalUrl);
                Map<String, Object> result = parseJsonResponse(response);

                int code = -4;
                if (result.containsKey("code")) {
                    Object codeObj = result.get("code");
                    if (codeObj instanceof Number) {
                        code = ((Number) codeObj).intValue();
                    }
                }

                if (code == 200) {
                    saveLoginInfo(deviceCode, password);
                    UI.main(new String[]{});
                } else {
                    login(null);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private Map<String, Object> parseJsonResponse(String response) {
        Map<String, Object> result = new HashMap<>();
        try {
            Gson gson = new Gson();
            if (response != null && response.trim().startsWith("<html>")) {
                result.put("code", -2);
                return result;
            }
            if (response == null || response.trim().isEmpty()) {
                result.put("code", -4);
                return result;
            }

            result = gson.fromJson(response, HashMap.class);
            if (!result.containsKey("code")) {
                result.put("code", -3);
            }
        } catch (JsonSyntaxException e) {
            result.put("code", -1);
        } catch (Exception e) {
            result.put("code", -4);
        }
        return result;
    }

    private void saveLoginInfo(String username, String password) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SAVE_FILE_PATH))) {
            String encryptedUsername = encrypt(username);
            String encryptedPassword = encrypt(password);
            bw.write(encryptedUsername);
            bw.newLine();
            bw.write(encryptedPassword);
        } catch (IOException e) {

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

            return null;
        }
    }

    private void login(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            return;
        }

        long timeStamp = System.currentTimeMillis() / 1000;
        String finalUrl = String.format("%s&app=%s&user=%s&password=%s&t=%d",
                LOGIN_API_HOST, APP_ID, username, password, timeStamp);

        executor.execute(() -> {
            try {
                String response = sendValidationRequest(finalUrl);
                Map<String, Object> result = parseJsonResponse(response);

                int code = -4;
                if (result.containsKey("code")) {
                    Object codeObj = result.get("code");
                    if (codeObj instanceof Number) {
                        code = ((Number) codeObj).intValue();
                    }
                }

                if (code == 200) {
                    if (autoLoginCheckBox.isSelected()) {
                        saveLoginInfo(username, password);
                    } else {
                        File saveFile = new File(SAVE_FILE_PATH);
                        if (saveFile.exists()) {
                            saveFile.delete();
                        }
                    }
                    IsLogin = true;
                    UI.main(new String[]{});
                } else {

                }
            } catch (Exception ex) {
                ex.printStackTrace();

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

            }
        } catch (IOException e) {

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return urlStr;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(KeyPassNetworkValidation::new);
    }
}
