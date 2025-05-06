package com.csdy.jzyy.mixins;



//import com.csdy.jzyy.ms.util.Helper;
//import com.csdy.jzyy.ms.util.RestrictedUnsafeProxy;
//import net.minecraft.client.main.Main;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.lang.reflect.Field;
//
//// !!! 目标类和方法需要非常仔细地选择，确保在 Unsafe 被广泛使用前执行 !!!
//// 这个目标只是示例，你可能需要根据 Helfy API 的能力选择更早的注入点
//// 比如 Helfy 提供的某个早期初始化事件或服务加载器接口
//@Mixin(value = Main.class, remap = false, priority = 1) // remap=false 如果目标是 MC 代码
//// 或者 Mixin 到 Forge 的某个早期加载器类
//// @Mixin(targets = "net.minecraftforge.fml.loading.EarlyLoadingException", remap = false) // 只是一个猜测
//public class UnsafeHijackMixin {
//
//    private static final Logger LOGGER = LogManager.getLogger("YourModId UnsafeHijack");
//    private static boolean hijackAttempted = false; // 防止重复执行
//    private static boolean hijackSuccessful = false;
//
//    // 注入到静态初始化块的末尾
//    @Inject(method = "<clinit>", at = @At("TAIL"), require = 1)
//    private static void hijackUnsafeStaticInit(CallbackInfo ci) {
//        LOGGER.info("Mixin injecting into <clinit> TAIL of target class to hijack Unsafe.");
//        hijackGlobalUnsafe();
//    }
//
//    // 或者注入到 main 方法的开头 (如果目标是 Main 类)
//    // @Inject(method = "main", at = @At("HEAD"), require = 1)
//    // private static void hijackUnsafeInMain(String[] args, CallbackInfo ci) {
//    //     LOGGER.info("Mixin injecting into main HEAD of target class to hijack Unsafe.");
//    //     hijackGlobalUnsafe();
//    // }
//
//    // 这个方法应该被 Helfy 在极早期调用 (如果 Helfy 提供此类 Hook)
//    private static synchronized void executeHijackViaHelfyHook() {
//        LOGGER.info("Helfy early hook triggered. Attempting to hijack Unsafe.");
//        hijackGlobalUnsafe();
//    }
//
//
//    private static synchronized void hijackGlobalUnsafe() {
//        if (hijackAttempted) {
//            LOGGER.warn("Unsafe hijack already attempted (Success: {}). Skipping.", hijackSuccessful);
//            return;
//        }
//        hijackAttempted = true;
//        LOGGER.warn("!!! EXTREMELY DANGEROUS OPERATION COMMENCING: GLOBAL UNSAFE HIJACK !!!");
//        LOGGER.warn("!!! MOD COMPATIBILITY AND GAME STABILITY ARE NOT GUARANTEED !!!");
//
//        try {
//            // 1. 初始化 UnsafeUtils 获取真实 Unsafe 和字段
//            if (!Helper.initialize()) {
//                LOGGER.fatal("Failed to initialize UnsafeUtils. Unsafe hijacking aborted!");
//                return; // 无法继续
//            }
//            Object realUnsafe = Helper.getRealUnsafe();
//            Field unsafeField = Helper.getTheUnsafeField();
//            Class<?> unsafeClass = Helper.getUnsafeClass();
//
//            // 2. 创建代理实例 (将 "com.yourmod.id" 替换为你的 Mod 根包名)
//            // 确保你的 Helper 类在这个包名下，或者修改允许的包名
//            String yourModPackage = "com.web13234.jzyy"; // *** 修改为你的 Mod 包名 ***
//            RestrictedUnsafeProxy proxy = new RestrictedUnsafeProxy(realUnsafe, unsafeClass, yourModPackage);
//
//            // 3. 移除 final 修饰符 (如果需要)
//            if (!Helper.removeFinalModifier(unsafeField)) {
//                LOGGER.fatal("Failed to remove final modifier from Unsafe field '{}'. Hijacking aborted!", unsafeField.getName());
//                // 理论上可以尝试不移除 final 直接 set，但通常会失败
//                return;
//            }
//
//            // 4. **执行替换**
//            LOGGER.info("Attempting to replace static field '{}' with proxy instance: {}", unsafeField.getName(), proxy);
//            unsafeField.set(null, proxy); // 因为字段是 static, 第一个参数是 null
//
//            // 5. **验证替换**
//            Object currentUnsafe = unsafeField.get(null);
//            if (currentUnsafe == proxy) { // 检查对象实例是否匹配
//                hijackSuccessful = true;
//                LOGGER.info("***** Successfully hijacked global Unsafe instance! *****");
//                LOGGER.info("The '{}' field in {} now holds: {}", unsafeField.getName(), unsafeClass.getName(), currentUnsafe);
//                LOGGER.warn("Reminder: Calls to Unsafe methods not implemented in RestrictedUnsafeProxy will fail!");
//                LOGGER.warn("Ensure STACK_WALK_SKIP_FRAMES ({}) is correctly configured in RestrictedUnsafeProxy.", RestrictedUnsafeProxy.STACK_WALK_SKIP_FRAMES); // 显示配置的值
//            } else {
//                LOGGER.fatal("!!! CRITICAL FAILURE: Failed to hijack Unsafe !!!");
//                LOGGER.error("The field '{}' still holds: {}. Expected proxy: {}", unsafeField.getName(), currentUnsafe, proxy);
//                // 尝试恢复原始 Unsafe (尽力而为)
//                try {
//                    LOGGER.warn("Attempting to restore original Unsafe instance...");
//                    unsafeField.set(null, realUnsafe);
//                    Object restored = unsafeField.get(null);
//                    if (restored == realUnsafe) {
//                        LOGGER.info("Successfully restored original Unsafe instance.");
//                    } else {
//                        LOGGER.error("Failed to restore original Unsafe instance. Current value: {}", restored);
//                    }
//                } catch (Exception restoreEx) {
//                    LOGGER.error("Exception during attempt to restore original Unsafe instance.", restoreEx);
//                }
//                // 抛出异常可能导致游戏无法启动，但能明确指示失败
//                 throw new RuntimeException("Failed to hijack Unsafe instance!");
//            }
//
//        } catch (Throwable t) {
//            // 捕获所有可能的错误，防止 Mixin 失败导致游戏崩溃
//            LOGGER.fatal("Catastrophic uncaught exception during Unsafe hijack process!", t);
//            hijackSuccessful = false;
//            // 尝试记录更多信息
//            LOGGER.error("Hijack aborted due to unexpected error. Game stability may be compromised.");
//        }
//    }
//}
