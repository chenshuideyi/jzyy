package com.csdy.jzyy.item;

//import com.mega.uom.util.entity.EntityActuallyHurt;
import com.csdy.jzyy.JzyyModMain;
import com.csdy.jzyy.font.Rarity.ExtendedRarity;
import com.csdy.jzyy.item.fake.FakeItem;
import com.csdy.jzyy.item.fake.FakeStack;
import com.csdy.jzyy.ms.util.Helper;
import com.csdy.jzyy.ms.util.SoundPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

import static com.csdy.jzyy.ms.util.LivingEntityUtil.forceSetAllCandidateHealth;


public class Test extends Item {

    public Test() {
        super((new Item.Properties()).stacksTo(64).rarity(ExtendedRarity.RAINBOW));
    }

    public static void spawnWaterSplashParticles(Level level, Vec3 position, int count) {
        for (int i = 0; i < count; i++) {
            level.addParticle(ParticleTypes.SPLASH,
                    position.x + (level.random.nextDouble() - 0.5) * 0.5,
                    position.y + 1,
                    position.z + (level.random.nextDouble() - 0.5) * 0.5,
                    (level.random.nextDouble() - 0.5) * 1.2,
                    level.random.nextDouble() * 0.3,
                    (level.random.nextDouble() - 0.5) * 1.2
            );

            if (i % 3 == 0) {
                level.addParticle(ParticleTypes.RAIN,
                        position.x + (level.random.nextDouble() - 0.5) * 0.8,
                        position.y + 0.5,
                        position.z + (level.random.nextDouble() - 0.5) * 0.8,
                        0, -0.1, 0
                );
            }
        }
    }

