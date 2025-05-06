package com.csdy.jzyy.ms.util;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.ModuleLayerHandler;
import cpw.mods.modlauncher.api.NamedPath;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import net.minecraft.SharedConstants;
import net.minecraftforge.fml.loading.ModDirTransformerDiscoverer;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.module.ResolvedModule;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class Helper {
    public static final Unsafe UNSAFE;
    private static final Lookup lookup;
    private static final Object internalUNSAFE;
    private static MethodHandle objectFieldOffsetInternal;

    static {
        UNSAFE = getUnsafe();
        lookup = getFieldValue(Lookup.class, "IMPL_LOOKUP", Lookup.class);
        internalUNSAFE = getInternalUNSAFE();
        try {
            Class<?> internalUNSAFEClass = lookup.findClass("jdk.internal.misc.Unsafe");
            objectFieldOffsetInternal = lookup.findVirtual(internalUNSAFEClass, "objectFieldOffset", MethodType.methodType(long.class, Field.class)).bindTo(internalUNSAFE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Unsafe getUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object getInternalUNSAFE() {
        try {
            Class<?> clazz = lookup.findClass("jdk.internal.misc.Unsafe");
            return lookup.findStatic(clazz, "getUnsafe", MethodType.methodType(clazz)).invoke();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Field f, Object target, Class<T> clazz) {
        try {
            long offset;
            if (Modifier.isStatic(f.getModifiers())) {
                target = UNSAFE.staticFieldBase(f);
                offset = UNSAFE.staticFieldOffset(f);
            } else offset = objectFieldOffset(f);
            return (T) UNSAFE.getObject(target, offset);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long objectFieldOffset(Field f) {
        try {
            return UNSAFE.objectFieldOffset(f);
        } catch (Throwable e) {
            try {
                return (long) objectFieldOffsetInternal.invoke(f);
            } catch (Throwable t1) {
                t1.printStackTrace();
            }
        }
        return 0L;
    }

    public static <T> T getFieldValue(Object target, String fieldName, Class<T> clazz) {
        try {
            return getFieldValue(target.getClass().getDeclaredField(fieldName), target, clazz);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getFieldValue(Class<?> target, String fieldName, Class<T> clazz) {
        try {
            return getFieldValue(target.getDeclaredField(fieldName), (Object) null, clazz);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setFieldValue(Object target, String fieldName, Object value) {
        try {
            setFieldValue(target.getClass().getDeclaredField(fieldName), target, value);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setFieldValue(Field f, Object target, Object value) {
        try {
            long offset;
            if (Modifier.isStatic(f.getModifiers())) {
                target = UNSAFE.staticFieldBase(f);
                offset = UNSAFE.staticFieldOffset(f);
            } else offset = objectFieldOffset(f);
            UNSAFE.putObject(target, offset, value);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String getJarPath(Class<?> clazz) {
        String file = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (!file.isEmpty()) {
            if (file.startsWith("union:"))
                file = file.substring(6);
            if (file.startsWith("/"))
                file = file.substring(1);
            file = file.substring(0, file.lastIndexOf(".jar") + 4);
            file = file.replaceAll("/", "\\\\");
        }
        return URLDecoder.decode(file, StandardCharsets.UTF_8);
    }

    @SuppressWarnings({"ConstantConditions", "unchecked", "rawtypes"})
    public static void coexistenceCoreAndMod() {
        List<NamedPath> found = Helper.getFieldValue(ModDirTransformerDiscoverer.class, "found", List.class);
        found.removeIf(namedPath -> Helper.getJarPath(Helper.class).equals(namedPath.paths()[0].toString()));

        Helper.getFieldValue(Helper.getFieldValue(Launcher.INSTANCE, "moduleLayerHandler", ModuleLayerHandler.class), "completedLayers", EnumMap.class).values().forEach(layerInfo -> {
            ModuleLayer layer = Helper.getFieldValue(layerInfo, "layer", ModuleLayer.class);

            layer.modules().forEach(module -> {
                if (module.getName().equals(Helper.class.getModule().getName())) {
                    Set<ResolvedModule> modules = new HashSet<>(Helper.getFieldValue(layer.configuration(), "modules", Set.class));
                    Map<String, ResolvedModule> nameToModule = new HashMap(Helper.getFieldValue(layer.configuration(), "nameToModule", Map.class));

                    modules.remove(nameToModule.remove(Helper.class.getModule().getName()));

                    Helper.setFieldValue(layer.configuration(), "modules", modules);
                    Helper.setFieldValue(layer.configuration(), "nameToModule", nameToModule);
                }
            });
        });
    }

    public static void copyProperties(Class<?> clazz, Object source, Object target) {
        try {
            Field[] fields = clazz.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);

            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.set(target, field.get(source));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setClass(Object object, Class<?> targetClass) {
        if (object == null)
            throw new NullPointerException("object null");
        if (targetClass == null)
            throw new NullPointerException("targetClass null");
        try {
            lookup.ensureInitialized(object.getClass());
            lookup.ensureInitialized(targetClass);
            int of = SnowUnsafeAccess.UNSAFE.getIntVolatile(SnowUnsafeAccess.UNSAFE.allocateInstance(targetClass), SnowUnsafeAccess.UNSAFE.addressSize());
            SnowUnsafeAccess.UNSAFE.putIntVolatile(object, SnowUnsafeAccess.UNSAFE.addressSize(), of);
        } catch (InstantiationException|IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void replaceClass(Object object, Class<?> targetClass) {
        if (object == null)
            throw new NullPointerException("object==null");
        if (targetClass == null)
            throw new NullPointerException("targetClass==null");
        try {
            int klass_ptr = UnsafeAccess.UNSAFE.getIntVolatile(UnsafeAccess.UNSAFE.allocateInstance(targetClass), 8L);
            UnsafeAccess.UNSAFE.putIntVolatile(object, 8L, klass_ptr);
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T, E> void fieldSetField(T instance, Class<? super T> cls, String fieldName, E val, String srg) {
        String[] remap = new String[]{srg, fieldName};
        String name = SharedConstants.IS_RUNNING_IN_IDE ? remap[1] : remap[0];
        try {
            ObfuscationReflectionHelper.setPrivateValue(cls, instance, val, name);
        } catch (Exception ignored) {
        }
    }

    public static <E> Object getField(E instance, Class<? super E> cls, String fieldName, String srg) {
        String[] remap = new String[]{srg, fieldName};
        String name = SharedConstants.IS_RUNNING_IN_IDE ? remap[1] : remap[0];
        return ObfuscationReflectionHelper.getPrivateValue(cls, instance, name);
    }

    public static Object fieldMethod(Object instance, Class<?> cls, @NotNull String fieldName, Object[] objects, String srg, Class<?>... classes) {
        String name = SharedConstants.IS_RUNNING_IN_IDE ? fieldName : srg;
        try {
            return ObfuscationReflectionHelper.findMethod(cls, name, classes).invoke(instance, objects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // We don't want loopback or non-running interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // We only want IPv4 addresses
                    if (addr instanceof java.net.Inet4Address)
                        return addr.getHostAddress();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final Logger LOGGER = LogManager.getLogger("Jzyy UnsafeUtils");
    private static Object REAL_UNSAFE_INSTANCE = null; // 使用 Object 以兼容 sun.misc 和 jdk.internal
    private static Field THE_UNSAFE_FIELD = null;
    private static Class<?> UNSAFE_CLASS = null; // 存储找到的 Unsafe 类

    // 尝试在静态初始化块中尽早获取，但这依赖类加载顺序
    // 更好的方式是在 Helfy 提供的更早的 Hook 中调用 initialize()
    // static { initialize(); } // 不推荐在此处调用

    public static synchronized boolean initialize() {
        if (REAL_UNSAFE_INSTANCE != null) {
            return true; // 已经初始化
        }
        LOGGER.info("Attempting to locate and initialize Unsafe...");
        try {
            try {
                // 优先尝试 sun.misc.Unsafe (常见于 Java 8, 11, 17 的 Forge 环境)
                UNSAFE_CLASS = Class.forName("sun.misc.Unsafe");
                THE_UNSAFE_FIELD = UNSAFE_CLASS.getDeclaredField("theUnsafe");
                LOGGER.debug("Found sun.misc.Unsafe and 'theUnsafe' field.");
            } catch (ClassNotFoundException | NoSuchFieldException e) {
                LOGGER.warn("sun.misc.Unsafe or 'theUnsafe' field not found, trying jdk.internal.misc.Unsafe.", e);
                try {
                    // 备选：jdk.internal.misc.Unsafe (Java 9+)
                    // 需要 --add-opens=java.base/jdk.internal.misc=ALL-UNNAMED
                    // 或者 Helfy/Mixin 可能已经处理了访问性
                    UNSAFE_CLASS = Class.forName("jdk.internal.misc.Unsafe");
                    // 在 Java 9+ 中，获取实例的方式可能是 getUnsafe() 静态方法
                    // 但 'theUnsafe' 字段通常仍然存在以兼容旧代码，我们优先尝试字段
                    try {
                        THE_UNSAFE_FIELD = UNSAFE_CLASS.getDeclaredField("theUnsafe");
                        LOGGER.debug("Found jdk.internal.misc.Unsafe and 'theUnsafe' field.");
                    } catch (NoSuchFieldException e2) {
                        LOGGER.warn("'theUnsafe' field not found in jdk.internal.misc.Unsafe, will try getUnsafe() method later if needed.", e2);
                        // 如果字段不存在，后面 Hijack 逻辑需要调整为修改 getUnsafe() 方法或其返回逻辑
                        // 但目前常见的 Unsafe Hijack 还是针对 aheUnsafe 字段
                        throw new RuntimeException("Cannot find a static field to hold the Unsafe instance.", e2);
                    }
                } catch (Exception e2) {
                    LOGGER.error("Failed to find Unsafe class (tried sun.misc and jdk.internal.misc). Unsafe hijacking cannot proceed.", e2);
                    return false; // 明确表示失败
                }
            }

            THE_UNSAFE_FIELD.setAccessible(true);
            REAL_UNSAFE_INSTANCE = THE_UNSAFE_FIELD.get(null); // 获取原始实例

            if (REAL_UNSAFE_INSTANCE == null) {
                LOGGER.error("The 'theUnsafe' field is null! Cannot proceed.");
                return false;
            }

            LOGGER.info("Successfully retrieved the original Unsafe instance: {} (from class {})", REAL_UNSAFE_INSTANCE, UNSAFE_CLASS.getName());
            return true;

        } catch (Exception e) { // Catch broader exceptions during initialization
            LOGGER.error("An unexpected error occurred while initializing UnsafeUtils", e);
            // 清理状态，防止后续方法误用
            REAL_UNSAFE_INSTANCE = null;
            THE_UNSAFE_FIELD = null;
            UNSAFE_CLASS = null;
            return false;
        }
    }

    public static Object getRealUnsafe() {
        if (REAL_UNSAFE_INSTANCE == null) {
            throw new IllegalStateException("Unsafe has not been initialized or initialization failed. Call initialize() first in an early hook.");
        }
        return REAL_UNSAFE_INSTANCE;
    }

    public static Field getTheUnsafeField() {
        if (THE_UNSAFE_FIELD == null) {
            throw new IllegalStateException("Unsafe field has not been initialized or initialization failed. Call initialize() first.");
        }
        return THE_UNSAFE_FIELD;
    }

    public static Class<?> getUnsafeClass() {
        if (UNSAFE_CLASS == null) {
            throw new IllegalStateException("Unsafe class has not been initialized or initialization failed. Call initialize() first.");
        }
        return UNSAFE_CLASS;
    }

    // 工具方法：移除 final 修饰符 (极度危险)
    public static boolean removeFinalModifier(Field field) {
        if (!Modifier.isFinal(field.getModifiers())) {
            LOGGER.debug("Field '{}' is not final, no need to remove modifier.", field.getName());
            return true; // 已经是 non-final
        }
        LOGGER.warn("Attempting to remove final modifier from field: {}", field.getName());
        try {
            // 获取 Field 类中的 'modifiers' 字段
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            // 计算新的修饰符值 (去掉 final 位)
            int originalModifiers = field.getModifiers();
            int newModifiers = originalModifiers & ~Modifier.FINAL;
            // 将新的修饰符值写入字段对象
            modifiersField.setInt(field, newModifiers);
            LOGGER.info("Successfully removed final modifier from field: {}. New modifiers: {}", field.getName(), Modifier.toString(field.getModifiers()));
            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("CRITICAL: Failed to remove final modifier from field: {}. Hijacking might fail.", field.getName(), e);
            return false;
        } catch (Exception e) {
            LOGGER.error("CRITICAL: Unexpected error removing final modifier from field: {}.", field.getName(), e);
            return false;
        }
    }


}
