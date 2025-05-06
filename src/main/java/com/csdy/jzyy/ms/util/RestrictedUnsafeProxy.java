package com.csdy.jzyy.ms.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// 这个类不继承 Unsafe，它是一个独立的代理
public class RestrictedUnsafeProxy {

    private static final Logger LOGGER = LogManager.getLogger("YourModId RestrictedUnsafe");
    private final Object realUnsafe; // 使用 Object 兼容 sun.misc 和 jdk.internal
    private final Class<?> realUnsafeClass;
    private final String allowedPackagePrefix;
    private final MethodHandles.Lookup lookup = MethodHandles.lookup(); // 用于查找方法

    // 缓存 MethodHandle 比缓存 Method 性能可能更好，但更复杂
    private final Map<String, MethodHandle> handleCache = new ConcurrentHashMap<>();

    // !!! 关键：栈追踪跳过的帧数需要根据实际调用链精确调整 !!!
    // 至少需要跳过 RestrictedUnsafeProxy 的方法、StackWalker 的方法
    public static final int STACK_WALK_SKIP_FRAMES = 3; // 初始猜测值，需要调试确定

    public RestrictedUnsafeProxy(Object realUnsafeInstance, Class<?> unsafeClass, String allowedPackagePrefix) {
        if (realUnsafeInstance == null) throw new NullPointerException("Real Unsafe instance cannot be null");
        if (unsafeClass == null) throw new NullPointerException("Real Unsafe class cannot be null");
        if (allowedPackagePrefix == null || allowedPackagePrefix.isEmpty()) throw new IllegalArgumentException("Allowed package prefix cannot be empty");

        this.realUnsafe = realUnsafeInstance;
        this.realUnsafeClass = unsafeClass;
        // 确保包名以点结尾，以便进行前缀匹配
        this.allowedPackagePrefix = allowedPackagePrefix.endsWith(".") ? allowedPackagePrefix : allowedPackagePrefix + ".";
        LOGGER.info("RestrictedUnsafeProxy initialized. Wrapping {} instance. Allowing calls from packages starting with: {}", unsafeClass.getName(), this.allowedPackagePrefix);
    }

    // --- 核心检查逻辑 ---
    private boolean isCallerAllowed() {
        // 使用 StackWalker 获取调用者类
        // RETAIN_CLASS_REFERENCE 是必需的
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        Optional<String> callerClassNameOpt = walker.walk(frames ->
                frames.skip(STACK_WALK_SKIP_FRAMES) // *** 调整这里的数字 ***
                        .map(StackWalker.StackFrame::getClassName) // 获取类名字符串即可，避免加载类
                        .findFirst()
        );

        if (callerClassNameOpt.isPresent()) {
            String callerClassName = callerClassNameOpt.get();
            // 执行检查逻辑
            boolean allowed = callerClassName.startsWith(this.allowedPackagePrefix)
                    // || callerClassName.startsWith("net.minecraft.") // 可选：允许 MC 自身
                    // || callerClassName.startsWith("net.minecraftforge.") // 可选：允许 Forge
                    ;

            if (!allowed) {
                LOGGER.warn("Unsafe call blocked from: {}", callerClassName);
            } else {
                // 可以降级为 DEBUG 或 TRACE 级别，避免日志刷屏
                LOGGER.trace("Unsafe call allowed from: {}", callerClassName);
            }
            return allowed;
        } else {
            // 无法确定调用者（可能来自本机代码 JNI/JVMTI 或非常规调用）
            LOGGER.warn("Unsafe call blocked because caller could not be determined (native code or unexpected stack?).");
            return false; // 安全起见，阻止未知来源
        }
    }

    // --- 方法调用转发 ---
    // 使用 MethodHandle 实现调用转发
    private Object invokeRealUnsafe(String methodName, MethodType methodType, Object... args) {
        if (!isCallerAllowed()) {
            throw new SecurityException("Call to Unsafe." + methodName + " blocked from untrusted source.");
        }

        // 缓存 key: "methodName:returnType:paramType1:paramType2..."
        String cacheKey = methodName + ":" + methodType.toMethodDescriptorString();

        try {
            MethodHandle handle = handleCache.computeIfAbsent(cacheKey, key -> {
                try {
                    // 使用 lookup 查找 virtual 方法并绑定到真实实例
                    // 注意：Unsafe 的方法通常是 public，所以普通 lookup 应该可以
                    return lookup.findVirtual(realUnsafeClass, methodName, methodType).bindTo(realUnsafe);
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    LOGGER.error("Failed to find or access real Unsafe method handle for: {} {}", methodName, methodType, e);
                    // 返回一个总是抛出异常的 handle，避免重复查找失败
                    return MethodHandles.throwException(methodType.returnType(), RuntimeException.class)
                            .bindTo(new RuntimeException("Failed to obtain handle for Unsafe." + methodName));
                }
            });

            // 调用 MethodHandle
            // 使用 invokeWithArguments 比精确的 invoke 需要拆包，但更通用
            return handle.invokeWithArguments(args);

        } catch (Throwable t) {
            // MethodHandle.invokeExact 会抛出原始异常，invoke 和 invokeWithArguments 会包装
            // 这里我们捕获 Throwable 并包装成 RuntimeException，模仿一些 Unsafe 行为
            LOGGER.error("Error invoking real Unsafe method: {} via MethodHandle", methodName, t);
            throw new RuntimeException("Failed to invoke real Unsafe method " + methodName, t);
        }
    }

