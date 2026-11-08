package com.bothengine.rendering.shaders;

/**
 * Basic2DShader
 * Simple shader for 2D rendering (sprites, UI, etc.)
 */
public class Basic2DShader extends Shader {
    
    @Override
    protected String getVertexShaderSource() {
        return "#version 100\n" +
                "attribute vec3 aPosition;\n" +
                "attribute vec2 aTexCoord;\n" +
                "attribute vec4 aColor;\n" +
                "\n" +
                "uniform mat4 uModelMatrix;\n" +
                "uniform mat4 uViewProjectionMatrix;\n" +
                "\n" +
                "varying vec2 vTexCoord;\n" +
                "varying vec4 vColor;\n" +
                "\n" +
                "void main() {\n" +
                "    gl_Position = uViewProjectionMatrix * uModelMatrix * vec4(aPosition, 1.0);\n" +
                "    vTexCoord = aTexCoord;\n" +
                "    vColor = aColor;\n" +
                "}";
    }
    
    @Override
    protected String getFragmentShaderSource() {
        return "#version 100\n" +
                "precision mediump float;\n" +
                "\n" +
                "varying vec2 vTexCoord;\n" +
                "varying vec4 vColor;\n" +
                "\n" +
                "uniform sampler2D uTexture;\n" +
                "uniform bool uUseTexture;\n" +
                "\n" +
                "void main() {\n" +
                "    vec4 texColor = texture2D(uTexture, vTexCoord);\n" +
                "    if (uUseTexture) {\n" +
                "        gl_FragColor = texColor * vColor;\n" +
                "    } else {\n" +
                "        gl_FragColor = vColor;\n" +
                "    }\n" +
                "}";
    }
}
