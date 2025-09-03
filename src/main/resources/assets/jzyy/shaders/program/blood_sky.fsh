#version 440

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform mat4 InvProjMat;
uniform mat4 InvViewMat;
uniform float CycleTime;
uniform float Time;

in vec2 texCoord;

out vec4 fragColor;

vec3 hsv2rgb(vec3 hsv) {
    vec3 rgb = clamp(abs(mod(hsv.x * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
    return hsv.z * mix(vec3(1.0), rgb, hsv.y);
}

vec3 rgb2hsv(vec3 rgb) {
    vec4 k = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(rgb.bg, k.wz), vec4(rgb.gb, k.xy), step(rgb.b, rgb.g));
    vec4 q = mix(vec4(p.xyw, rgb.r), vec4(rgb.r, p.yzx), step(p.x, rgb.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

// 噪声函数
float hash(float n) { return fract(sin(n) * 1e4); }
float hash(vec2 p) { return fract(1e4 * sin(17.0 * p.x + p.y * 0.1) * (0.1 + abs(sin(p.y * 13.0 + p.x)))); }

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

// 脉动效果
float pulse(float x, float speed, float intensity) {
    return 0.5 + 0.5 * sin(x * speed) * intensity;
}

// 流动的血丝效果
vec3 bloodVeins(vec2 uv, float time) {
    float vein1 = sin(uv.x * 8.0 + uv.y * 4.0 + time * 0.5) * 0.5 + 0.5;
    float vein2 = cos(uv.x * 6.0 - uv.y * 3.0 + time * 0.3) * 0.5 + 0.5;
    float vein3 = sin(uv.y * 10.0 + time * 0.7) * 0.5 + 0.5;

    float veins = max(vein1, max(vein2, vein3));
    veins = smoothstep(0.3, 0.7, veins);

    return vec3(veins * 0.3);
}

bool isSky(float depth) {
    return depth > 0.9995;
}

void main() {
    vec4 originalColor = texture(DiffuseSampler, texCoord);
    float depth = texture(DepthSampler, texCoord).r;

    if (!isSky(depth)) {
        fragColor = originalColor;
        return;
    }

    // 动态效果参数
    float time = Time * 0.5;
    vec2 uv = texCoord * 2.0 - 1.0;
    float dist = length(uv);

    // 基础血色
    vec3 baseBlood = vec3(0.8, 0.1, 0.1);

    // 脉动效果 - 心脏跳动般的节奏
    float pulse1 = pulse(time, 2.0, 0.1);
    float pulse2 = pulse(time + 0.3, 3.0, 0.08);
    float overallPulse = 0.8 + pulse1 + pulse2;

    // 噪声扰动
    float noise1 = noise(uv * 3.0 + time * 0.1);
    float noise2 = noise(uv * 5.0 - time * 0.2);
    float noisePattern = mix(noise1, noise2, 0.5);

    // 漩涡效果
    float angle = atan(uv.y, uv.x);
    float radius = dist * 2.0;
    float swirl = sin(radius * 8.0 - time * 2.0 + angle * 4.0) * 0.3;

    // 血丝效果
    vec3 veins = bloodVeins(uv, time);

    // 颜色混合
    vec3 dynamicColor = baseBlood * overallPulse;
    dynamicColor += vec3(noisePattern * 0.2);
    dynamicColor += vec3(swirl * 0.15);
    dynamicColor += veins;

    // 边缘变暗效果
    float edgeDarken = 1.0 - smoothstep(0.7, 1.2, dist);
    dynamicColor *= edgeDarken;

    // 饱和度增强
    vec3 hsv = rgb2hsv(dynamicColor);
    hsv.y = min(hsv.y * 1.5, 0.95); // 提高饱和度
    hsv.z = hsv.z * 0.9; // 稍微降低亮度

    vec3 finalColor = hsv2rgb(hsv);

    // 保持原始alpha
    fragColor = vec4(finalColor, originalColor.a);
}