    // --- 实现 Unsafe 的公共 API ---
    // !!! 你必须为你需要拦截的所有 Unsafe 方法添加类似下面的代理实现 !!!
    // !!! 参考 sun.misc.Unsafe 或 jdk.internal.misc.Unsafe 的公共方法签名 !!!

    // 内存操作示例
    public long allocateMemory(long bytes) {
        MethodType mt = MethodType.methodType(long.class, long.class);
        return (long) invokeRealUnsafe("allocateMemory", mt, bytes);
    }

    public void freeMemory(long address) {
        MethodType mt = MethodType.methodType(void.class, long.class);
        invokeRealUnsafe("freeMemory", mt, address);
    }

    public void setMemory(Object o, long offset, long bytes, byte value) {
        MethodType mt = MethodType.methodType(void.class, Object.class, long.class, long.class, byte.class);
        invokeRealUnsafe("setMemory", mt, o, offset, bytes, value);
    }
    public void setMemory(long address, long bytes, byte value) {
        MethodType mt = MethodType.methodType(void.class, long.class, long.class, byte.class);
        invokeRealUnsafe("setMemory", mt, address, bytes, value);
    }


    // 字段访问示例
    public long objectFieldOffset(Field f) {
        MethodType mt = MethodType.methodType(long.class, Field.class);
        return (long) invokeRealUnsafe("objectFieldOffset", mt, f);
    }
    // 如果 jdk.internal.misc.Unsafe 的签名不同，需要适配
    public long objectFieldOffset(Class<?> c, String name) {
        // 假设 jdk.internal.misc.Unsafe 有这个方法
        if (realUnsafeClass.getName().startsWith("jdk.internal")) {
            MethodType mt = MethodType.methodType(long.class, Class.class, String.class);
            return (long) invokeRealUnsafe("objectFieldOffset", mt, c, name);
        } else {
            // sun.misc.Unsafe 没有这个签名，可能需要通过 Field 来获取
            try {
                return objectFieldOffset(c.getDeclaredField(name));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public int getInt(Object o, long offset) {
        MethodType mt = MethodType.methodType(int.class, Object.class, long.class);
        return (int) invokeRealUnsafe("getInt", mt, o, offset);
    }

    public void putInt(Object o, long offset, int x) {
        MethodType mt = MethodType.methodType(void.class, Object.class, long.class, int.class);
        invokeRealUnsafe("putInt", mt, o, offset, x);
    }

    public Object getObject(Object o, long offset) {
        MethodType mt = MethodType.methodType(Object.class, Object.class, long.class);
        return invokeRealUnsafe("getObject", mt, o, offset);
    }

    public void putObject(Object o, long offset, Object x) {
        MethodType mt = MethodType.methodType(void.class, Object.class, long.class, Object.class);
        invokeRealUnsafe("putObject", mt, o, offset, x);
    }


    // CAS 操作示例
    public final boolean compareAndSwapObject(Object o, long offset, Object expected, Object x) {
        MethodType mt = MethodType.methodType(boolean.class, Object.class, long.class, Object.class, Object.class);
        return (boolean) invokeRealUnsafe("compareAndSwapObject", mt, o, offset, expected, x);
    }

    public final boolean compareAndSwapInt(Object o, long offset, int expected, int x) {
        MethodType mt = MethodType.methodType(boolean.class, Object.class, long.class, int.class, int.class);
        return (boolean) invokeRealUnsafe("compareAndSwapInt", mt, o, offset, expected, x);
    }

    public final boolean compareAndSwapLong(Object o, long offset, long expected, long x) {
        MethodType mt = MethodType.methodType(boolean.class, Object.class, long.class, long.class, long.class);
        return (boolean) invokeRealUnsafe("compareAndSwapLong", mt, o, offset, expected, x);
    }

    // Volatile 操作示例
    public int getIntVolatile(Object o, long offset) {
        MethodType mt = MethodType.methodType(int.class, Object.class, long.class);
        return (int) invokeRealUnsafe("getIntVolatile", mt, o, offset);
    }
    public void putIntVolatile(Object o, long offset, int x) {
        MethodType mt = MethodType.methodType(void.class, Object.class, long.class, int.class);
        invokeRealUnsafe("putIntVolatile", mt, o, offset, x);
    }

    // 其他重要方法 (需要继续添加)
    // - getByte, putByte, getShort, putShort, getChar, putChar, getLong, putLong, getFloat, putFloat, getDouble, putDouble
    // - getAddress, putAddress
    // - staticFieldOffset, staticFieldBase, ensureClassInitialized, shouldBeInitialized
    // - defineClass, defineAnonymousClass
    // - allocateInstance
    // - monitorEnter, monitorExit, tryMonitorEnter
    // - throwException
    // - park, unpark
    // - loadFence, storeFence, fullFence
    // - getLoadAverage (虽然已弃用)
    // - invokeCleaner (Java 9+)
    // - ...以及 jdk.internal.misc.Unsafe 中可能存在的其他方法

    @Override
    public String toString() {
        return "RestrictedUnsafeProxy[wrapping=" + realUnsafeClass.getName() + "@" + Integer.toHexString(System.identityHashCode(realUnsafe)) + ", allowed=" + allowedPackagePrefix + "*]";
    }
}