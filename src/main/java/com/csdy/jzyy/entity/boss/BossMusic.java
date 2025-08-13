package com.csdy.jzyy.entity.boss;


import com.csdy.jzyy.sounds.JzyySoundsRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;

public class BossMusic extends AbstractTickableSoundInstance {
    BossEntity boss;
    private final float baseVolume; // 新增：用于存储这个音乐的基础音量

    protected BossMusic(BossEntity boss) {
        super(boss.getBossMusic(), SoundSource.RECORDS, RandomSource.create());
        this.boss = boss;
        this.pitch = 1.0f;
        this.looping = true;

        this.baseVolume = 0.5f; // 为这首特定的音乐设置一个较低的基础音量（例如60%）

        this.volume = this.baseVolume; // 初始化音量
    }

    @Nullable
    public static BossMusic create(BossEntity boss) {
        if (boss == null || boss.getBossMusic() == null) {
            return null; // 如果 boss 或音乐是 null，返回 null
        }
        return new BossMusic(boss);
    }

    @Override
    public void tick() {
        if (this.boss == null || !this.boss.isAlive() || Minecraft.getInstance().player == null) {
            this.stop();
            return;
        }

        if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) <= 0.0F) {
            this.volume = 0.0F;
            return;
        }

        // 根据距离动态调整音量
        float maxDistance = 60.0f;
        float distance = this.boss.distanceTo(Minecraft.getInstance().player);
        float playerMusicVolumeSetting = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC);

        if (distance > maxDistance) {
            this.volume = 0.0f;
        } else if (distance < 5.0f) {
            // 修改：使用 baseVolume 替代 1.0f
            this.volume = this.baseVolume * playerMusicVolumeSetting;
        }
        else {
            float distanceFactor = 1.0f - (Math.max(0, distance - 5.0f) / (maxDistance - 5.0f));
            // 修改：使用 baseVolume 替代 1.0f
            this.volume = this.baseVolume * distanceFactor * playerMusicVolumeSetting;
        }
        this.volume = Mth.clamp(this.volume, 0.0f, 1.0f);


        this.x = this.boss.getX();
        this.y = this.boss.getY();
        this.z = this.boss.getZ();
    }

    public boolean canPlayMusic() {
        boolean b = true;
        for (SoundInstance soundInstance : Minecraft.getInstance().getSoundManager().soundEngine.tickingSounds) {
            if (!soundInstance.getLocation().equals(this.getLocation()) || !(soundInstance.getVolume() > 0.0f)) continue;
            b = false;
        }
        return !Minecraft.getInstance().getSoundManager().isActive(this) && Minecraft.getInstance().level.isClientSide() && b;
    }

    public boolean isStopped() {
        return super.isStopped();
    }

    public static void playMusic(BossMusic music, BossEntity bossEntity) {
        if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) <= 0.0f) {
            music = null;
        }
        if (music != null) {
            music = new BossMusic(bossEntity);
        }
        if (music != null && music.canPlayMusic()) {
            Minecraft.getInstance().getSoundManager().play(music);
        }
    }

    public float getVolume() {
        return this.volume;
    }
}
