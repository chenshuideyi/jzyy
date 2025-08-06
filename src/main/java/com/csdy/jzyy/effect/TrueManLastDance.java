package com.csdy.jzyy.effect;


import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.effect.register.JzyyEffectRegister;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = JzyyModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TrueManLastDance extends MobEffect {

    // 属性修改器的UUID（需保持唯一性）
    private static final String ATTACK_DAMAGE_MODIFIER_UUID = "7237DE5E-7CE8-4030-940E-514C1F160890";
    // 基础攻击力修正值（初始为0）
    private static final double BASE_ATTACK_DAMAGE_MODIFIER = 0;
    // 每次受伤时的攻击力提升幅度（10%）
    private static final double DAMAGE_BOOST_PER_HIT = 0.1;
    // 最大攻击力提升上限（100%）
    private static final double MAX_DAMAGE_BOOST = 1.0;

    public TrueManLastDance() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFFFF); // 白色效果粒子
        // 初始化时添加基础属性修改器（初始无效果）
        this.addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                ATTACK_DAMAGE_MODIFIER_UUID,
                BASE_ATTACK_DAMAGE_MODIFIER,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(JzyyEffectRegister.TRUE_MAN_LAST_DANCE.get())) {
            AttributeInstance attackDamage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamage != null) {

                // 2. 移除旧的修改器
                AttributeModifier oldModifier = attackDamage.getModifier(UUID.fromString(ATTACK_DAMAGE_MODIFIER_UUID));
                if (oldModifier != null) {
                    attackDamage.removeModifier(oldModifier);
                }

                // 3. 计算新的加成值（旧值 + 每次受伤加成，不超过上限）
                double currentBoost = oldModifier != null ? oldModifier.getAmount() : BASE_ATTACK_DAMAGE_MODIFIER;
                double newBoost = Math.min(currentBoost + DAMAGE_BOOST_PER_HIT, MAX_DAMAGE_BOOST);

                // 4. 应用新的属性修改器
                AttributeModifier newModifier = new AttributeModifier(
                        UUID.fromString(ATTACK_DAMAGE_MODIFIER_UUID),
                        "真男人最后的舞曲伤害加成",
                        newBoost,
                        AttributeModifier.Operation.MULTIPLY_TOTAL // 百分比加成
                );

                attackDamage.addTransientModifier(newModifier);

                // 5. [可选] 添加视觉反馈
                if (entity.level().isClientSide) {
                    entity.level().addParticle(
                            ParticleTypes.HEART,
                            entity.getX(),
                            entity.getY() + 2.0,
                            entity.getZ(),
                            0, 0.1, 0
                    );
                }
        }
    }
        }


    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 每tick执行的效果（如需要持续治疗效果可在此添加）
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 是否需要每tick执行applyEffectTick
        return false; // 本例中不需要持续效果
    }
}
