package com.csdy.jzyy.ms.util;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class SoundPlayer {

    // 静态原子布尔值，用于跟踪 specificSound 是否正在播放
    // AtomicBoolean 保证了在多线程环境下的可见性和原子性操作
    private static final AtomicBoolean isMillenniumSnowPlaying = new AtomicBoolean(false);

    /**
     * 尝试播放音乐，如果当前未播放则开始播放。
     * 在新线程中异步执行。
     * @param sound  wav文件位置
     */
    public static void tryPlayMillenniumSnowAsync(String sound) {
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
     * 内部播放逻辑，接受一个完成时的回调。
     *
     * @param soundFileName 声音文件名
     * @param onComplete    播放完成或失败时执行的回调
     */
    private static void playSoundInternal(String soundFileName, Runnable onComplete) {
        String resourcePath = "assets/jzyy/csdy_like_music/" + soundFileName;
        Clip clip = null;

        try (InputStream audioSrc = SoundPlayer.class.getClassLoader().getResourceAsStream(resourcePath);
             BufferedInputStream bufferedIn = (audioSrc != null) ? new BufferedInputStream(audioSrc) : null) { // 使用 try-with-resources

            if (bufferedIn == null) {
                System.err.println("Sound resource not found: " + resourcePath);
                return; // 退出线程
            }

            try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn)) {
                clip = AudioSystem.getClip();
                final CountDownLatch syncLatch = new CountDownLatch(1);
                final Clip finalClip = clip; // 在 lambda 中使用需要 effectively final

                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP || event.getType() == LineEvent.Type.CLOSE) {
                        // 确保 Clip 没有被提前关闭（例如在异常中）
                        if (finalClip != null && finalClip.isOpen()) {
                            finalClip.close(); // 在监听器中关闭 Clip
                            System.out.println("Clip closed via listener for: " + soundFileName);
                        }
                        if (syncLatch.getCount() > 0) {
                            syncLatch.countDown();
                        }
                    }
                });

                clip.open(audioStream);
                System.out.println("Playing sound: " + soundFileName);
                clip.start();

                try {
                    syncLatch.await(); // 等待播放结束 (STOP 或 CLOSE 事件)
                } catch (InterruptedException e) {
                    System.err.println("Sound playback interrupted for: " + soundFileName);
                    if (clip != null && clip.isOpen()) {
                        clip.stop(); // 停止播放
                    }
                    Thread.currentThread().interrupt();
                }

            } // AudioInputStream 在这里自动关闭

        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio file format for: " + resourcePath);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException during sound playback for: " + resourcePath);
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable for playing sound: " + resourcePath);
            e.printStackTrace();
        } finally {
            // 无论成功还是失败，最终都要执行回调（重置标志位）
            if (onComplete != null) {
                onComplete.run();
            }
            // 额外的安全措施：如果 Clip 因异常未在监听器中关闭，在此处尝试关闭
            if (clip != null && clip.isOpen()) {
                clip.close();
                System.out.println("Clip closed via finally block for: " + soundFileName);
            }
            System.out.println("Sound playback attempt finished for: " + soundFileName);
        }
    }

}
