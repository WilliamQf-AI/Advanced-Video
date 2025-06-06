package com.netease.nmc.nertcsample.beauty.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by jerikc on 16/2/23.
 */
public class GlUtil {
    private static final String TAG = "GlUtil";
    /** Identity matrix for general use.  Don't modify or life will get weird. */

    public static final int NO_TEXTURE = -1;

    private static final int SIZEOF_FLOAT = 4;

    private GlUtil() { // do not instantiate
    }

    public static int createProgram(Context applicationContext, @RawRes int vertexSourceRawId,
                                    @RawRes int fragmentSourceRawId) {

        String vertexSource = readTextFromRawResource(applicationContext, vertexSourceRawId);
        String fragmentSource = readTextFromRawResource(applicationContext, fragmentSourceRawId);

        return createProgram(vertexSource, fragmentSource);
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }
        int program = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        if (program == 0) {
            Log.e(TAG, "Could not create program");
        }
        GLES20.glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        GLES20.glAttachShader(program, pixelShader);
        checkGlError("glAttachShader");
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        Log.i(TAG, "linkStatus:" + linkStatus[0]);
        return program;
    }

    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        checkGlError("glCreateShader type=" + shaderType);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader " + shaderType + ":");
            Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    public static int createTexture(int textureTarget, @Nullable Bitmap bitmap, int minFilter,
                                    int magFilter, int wrapS, int wrapT) {
        int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);
        GlUtil.checkGlError("glGenTextures");
        GLES20.glBindTexture(textureTarget, textureHandle[0]);
        GlUtil.checkGlError("glBindTexture " + textureHandle[0]);
        GLES20.glTexParameterf(textureTarget, GLES20.GL_TEXTURE_MIN_FILTER, minFilter);
        GLES20.glTexParameterf(textureTarget, GLES20.GL_TEXTURE_MAG_FILTER, magFilter); //线性插值
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_S, wrapS);
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_T, wrapT);

        if (bitmap != null) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }

        GlUtil.checkGlError("glTexParameter");
        return textureHandle[0];
    }

    public static int createTexture(int textureTarget) {
        return createTexture(textureTarget, null, GLES20.GL_LINEAR, GLES20.GL_LINEAR,
                GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
    }

    public static int createTexture(int textureTarget, Bitmap bitmap) {
        return createTexture(textureTarget, bitmap, GLES20.GL_LINEAR, GLES20.GL_LINEAR,
                GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
    }

    public static void initEffectTexture(int width, int height, int[] textureId, int type) {
        int len = textureId.length;
        if (len > 0) {
            GLES20.glGenTextures(len, textureId, 0);
        }
        for (int i = 0; i < len; i++) {
            ByteBuffer data = ByteBuffer.allocate(width * height * 4);
            GLES20.glBindTexture(type, textureId[i]);
            GLES20.glTexParameterf(type,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(type,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(type,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(type,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(type, 0, GLES20.GL_RGBA, width, height, 0,
                    GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
        }
    }
    /**
     * Checks to see if a GLES error has been raised.
     */
    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Log.e(TAG, msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * Allocates a direct float buffer, and populates it with the float array data.
     */
    public static FloatBuffer createFloatBuffer(float[] coords) {
        // Allocate a direct ByteBuffer, using 4 bytes per float, and copy coords into it.
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * SIZEOF_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(coords);
        fb.position(0);
        return fb;
    }

    public static String readTextFromRawResource(final Context applicationContext,
                                                 @RawRes final int resourceId) {
        final InputStream inputStream =
                applicationContext.getResources().openRawResource(resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String nextLine;
        final StringBuilder body = new StringBuilder();
        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            return null;
        }

        return body.toString();
    }

    private static int[] mDstFrameBuffer;
    private static int[] mSrcFrameBuffer;
    public static void copyTexture(int srcTexture, int dstTexture, int width, int height){
        if(mSrcFrameBuffer == null){
            mSrcFrameBuffer = new int[1];
            GLES20.glGenFramebuffers(1, mSrcFrameBuffer, 0);
        }

        if(mDstFrameBuffer == null){
            mDstFrameBuffer = new int[1];
            GLES20.glGenFramebuffers(1, mDstFrameBuffer, 0);
        }


        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, srcTexture);
        GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, mSrcFrameBuffer[0]);
        GLES30.glFramebufferTexture2D(GLES30.GL_READ_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, srcTexture, 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, dstTexture);
        GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, mDstFrameBuffer[0]);
        GLES30.glFramebufferTexture2D(GLES30.GL_DRAW_FRAMEBUFFER,
                GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, dstTexture, 0);

        GLES30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GLES30.GL_COLOR_BUFFER_BIT, GLES30.GL_LINEAR);
        GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, 0);
        GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }



}
