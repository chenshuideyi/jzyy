#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform mat4 InvProjMat;
uniform mat4 InvViewMat;
uniform float CycleTime;
uniform float Time;

in vec2 texCoord;

out vec4 fragColor;

// 噪声函数
float hash(float n) {
    return fract(sin(n) * 1e4);
}

float hash(vec2 p) {
    return fract(1e4 * sin(17.0 * p.x + p.y * 0.1) * (0.1 + abs(sin(p.y * 13.0 + p.x))));
}

float noise(vec2 x) {
    vec2 i = floor(x);
    vec2 f = fract(x);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

float fbm(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;
    float frequency = 1.0;

    for (int i = 0; i < 5; i++) {
        value += amplitude * noise(p * frequency);
        amplitude *= 0.5;
        frequency *= 2.0;
    }
    return value;
}

// 呼吸效果 - 多个频率叠加
float breathingEffect(float time) {
    // 主呼吸节奏 - 缓慢的深呼吸
    float mainBreath = sin(time * 0.3) * 0.5 + 0.5;

    // 次要呼吸 - 较快的呼吸
    float secondaryBreath = sin(time * 1.2 + 1.0) * 0.3 + 0.3;

    // 微颤动 - 增加真实感
    float microTremor = sin(time * 4.5 + 2.0) * 0.15 + 0.15;

    return (mainBreath + secondaryBreath + microTremor) * 0.5;
}

// 漩涡缠绕效果
float swirlEffect(vec2 uv, float time) {
    vec2 center = vec2(0.5, 0.6); // 漩涡中心稍微偏上
    vec2 pos = uv - center;

    // 角度和距离计算
    float angle = atan(pos.y, pos.x);
    float radius = length(pos) * 2.0;

    // 多层漩涡叠加
    float swirl1 = sin(radius * 6.0 - time * 1.5 + angle * 3.0) * 0.4;
    float swirl2 = cos(radius * 4.0 + time * 2.0 - angle * 2.0) * 0.3;
    float swirl3 = sin(radius * 8.0 + time * 0.8 + angle * 5.0) * 0.2;

    // 距离衰减
    float distanceAttenuation = 1.0 - smoothstep(0.3, 1.0, radius);

    return (swirl1 + swirl2 + swirl3) * distanceAttenuation;
}

// 雾浪效果 - 模拟一阵阵的雾气
float fogWaves(vec2 uv, float time) {
    // 水平移动的雾浪
    float wave1 = sin(uv.x * 8.0 + time * 0.8) * 0.6;
    float wave2 = cos(uv.x * 5.0 - time * 1.2 + uv.y * 3.0) * 0.4;
    float wave3 = sin(uv.x * 12.0 + time * 0.5 - uv.y * 2.0) * 0.3;

    // 垂直移动的雾浪
    float verticalWave = sin(uv.y * 6.0 + time * 0.4) * 0.5;

    // 组合波浪
    float waves = (wave1 + wave2 + wave3 + verticalWave) * 0.5;

    // 波浪强度随高度变化
    float heightFactor = 1.0 - uv.y; // 天空顶部波浪较弱

    return waves * heightFactor;
}

// 脉动雾团
float pulsatingClouds(vec2 uv, float time) {
    vec2 cloudCoord1 = uv * 2.0 + vec2(time * 0.1, time * 0.05);
    vec2 cloudCoord2 = uv * 3.0 - vec2(time * 0.08, time * 0.12);

    float cloud1 = fbm(cloudCoord1);
    float cloud2 = fbm(cloudCoord2);

    // 雾团的脉动
    float cloudPulse1 = sin(time * 0.7) * 0.3 + 0.7;
    float cloudPulse2 = cos(time * 1.1 + 2.0) * 0.2 + 0.8;

    return (cloud1 * cloudPulse1 + cloud2 * cloudPulse2) * 0.6;
}

float createBlackFog(vec2 uv, float time) {
    float fog = 0.0;

    // 基础环境雾
    float baseFog = fbm(uv * 0.4 + time * 0.01) * 0.6;

    // 移动的雾团
    vec2 movingCoord = uv * 1.8;
    movingCoord.x += sin(time * 0.2) * 0.5;
    movingCoord.y += cos(time * 0.18) * 0.4;
    float movingFog = fbm(movingCoord) * 0.7;

    // 快速飘过的薄雾
    float fastFog = fbm(uv * 2.5 + time * 0.5) * 0.5;

    // 呼吸效果
    float breath = breathingEffect(time) * 0.4;

    // 漩涡缠绕效果
    float swirl = swirlEffect(uv, time) * 0.6;

    // 雾浪效果
    float waves = fogWaves(uv, time) * 0.5;

    // 脉动雾团
    float clouds = pulsatingClouds(uv, time);

    // 距离雾
    float distFog = length(uv - vec2(0.5)) * 1.2;

    // 组合所有效果 - 呼吸和漩涡占主导
    fog = baseFog + movingFog * 0.6 + fastFog * 0.4 + breath + swirl + waves + clouds * 0.3 + distFog * 0.3;

    // 动态浓度控制 - 随呼吸变化
    float dynamicIntensity = 0.7 + breathingEffect(time) * 0.3;
    fog = smoothstep(0.2 * dynamicIntensity, 0.9, fog);

    return fog;
}

bool isSky(float depth, vec3 color) {
    return depth > 0.999;
}

void main() {
    vec4 originalColor = texture(DiffuseSampler, texCoord);
    float depth = texture(DepthSampler, texCoord).r;

    if (isSky(depth, originalColor.rgb)) {
        float fogIntensity = createBlackFog(texCoord, Time * 0.8);
        vec3 blackFogColor = vec3(0.0);

        // 最终颜色混合 - 保留一些原始天空颜色的痕迹
        vec3 finalColor = mix(originalColor.rgb, blackFogColor, fogIntensity * 0.95);

        fragColor = vec4(finalColor, originalColor.a);
    } else {
        fragColor = originalColor;
    }
}