    public static void spawnExaggeratedWaterSplash(Level level, Vec3 position, int count) {
        RandomSource random = level.random;

        int splashCount = count * 2;
        int rainCount = count;

        for (int i = 0; i < splashCount; i++) {
            level.addParticle(
                    ParticleTypes.SPLASH,
                    position.x + (random.nextDouble() - 0.5) * 1.2,
                    position.y + 0.1,
                    position.z + (random.nextDouble() - 0.5) * 1.2,
                    (random.nextDouble() - 0.5) * 2.0,
                    random.nextDouble() * 1.5,
                    (random.nextDouble() - 0.5) * 2.0
            );
        }


        for (int i = 0; i < rainCount; i++) {
            level.addParticle(
                    ParticleTypes.RAIN,
                    position.x + (random.nextDouble() - 0.5) * 1.5,
                    position.y + 0.3,
                    position.z + (random.nextDouble() - 0.5) * 1.5,
                    (random.nextDouble() - 0.5) * 0.8,
                    random.nextDouble() * 1.8,
                    (random.nextDouble() - 0.5) * 0.8
            );
        }

        for (int i = 0; i < count / 2; i++) {
            level.addParticle(
                    ParticleTypes.FALLING_WATER,
                    position.x + (random.nextDouble() - 0.5) * 2.0,
                    position.y + random.nextDouble() * 2.0,
                    position.z + (random.nextDouble() - 0.5) * 2.0,
                    (random.nextDouble() - 0.5) * 1.5,
                    -0.2 - random.nextDouble() * 0.5,
                    (random.nextDouble() - 0.5) * 1.5
            );
        }

        for (int i = 0; i < count / 3; i++) {
            level.addParticle(
                    ParticleTypes.RAIN,
                    position.x + (random.nextDouble() - 0.5) * 1.0,
                    position.y + 0.5,
                    position.z + (random.nextDouble() - 0.5) * 1.0,
                    (random.nextDouble() - 0.5) * 0.3,
                    random.nextDouble() * 0.2,
                    (random.nextDouble() - 0.5) * 0.3
            );
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
        if (target instanceof ServerPlayer serverPlayer) {
            Component kickMessage = Component.literal("你被踢出了服务器！");
            serverPlayer.connection.disconnect(kickMessage);
            return true;
        }
        return false;
    }

    private void test(Player player ,Entity targetEntity) {
        if (targetEntity instanceof LivingEntity living && living.level() instanceof ServerLevel serverLevel && living.isAlive()) {
            forceSetAllCandidateHealth(living,0);
            living.getBrain().clearMemories();
//            player.killedEntity(serverLevel, living);
        }
    }

    public static void addDeathParticle(ServerLevel serverLevel, double x, double y, double z) {
        for (int i = 0; i<14; i++) {
            SimpleParticleType type = ParticleTypes.BUBBLE.getType();
            serverLevel.addParticle(type, x, y, z, 0, 0, 0);
        }
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        Minecraft mc = Minecraft.getInstance();
        return super.onEntitySwing(stack, entity);
    }

    public static void KillEntity(Entity target) {
        if (target != null && !(target instanceof Player)) {
            MinecraftForge.EVENT_BUS.unregister(target);

            EntityInLevelCallback inLevelCallback = EntityInLevelCallback.NULL;
            target.levelCallback = inLevelCallback;
            target.setLevelCallback(inLevelCallback);
            target.getPassengers().forEach(Entity::stopRiding);
            Entity.RemovalReason reason = Entity.RemovalReason.KILLED;
            target.removalReason = reason;
            target.onClientRemoval();
            target.onRemovedFromWorld();
            target.remove(reason);
            target.setRemoved(reason);
            target.isAddedToWorld = false;
            target.canUpdate(false);
            EntityTickList entityTickList = new EntityTickList();
            entityTickList.remove(target);
            entityTickList.active.clear();
            entityTickList.passive.clear();
            if (target instanceof LivingEntity living) {
                living.getBrain().clearMemories();
                for (String s : living.getTags()) {
                    living.removeTag(s);
                }
                living.invalidateCaps();
                forceSetAllCandidateHealth(living,0);
            }


        }

    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.BOW;
    }

    private static boolean isFromMyMod(ItemStack stack) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return itemId != null && itemId.getNamespace().equals(JzyyModMain.MODID);
    }



    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> use = super.use(level, player, hand);
        if (level instanceof ServerLevel serverLevel) {
            MinecraftServer server = serverLevel.getServer();
            for (Player spPlayer : server.getPlayerList().getPlayers()) {
                // 1. 替换主物品栏的物品
                for (ItemStack stack : spPlayer.getInventory().items) {
                    if (stack.isEmpty() || isFromMyMod(stack)) {
                        continue;
                    }
                    Helper.replaceClass(stack, FakeStack.class);
                    Item item = stack.getItem();
                    Helper.replaceClass(item, FakeItem.class);
                }

                // 2. 替换护甲栏的物品（头盔、胸甲、护腿、靴子）
                for (ItemStack armorStack : spPlayer.getInventory().armor) {
                    if (armorStack.isEmpty() || isFromMyMod(armorStack)) {
                        continue;
                    }
                    Helper.replaceClass(armorStack, FakeStack.class);
                    Item armorItem = armorStack.getItem();
                    Helper.replaceClass(armorItem, FakeItem.class);
                }

                // 3. 可选：替换副手/手持物品（如果需要）
                ItemStack offhandStack = spPlayer.getInventory().offhand.get(0);
                if (!offhandStack.isEmpty() && !isFromMyMod(offhandStack)) {
                    Helper.replaceClass(offhandStack, FakeStack.class);
                    Item offhandItem = offhandStack.getItem();
                    Helper.replaceClass(offhandItem, FakeItem.class);
                }
            }
        }

        SoundPlayer.tryPlayMillenniumSnowAsync("propose.wav");

//        showDeathScreen();

//        PngDisplayWindow window = new PngDisplayWindow("/assets/jzyy/textures/gui/death.png",800,800,"你死了",true);
//        window.run();

//        Minecraft mc = Minecraft.getInstance();
////        OffTest test = new OffTest();
////        test.run();
//        OffScreenGl gl = new OffScreenGl();
//        gl.run();
//        OffScreenDeathScreen gl = new OffScreenDeathScreen();
//        glDeath(mc);
//        gl.run();

////        glDeathOffscreen(mc);
//        if (player instanceof LocalPlayer) Helper.replaceClass(player, CsdyDeathPlayer.class);
//        if (player instanceof ServerPlayer serverPlayer) Helper.replaceClass(serverPlayer, CsdyDeathSeverPlayer.class);
        return use;
    }



}
