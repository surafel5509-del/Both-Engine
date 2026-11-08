package com.bothengine.rendering;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * SceneRenderer
 *
 * Draws a small, self-contained OpenGL ES 2.0 scene: a lit, rotating cube plus a
 * gradient background. It exists so the engine always has something visible to
 * render on first launch instead of a cleared black surface, and so that a
 * black screen can never again be mistaken for a working build.
 *
 * The renderer is deliberately independent of the higher level scene graph:
 * no assets, no textures and no vertex buffers to load.
 */
public class SceneRenderer {
    private static final String TAG = "SceneRenderer";

    private static final String VERTEX_SHADER =
        "uniform mat4 uMvp;\n"
            + "uniform mat4 uModel;\n"
            + "attribute vec3 aPosition;\n"
            + "attribute vec3 aNormal;\n"
            + "varying vec3 vNormal;\n"
            + "varying vec3 vWorld;\n"
            + "void main() {\n"
            + "  vNormal = mat3(uModel) * aNormal;\n"
            + "  vWorld = (uModel * vec4(aPosition, 1.0)).xyz;\n"
            + "  gl_Position = uMvp * vec4(aPosition, 1.0);\n"
            + "}\n";

    private static final String FRAGMENT_SHADER =
        "precision mediump float;\n"
            + "varying vec3 vNormal;\n"
            + "varying vec3 vWorld;\n"
            + "uniform vec3 uLightDir;\n"
            + "uniform vec3 uColor;\n"
            + "void main() {\n"
            + "  vec3 n = normalize(vNormal);\n"
            + "  float diffuse = max(dot(n, normalize(uLightDir)), 0.0);\n"
            + "  // Cheap rim light so the silhouette reads against the background.\n"
            + "  vec3 viewDir = normalize(vec3(0.0, 0.0, 4.0) - vWorld);\n"
            + "  float rim = pow(1.0 - max(dot(n, viewDir), 0.0), 2.0);\n"
            + "  vec3 lit = uColor * (0.25 + 0.75 * diffuse) + vec3(0.15, 0.45, 0.95) * rim;\n"
            + "  gl_FragColor = vec4(lit, 1.0);\n"
            + "}\n";

    // Unit cube: 6 faces x (4 vertices x 6 floats position+normal) = 36 verts.
    private static final float[] CUBE = buildCube();

    private int program;
    private int aPosition;
    private int aNormal;
    private int uMvp;
    private int uModel;
    private int uLightDir;
    private int uColor;

    private FloatBuffer vertexBuffer;
    private int vertexCount;

    private final float[] model = new float[16];
    private final float[] view = new float[16];
    private final float[] projection = new float[16];
    private final float[] mvp = new float[16];

    private float angleDegrees;
    private float secondsRendered;
    private boolean ready;

    /** True once the shaders compiled and the geometry uploaded. */
    public boolean isReady() {
        return ready;
    }

    /**
     * Compile shaders and upload geometry. Must run on the GL thread.
     */
    public void initialize() {
        if (ready) {
            return;
        }

        program = linkProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (program == 0) {
            Log.e(TAG, "Shader program could not be linked");
            return;
        }

        aPosition = GLES20.glGetAttribLocation(program, "aPosition");
        aNormal = GLES20.glGetAttribLocation(program, "aNormal");
        uMvp = GLES20.glGetUniformLocation(program, "uMvp");
        uModel = GLES20.glGetUniformLocation(program, "uModel");
        uLightDir = GLES20.glGetUniformLocation(program, "uLightDir");
        uColor = GLES20.glGetUniformLocation(program, "uColor");

        vertexBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();
        vertexBuffer.put(CUBE);
        vertexBuffer.position(0);
        vertexCount = CUBE.length / 6;

        ready = true;
        Log.i(TAG, "Default scene ready (" + vertexCount + " vertices)");
    }

    /**
     * Draw one frame. Must run on the GL thread.
     *
     * @param deltaSeconds seconds since the previous frame
     * @param width        surface width in pixels
     * @param height       surface height in pixels
     */
    public void drawFrame(float deltaSeconds, int width, int height) {
        if (!ready) {
            return;
        }

        float w = width <= 0 ? 1 : width;
        float h = height <= 0 ? 1 : height;

        // Background: a vertical gradient so the frame is never pure black.
        GLES20.glClearColor(0.04f + 0.03f * (float) Math.sin(secondsRendered * 0.25f),
            0.06f, 0.14f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        secondsRendered += deltaSeconds;
        angleDegrees = (angleDegrees + deltaSeconds * 45.0f) % 360.0f;

        identity(model);
        rotateY(model, angleDegrees);
        rotateX(model, 20.0f);

        identity(view);
        translateZ(view, -4.5f);

        float aspect = w / h;
        perspective(projection, 60.0f, aspect, 0.1f, 100.0f);

        multiply(mvp, projection, view);
        multiplyInPlace(mvp, model);

        GLES20.glUseProgram(program);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glUniformMatrix4fv(uMvp, 1, false, mvp, 0);
        GLES20.glUniformMatrix4fv(uModel, 1, false, model, 0);
        GLES20.glUniform3f(uLightDir, 0.4f, 0.7f, 1.0f);
        GLES20.glUniform3f(uColor, 0.95f, 0.55f, 0.20f);

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 24, vertexBuffer);
        GLES20.glEnableVertexAttribArray(aPosition);

        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(aNormal, 3, GLES20.GL_FLOAT, false, 24, vertexBuffer);
        GLES20.glEnableVertexAttribArray(aNormal);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(aPosition);
        GLES20.glDisableVertexAttribArray(aNormal);
        GLES20.glUseProgram(0);
    }

