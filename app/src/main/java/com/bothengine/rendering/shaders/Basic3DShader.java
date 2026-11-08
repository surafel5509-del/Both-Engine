package com.bothengine.rendering.shaders;

/**
 * Basic3DShader
 * Simple shader for 3D rendering with lighting
 */
public class Basic3DShader extends Shader {
    
    @Override
    protected String getVertexShaderSource() {
        return "#version 100\n" +
                "attribute vec3 aPosition;\n" +
                "attribute vec3 aNormal;\n" +
                "attribute vec2 aTexCoord;\n" +
                "attribute vec4 aColor;\n" +
                "\n" +
                "uniform mat4 uModelMatrix;\n" +
                "uniform mat4 uViewProjectionMatrix;\n" +
                "uniform mat3 uNormalMatrix;\n" +
                "\n" +
                "varying vec3 vNormal;\n" +
                "varying vec2 vTexCoord;\n" +
                "varying vec4 vColor;\n" +
                "varying vec3 vPosition;\n" +
                "\n" +
                "void main() {\n" +
                "    gl_Position = uViewProjectionMatrix * uModelMatrix * vec4(aPosition, 1.0);\n" +
                "    vNormal = uNormalMatrix * aNormal;\n" +
                "    vTexCoord = aTexCoord;\n" +
                "    vColor = aColor;\n" +
                "    vPosition = vec3(uModelMatrix * vec4(aPosition, 1.0));\n" +
                "}";
    }
    
    @Override
    protected String getFragmentShaderSource() {
        return "#version 100\n" +
                "precision mediump float;\n" +
                "\n" +
                "varying vec3 vNormal;\n" +
                "varying vec2 vTexCoord;\n" +
                "varying vec4 vColor;\n" +
                "varying vec3 vPosition;\n" +
                "\n" +
                "uniform sampler2D uTexture;\n" +
                "uniform bool uUseTexture;\n" +
                "uniform vec3 uLightDirection;\n" +
                "uniform vec3 uLightColor;\n" +
                "uniform vec3 uAmbientColor;\n" +
                "uniform bool uUseLighting;\n" +
                "\n" +
                "void main() {\n" +
                "    vec3 normal = normalize(vNormal);\n" +
                "    vec3 lightDir = normalize(uLightDirection);\n" +
                "    \n" +
                "    float diffuse = max(dot(normal, lightDir), 0.0);\n" +
                "    vec3 diffuseColor = diffuse * uLightColor;\n" +
                "    vec3 ambientColor = uAmbientColor;\n" +
                "    vec3 finalColor = ambientColor + diffuseColor;\n" +
                "    \n" +
                "    vec4 texColor = texture2D(uTexture, vTexCoord);\n" +
                "    vec4 baseColor = uUseTexture ? texColor : vColor;\n" +
                "    \n" +
                "    if (uUseLighting) {\n" +
                "        gl_FragColor = vec4(baseColor.rgb * finalColor, baseColor.a);\n" +
                "    } else {\n" +
                "        gl_FragColor = baseColor;\n" +
                "    }\n" +
                "}";
    }
}
