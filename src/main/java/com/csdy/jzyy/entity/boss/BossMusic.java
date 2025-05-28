package com.csdy.jzyy.entity.boss;



import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

public class BossMusic extends AbstractTickableSoundInstance {
    BossEntity boss;

    protected BossMusic(BossEntity boss) {
        super(boss.getBossMusic(), SoundSource.RECORDS, RandomSource.create());
        this.boss = boss;
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.looping = true;
    }

    @Override
    public void tick() {
        if (this.boss == null || !this.boss.isAlive() || Minecraft.getInstance().player == null) {
            this.stop(); // Boss没了或玩家没了，就停止
            return;
        }

        // 根据音乐设置调整主音量
        if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) <= 0.0F) {
            this.volume = 0.0F;
            // 可以考虑直接停止，而不是仅仅静音，因为听不到的循环音乐也是一种资源消耗
            // this.stop();
            return;
        } else {
            // 恢复到基础音量（如果之前因为设置被静音了）
            // 这个基础音量应该从构造函数或一个变量读取，而不是写死1.0f
            this.volume = 1.0f; // 假设基础音量是1.0f
        }


        // 根据距离动态调整音量 (示例：线性衰减)
        float maxDistance = 60.0f; // 音乐完全听不见的距离
        float distance = this.boss.distanceTo(Minecraft.getInstance().player);

        if (distance > maxDistance) {
            this.volume = 0.0f;
            // 也可以选择停止音乐 this.stop();
        } else if (distance < 5.0f) { // 5格内最大音量
            this.volume = 1.0f * Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC); // 乘上用户设置
        }
        else {
            // (1.0f - (distance / maxDistance)) 是一个从近到远 1 -> 0 的因子
            this.volume = (1.0f - (Math.max(0, distance - 5.0f) / (maxDistance - 5.0f))) * Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC);
        }
        this.volume = Mth.clamp(this.volume, 0.0f, 1.0f); // 确保音量在0-1之间


        // 更新声音实例的位置到Boss身上（如果Boss会移动）
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
