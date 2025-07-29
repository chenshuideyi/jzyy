package com.csdy.jzyy.mixins;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.security.CodeSource;
import java.util.Arrays;

@Mixin(net.minecraft.client.renderer.GameRenderer.class)
public abstract class GameRenderer {

    @Unique
    ItemStack avaritia$stack;
    @Unique
    VertexConsumer avaritia$vertexConsumer;
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "render(FJZ)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", ordinal = 0)
    )
    private void onRenderTick(float partialTicks, long finishTimeNano, boolean renderLevel, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof DeathScreen) {
            mc.screen = null;
        }

        if (mc.player != null && mc.level != null && isNotMinecraftGUI(mc.screen)) {
            mc.screen = null;
            mc.mouseHandler.grabMouse();
        }
    }


    @Contract("null -> false")
    private static boolean isNotMinecraftGUI(Screen screen) {
        if (screen == null) {
            return false;
        } else if (screen instanceof DeathScreen) {
            return true;
        } else if (isModClass(screen.getClass())) {
            return true;
        }else if(isBadScreen(screen)){
            return true;
        }
        else {
            return !screen.getClass().getName().startsWith("net.minecraft");
        }
    }

    private static boolean isModClass(Class<?> clazz) {
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        if (codeSource != null && codeSource.getLocation() != null && !codeSource.getLocation().getFile().isEmpty()) {
            String s = codeSource.getLocation().getFile();
            if (s.contains("!")) {
                s = s.substring(0, s.lastIndexOf("!"));
            }

            if (s.contains("file:")) {
                s = s.substring(4);
            }

            File f = new File(s);
            return f.getParentFile().getName().equals("mods");
        } else {
            return false;
        }
    }

    private static boolean isBadScreen(Screen screen) {
        if (screen == null) {
            return false;
        } else {
            String[] allValidScreen = new String[]{"net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen", "net.minecraft.client.gui.screenss.AddServerScreen", "net.minecraft.client.gui.screens.AlertScreen", "net.minecraft.client.gui.screens.ChatOptionsScreen", "net.minecraft.client.gui.screens.ChatScreen", "net.minecraft.client.gui.screens.inventory.CommandBlockEditScree", "net.minecraft.client.gui.screens.ConfirmBackupScreen", "net.minecraft.client.gui.screens.ConfirmOpenLinkScreen", "net.minecraft.client.gui.screens.ConfirmScreen", "net.minecraft.client.gui.screens.ConnectingScreen", "net.minecraft.client.gui.screens.ControlsScreen", "net.minecraft.client.gui.screens.CreateBuffetWorldScreen", "net.minecraft.client.gui.screens.CreateFlatWorldScreen", "net.minecraft.client.gui.screens.CreateWorldScreen", "net.minecraft.client.gui.screens.CustomizeSkinScreen", "net.minecraft.client.gui.screens.DatapackFailureScreen", "net.minecraft.client.gui.screens.DemoScreen", "net.minecraft.client.gui.screens.DirtMessageScreen", "net.minecraft.client.gui.screens.DisconnectedScreen", "net.minecraft.client.gui.screens.DownloadTerrainScreen", "net.minecraft.client.gui.screens.EditBookScreen", "net.minecraft.client.gui.screens.EditGamerulesScreen", "net.minecraft.client.gui.screens.EditMinecartCommandBlockScreen", "net.minecraft.client.gui.screens.EditSignScreen", "net.minecraft.client.gui.screens.EditStructureScreen", "net.minecraft.client.gui.screens.EditWorldScreen", "net.minecraft.client.gui.screens.EnchantmentScreen", "net.minecraft.client.gui.screens.ErrorScreen", "net.minecraft.client.gui.screens.FlatPresetsScreen", "net.minecraft.client.gui.screens.GamemodeSelectionScreen", "net.minecraft.client.gui.screens.GPUWarningScreen", "net.minecraft.client.gui.screens.GrindstoneScreen", "net.minecraft.client.gui.screens.HopperScreen", "net.minecraft.client.gui.screens.IngameMenuScreen", "net.minecraft.client.gui.screens.JigsawScreen", "net.minecraft.client.gui.screens.LanguageScreen", "net.minecraft.client.gui.screens.LecternScreen", "net.minecraft.client.gui.screens.LoomScreen", "net.minecraft.client.gui.screens.MemoryErrorScreen", "net.minecraft.client.gui.screens.MouseSettingsScreen", "net.minecraft.client.gui.screens.MultiplayerScreen", "net.minecraft.client.gui.screens.MultiplayerWarningScreen", "net.minecraft.client.gui.screens.OptimizeWorldScreen", "net.minecraft.client.gui.screens.OptionsScreen", "net.minecraft.client.gui.screens.OptionsSoundsScreen", "net.minecraft.client.gui.screens.PackScreen", "net.minecraft.client.gui.screens.ReadBookScreen", "net.minecraft.client.gui.screens.ServerListScreen", "net.minecraft.client.gui.screens.SettingsScreen", "net.minecraft.client.gui.screens.ShareToLanScreen", "net.minecraft.client.gui.screens.SleepInMultiplayerScreen", "net.minecraft.client.gui.screens.StatsScreen", "net.minecraft.client.gui.screens.VideoSettingsScreen", "net.minecraft.client.gui.screens.WithNarratorSettingsScreen", "net.minecraft.client.gui.screens.WorkingScreen", "net.minecraft.client.gui.screens.WorldLoadProgressScreen", "net.minecraft.client.gui.screens.WorldOptionsScreen", "net.minecraft.client.gui.screens.WorldSelectionScreen", "net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen", "net.minecraft.client.gui.screens.inventory.AbstractRepairScreen", "net.minecraft.client.gui.screens.inventory.AnvilScreen", "net.minecraft.client.gui.screens.inventory.BeaconScreen", "net.minecraft.client.gui.screens.inventory.BlastFurnaceScreen", "net.minecraft.client.gui.screens.inventory.BrewingStandScreen", "net.minecraft.client.gui.screens.inventory.CartographyTableScreen", "net.minecraft.client.gui.screens.inventory.ChestScreen", "net.minecraft.client.gui.screens.inventory.ContainerScreen", "net.minecraft.client.gui.screens.inventory.CraftingScreen", "net.minecraft.client.gui.screens.inventory.CreativeScreen", "net.minecraft.client.gui.screens.inventory.DispenserScreen", "net.minecraft.client.gui.screens.inventory.FurnaceScreen", "net.minecraft.client.gui.screens.inventory.HorseInventoryScreen", "net.minecraft.client.gui.screens.inventory.InventoryScreen", "net.minecraft.client.gui.screens.inventory.MerchantScreen", "net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen", "net.minecraft.client.gui.screens.inventory.SmithingTableScreen", "net.minecraft.client.gui.screens.inventory.SmokerScreen", "net.minecraft.client.gui.screens.inventory.StonecutterScreen", "net.minecraft.client.gui.screens.inventory.InventoryScreen", "net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen", "net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen", "net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen", "net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen", "net.minecraft.client.gui.screens.inventory.EnchantmentScreen"};
            return !Arrays.asList(allValidScreen).contains(screen.getClass().getName());
        }
    }
}

