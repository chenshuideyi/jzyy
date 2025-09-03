#version 440

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform mat4 ProjMat;
uniform mat4 InvProjMat;
uniform mat4 InvViewMat;
uniform float CycleTime;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    // 获取原始颜色
    vec4 originalColor = texture(DiffuseSampler, texCoord);
    float depth = texture(DepthSampler, texCoord).r;

    // 更强的深度影响 - 远处更加朦胧
    float depthFactor = clamp(depth * 1.5, 0.1, 0.9);

    // 更强的边缘暗角效果
    vec2 centeredCoord = (texCoord - 0.5) * 1.2;
    float vignette = 1.0 - smoothstep(0.3, 0.7, length(centeredCoord));
    vignette = mix(0.3, 1.0, vignette); // 边缘更暗

    // 更深的色调 (接近黑色但带有细微色彩)
    vec3 darkTint = vec3(0.03, 0.04, 0.06);

    // 更强的混合效果 - 85% 混合到深色
    vec3 finalColor = mix(originalColor.rgb, darkTint, 0.85);

    // 应用更强的深度和暗角效果
    finalColor *= depthFactor * vignette * 0.6;

    // 限制最小亮度，避免完全黑色
    finalColor = max(finalColor, vec3(0.01));

    // 添加更明显的噪声纹理
    float noise = fract(sin(dot(texCoord, vec2(127.1, 311.7))) * 43758.5453);
    finalColor += noise * 0.015;

    // 整体亮度降低
    finalColor *= 0.4;

    fragColor = vec4(finalColor, originalColor.a);
}