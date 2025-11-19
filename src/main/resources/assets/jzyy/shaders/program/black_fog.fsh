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

// 脉动效果
float pulse(float x, float speed, float intensity) {
    return 0.5 + 0.5 * sin(x * speed) * intensity;
}

bool isSky(float depth) {
    return depth > 0.9995;
}

// 创建纯粹的黑雾效果 - 增加浓度
float createBlackFog(vec2 uv, float time) {
    float fog = 0.0;

    // 1. 基础环境雾层 - 增加强度
    float baseFog = fbm(uv * 0.4 + time * 0.01) * 0.6;

    // 2. 移动的雾团 - 增加强度
    vec2 movingCoord = uv * 1.8;
    movingCoord.x += sin(time * 0.2) * 0.5;
    movingCoord.y += cos(time * 0.18) * 0.4;
    float movingFog = fbm(movingCoord) * 0.7;

    // 3. 快速飘过的薄雾 - 增加强度
    float fastFog = fbm(uv * 2.5 + time * 0.5) * 0.5;

    // 4. 雾的呼吸脉动 - 增加强度
    float breath = pulse(time, 0.6, 0.3);

    // 5. 大范围雾浪 - 增加强度
    vec2 waveCoord = uv * 0.7 + vec2(time * 0.015, time * 0.012);
    float waveFog = fbm(waveCoord) * 0.5;

    // 6. 距离雾 - 屏幕边缘更浓
    float distFog = length(uv - vec2(0.5)) * 1.2;

    // 组合所有雾层 - 增加总体强度
    fog = baseFog + movingFog * 0.8 + fastFog * 0.6 + breath * 0.25 + waveFog + distFog * 0.3;

    // 雾浓度控制 - 调整范围让雾更浓
    fog = smoothstep(0.2, 0.9, fog);

    return fog;
}

void main() {
    vec4 originalColor = texture(DiffuseSampler, texCoord);
    float depth = texture(DepthSampler, texCoord).r;

    // 只对天空部分应用黑雾效果
    if (isSky(depth)) {
        // 计算黑雾强度 - 增加混合强度
        float fogIntensity = createBlackFog(texCoord, Time * 0.4);

        // 纯黑色雾颜色
        vec3 blackFogColor = vec3(0.0);

        // 应用雾效 - 增加混合系数到0.8让雾更浓
        vec3 finalColor = mix(originalColor.rgb, blackFogColor, fogIntensity * 0.8);
        fragColor = vec4(finalColor, originalColor.a);
    } else {
        // 非天空部分保持原样
        fragColor = originalColor;
    }
}