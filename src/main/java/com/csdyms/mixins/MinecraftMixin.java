package com.csdyms.mixins;

//@Mixin(value = {Minecraft.class},priority = Integer.MAX_VALUE)
//public class MinecraftMixin {
//    @Unique
//    private boolean $renderKill = false;
//    @Shadow
//    @Nullable
//    public ClientLevel level;
//    @Shadow
//    @Nullable
//    public LocalPlayer player;
//    @Shadow
//    public Window window;
//
//    @Shadow private static int fps;
//
//    @Inject(method = "updateTitle", at = @At(value = "HEAD"), cancellable = true)
//    public void updateTitle(CallbackInfo ci) {
//        if (this.player != null) {
//            this.window.setTitle("Minecraft 1.20.1 至爱之刃  Health:  " + Minecraft.getInstance().player.getHealth() + "  maxHealth:  " + Minecraft.getInstance().player.getMaxHealth() +"  FPS:  " +fps);
//        } else {
//            this.window.setTitle("Minecraft 1.20.1   至爱之刃  --  -- FPS: " + fps);
//        }
//        ci.cancel();
//    }
//
//    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onRenderTickEnd(F)V", shift = At.Shift.BEFORE, remap = false))
//    public void runTick(boolean p_91384_, CallbackInfo ci) {
//        if (this.player != null) {
//            if (DeadLists.isEntity(this.player)) {
//                ForgeGui forgeGui = new ForgeGui(Minecraft.getInstance());
//                BeloveUtils.replaceClass(forgeGui, BeloveForgeGui.class);
//            }
//            this.window.setTitle("Minecraft 1.20.1 至爱之刃  Health:  " + Minecraft.getInstance().player.getHealth() + "  maxHealth:  " + Minecraft.getInstance().player.getMaxHealth() +"  FPS:  " +fps);
//        } else {
//            this.window.setTitle("Minecraft 1.20.1   至爱之刃  --  -- FPS: " + fps);
//            if (GodList.isbelovePlayer(player)){
//                DefUtils.def(player);
//                if (Minecraft.getInstance().player != null) {
//                    BeloveUtils.replaceClass(Minecraft.getInstance().player, ReLocalPlayer.class);
//                }
//            }
//        }
//        BeloveUtils.replaceClass(MinecraftForge.EVENT_BUS, FuckEventBus.class);
//
//    }
//    @Inject(method = "run", at = @At(value = "HEAD"), cancellable = true)
//    public void run(CallbackInfo ci) {
//        if (this.player != null) {
//            if (DeadLists.isEntity(this.player)) {
//                ForgeGui forgeGui = new ForgeGui(Minecraft.getInstance());
//                BeloveUtils.replaceClass(forgeGui, BeloveForgeGui.class);
//            }
//            this.window.setTitle("Minecraft 1.20.1 至爱之刃  Health:  " + Minecraft.getInstance().player.getHealth() + "  maxHealth:  " + Minecraft.getInstance().player.getMaxHealth() +"  FPS:  " +fps);
//        } else {
//            this.window.setTitle("Minecraft 1.20.1   至爱之刃  --  -- FPS: " + fps);
//        }
//        BeloveUtils.replaceClass(MinecraftForge.EVENT_BUS, FuckEventBus.class);
//        if (GodList.isbelovePlayer(player)){
//            DefUtils.def(player);
//        }
//    }
//}
