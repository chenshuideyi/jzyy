package com.csdy.jzyy.ms.util;

import javax.sound.sampled.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SoundPlayer {

        private static final List<String> MUSIC_FILES = Arrays.asList(
            "the_millennium_snow.wav",
            "hated_by_life_itself.wav",
            "propose.wav",
            "stronger_than_you.wav"
            // 可以继续添加更多音乐文件
    );

    private static final AtomicBoolean isMillenniumSnowPlaying = new AtomicBoolean(false);
    private static Clip currentClip = null;
    private static final String MUSIC_FOLDER = "assets/jzyy/csdy_like_music/";



    /**
     * 尝试播放音乐，如果当前未播放则开始播放。
     * 在新线程中异步执行。
     * @param sound  wav文件位置
     */
    public static void tryPlay(String sound) {
        // 检查文件是否存在
        if (!MUSIC_FILES.contains(sound)) {
            System.out.println("音乐文件不存在: " + sound);
            return;
        }

        // 将 false 设置为 true，如果成功（原来是 false），则表示可以播放
        if (isMillenniumSnowPlaying.compareAndSet(false, true)) {
            Thread soundThread = new Thread(() -> {
                playSoundInternal(sound, () -> {
                    isMillenniumSnowPlaying.set(false);
                });
            }, "开始播放" + sound);
            soundThread.start();
        } else {
            System.out.println("正在播放" + sound);
        }
    }

    /**
     * 随机播放一首音乐
     */
    public static void tryPlayRandom() {
        if (MUSIC_FILES.isEmpty()) {
            System.out.println("没有可用的音乐文件");
            return;
        }

        Random random = new Random();
        String randomMusic = MUSIC_FILES.get(random.nextInt(MUSIC_FILES.size()));
        tryPlay(randomMusic);
    }

    /**
     * 停止当前正在播放的音乐
     */
    public static void stopPlay() {
        if (currentClip != null && currentClip.isOpen() && currentClip.isRunning()) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
            isMillenniumSnowPlaying.set(false);
            System.out.println("音乐已停止");
        } else {
            System.out.println("当前没有音乐在播放");
        }
    }

    /**
     * 内部播放逻辑，接受一个完成时的回调。
     *
     * @param soundFileName 声音文件名
     * @param onComplete    播放完成或失败时执行的回调
     */
    private static void playSoundInternal(String soundFileName, Runnable onComplete) {
        String resourcePath = MUSIC_FOLDER + soundFileName;
        Clip clip = null;

        try (InputStream audioSrc = SoundPlayer.class.getClassLoader().getResourceAsStream(resourcePath);
             BufferedInputStream bufferedIn = (audioSrc != null) ? new BufferedInputStream(audioSrc) : null) {

            if (bufferedIn == null) {
                System.err.println("无法找到音乐: " + resourcePath);
                return; // 退出线程
            }

            try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn)) {
                clip = AudioSystem.getClip();
                currentClip = clip; // 保存当前Clip引用
                final CountDownLatch syncLatch = new CountDownLatch(1);
                final Clip finalClip = clip; // 在 lambda 中使用需要 effectively final

                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP || event.getType() == LineEvent.Type.CLOSE) {
                        // 确保 Clip 没有被提前关闭（例如在异常中）
                        if (finalClip != null && finalClip.isOpen()) {
                            finalClip.close(); // 在监听器中关闭 Clip
                            currentClip = null; // 清除当前Clip引用
                            System.out.println("已关闭Clip: " + soundFileName);
                        }
                        if (syncLatch.getCount() > 0) {
                            syncLatch.countDown();
                        }
                    }
                });

                clip.open(audioStream);
                clip.start();
                System.out.println("正在播放: " + soundFileName);

                try {
                    syncLatch.await(); // 等待播放结束 (STOP 或 CLOSE 事件)
                } catch (InterruptedException e) {
                    if (clip != null && clip.isOpen()) {
                        clip.stop(); // 停止播放
                    }
                    Thread.currentThread().interrupt();
                }

            } // AudioInputStream 在这里自动关闭

        } catch (UnsupportedAudioFileException e) {
            System.err.println("不支持的音频格式: " + resourcePath);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO异常: " + resourcePath);
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("音频线路不可用: " + resourcePath);
            e.printStackTrace();
        } finally {
            // 无论成功还是失败，最终都要执行回调（重置标志位）
            if (onComplete != null) {
                onComplete.run();
            }
            // 额外的安全措施：如果 Clip 因异常未在监听器中关闭，在此处尝试关闭
            if (clip != null && clip.isOpen()) {
                clip.close();
                currentClip = null; // 清除当前Clip引用
                System.out.println("清除当前Clip引用: " + soundFileName);
            }
            System.out.println("播放结束" + soundFileName);
        }
    }
}