    /** Release GL resources. Safe to call more than once. */
    public void release() {
        if (program != 0) {
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        ready = false;
    }


    // ----------------------------------------------------- matrix maths
    // Column-major 4x4 matrices, matching what glUniformMatrix4fv expects with
    // transpose = false.

    private static void identity(float[] m) {
        for (int i = 0; i < 16; i++) {
            m[i] = 0f;
        }
        m[0] = m[5] = m[10] = m[15] = 1f;
    }

    private static void translateZ(float[] m, float z) {
        m[14] += z;
    }

    private static void rotateY(float[] m, float degrees) {
        float r = (float) Math.toRadians(degrees);
        float c = (float) Math.cos(r);
        float s = (float) Math.sin(r);
        float[] out = new float[16];
        float[] rot = new float[16];
        identity(rot);
        rot[0] = c;
        rot[2] = -s;
        rot[8] = s;
        rot[10] = c;
        multiply(out, m, rot);
        System.arraycopy(out, 0, m, 0, 16);
    }

    private static void rotateX(float[] m, float degrees) {
        float r = (float) Math.toRadians(degrees);
        float c = (float) Math.cos(r);
        float s = (float) Math.sin(r);
        float[] out = new float[16];
        float[] rot = new float[16];
        identity(rot);
        rot[5] = c;
        rot[6] = s;
        rot[9] = -s;
        rot[10] = c;
        multiply(out, m, rot);
        System.arraycopy(out, 0, m, 0, 16);
    }

    private static void perspective(float[] m, float fovDegrees, float aspect,
                                    float near, float far) {
        for (int i = 0; i < 16; i++) {
            m[i] = 0f;
        }
        float f = 1.0f / (float) Math.tan(Math.toRadians(fovDegrees) / 2.0);
        float range = 1.0f / (near - far);
        m[0] = f / aspect;
        m[5] = f;
        m[10] = (far + near) * range;
        m[11] = -1f;
        m[14] = 2f * far * near * range;
    }

    private static void multiply(float[] out, float[] a, float[] b) {
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                float sum = 0f;
                for (int k = 0; k < 4; k++) {
                    sum += a[k * 4 + row] * b[col * 4 + k];
                }
                out[col * 4 + row] = sum;
            }
        }
    }

    private static void multiplyInPlace(float[] target, float[] b) {
        float[] out = new float[16];
        multiply(out, target, b);
        System.arraycopy(out, 0, target, 0, 16);
    }

    // ------------------------------------------------------------- internals

    private static int linkProgram(String vertexSource, String fragmentSource) {
        int vertex = compileShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertex == 0) {
            return 0;
        }
        int fragment = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragment == 0) {
            GLES20.glDeleteShader(vertex);
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program == 0) {
            GLES20.glDeleteShader(vertex);
            GLES20.glDeleteShader(fragment);
            return 0;
        }

        GLES20.glAttachShader(program, vertex);
        GLES20.glAttachShader(program, fragment);
        GLES20.glLinkProgram(program);

        int[] status = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Program link failed: " + GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }

        // The program keeps its own copy of the shaders once linked.
        GLES20.glDeleteShader(vertex);
        GLES20.glDeleteShader(fragment);
        return program;
    }

    private static int compileShader(int type, String source) {
        int shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            Log.e(TAG, "Could not create shader (type " + type + ")");
            return 0;
        }
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);

        int[] status = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Shader compile failed: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    /**
     * Six faces of a unit cube as interleaved position+normal triangles.
     */
    private static float[] buildCube() {
        float[][] faces = {
            // normal,        then 4 corners (CCW)
            {0, 0, 1},      // +Z
            {0, 0, -1},     // -Z
            {1, 0, 0},      // +X
            {-1, 0, 0},     // -X
            {0, 1, 0},      // +Y
            {0, -1, 0},     // -Y
        };

        float[][] corners = new float[6][12];
        // +Z
        corners[0] = new float[] {-1, -1, 1, 1, -1, 1, 1, 1, 1, -1, 1, 1};
        // -Z
        corners[1] = new float[] {1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1};
        // +X
        corners[2] = new float[] {1, -1, 1, 1, -1, -1, 1, 1, -1, 1, 1, 1};
        // -X
        corners[3] = new float[] {-1, -1, -1, -1, -1, 1, -1, 1, 1, -1, 1, -1};
        // +Y
        corners[4] = new float[] {-1, 1, 1, 1, 1, 1, 1, 1, -1, -1, 1, -1};
        // -Y
        corners[5] = new float[] {-1, -1, -1, 1, -1, -1, 1, -1, 1, -1, -1, 1};

        float[] data = new float[6 * 6 * 6];
        int i = 0;
        for (int face = 0; face < 6; face++) {
            float nx = faces[face][0];
            float ny = faces[face][1];
            float nz = faces[face][2];
            float[] c = corners[face];
            // Two triangles: (0,1,2) and (0,2,3).
            int[] order = {0, 1, 2, 0, 2, 3};
            for (int v : order) {
                data[i++] = c[v * 3];
                data[i++] = c[v * 3 + 1];
                data[i++] = c[v * 3 + 2];
                data[i++] = nx;
                data[i++] = ny;
                data[i++] = nz;
            }
        }
        return data;
    }
}
