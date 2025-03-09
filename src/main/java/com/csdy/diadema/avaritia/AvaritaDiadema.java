package com.csdy.diadema.avaritia;

import com.csdy.ModMain;
import com.csdy.diadema.api.ranges.HalfSphereDiademaRange;
import com.csdy.diadema.api.ranges.SphereDiademaRange;
import com.csdy.frames.diadema.Diadema;
import com.csdy.frames.diadema.DiademaType;
import com.csdy.frames.diadema.movement.DiademaMovement;
import com.csdy.frames.diadema.range.DiademaRange;
import com.csdy.network.ParticleSyncing;
import com.csdy.network.packets.AvaritaPacket;
import com.csdy.network.packets.SonicBoomPacket;
import com.csdy.sounds.SoundsRegister;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AvaritaDiadema extends Diadema {
    static final double RADIUS = 6;
    private  Player player = getPlayer();

    public AvaritaDiadema(DiademaType type, DiademaMovement movement) {
        super(type, movement);
    }
    private final SphereDiademaRange range = new SphereDiademaRange(this,RADIUS);

    @Override
    public @NotNull DiademaRange getRange() {
        return range;
    }

    @Override protected void perTick() {
        // 像这样就能获取到受影响的方块了，方块集合不自动更新所以得重复获取
        var blocks = range.getAffectingBlocks();
        //java比较弱智，Stream不能用来for,只能foreach
        blocks.forEach(blockPos -> {
            if (getLevel().getBlockState(blockPos).getBlock() == Blocks.AIR || getLevel().getBlockState(blockPos).getBlock() == Blocks.GOLD_BLOCK) return;
            getLevel().setBlockAndUpdate(blockPos, Blocks.GOLD_BLOCK.defaultBlockState());
        });
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        // 获取玩家和捡起的物品
        this.player = event.getEntity();
        ItemStack itemStack = event.getItem().getItem();

        // 检查捡起的物品是否为金块
        if (itemStack.getItem() == Items.GOLD_BLOCK) {
            // 取消捡起行为
            event.setCanceled(true);
            event.getItem().discard();
            float currentAbsorption = player.getAbsorptionAmount();
            float newAbsorption = currentAbsorption + 8.0F; // 每次增加 4 颗黄心（8点吸收生命值）
            player.setAbsorptionAmount(newAbsorption);
            //发包
            ParticleSyncing.CHANNEL.send(PacketDistributor.NEAR.with(()->new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(),
                            16, player.level.dimension()))
                    ,new AvaritaPacket(player.position));
        }
    }
}
