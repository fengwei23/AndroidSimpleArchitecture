package com.sunplus.screenrecorder.media.glutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.support.annotation.NonNull;
import android.util.Log;
import com.sunplus.toolbox.utils.AssetsHelper;
import com.sunplus.toolbox.utils.BuildCheck;
import java.io.IOException;

/**
 * Created by w.feng on 2018/10/11
 * Email: fengweisb@gmail.com
 */
public class GLHelper {
  private static final String TAG = "GLHelper";

  public static void checkGlError(final String op) {
    final int error = GLES20.glGetError();
    if (error != GLES20.GL_NO_ERROR) {
      final String msg = op + ": glError 0x" + Integer.toHexString(error);
      Log.e(TAG, msg);
      new Throwable(msg).printStackTrace();
    }
  }

  public static int initTex(final int texTarget, final int filterParam) {
    return initTex(texTarget, GLES20.GL_TEXTURE0,
        filterParam, filterParam, GLES20.GL_CLAMP_TO_EDGE);
  }

  public static int initTex(final int texTarget, final int texUnit,
                            final int minFilter, final int magFilter, final int wrap) {

    final int[] tex = new int[1];
    GLES20.glActiveTexture(texUnit);
    GLES20.glGenTextures(1, tex, 0);
    GLES20.glBindTexture(texTarget, tex[0]);
    GLES20.glTexParameteri(texTarget, GLES20.GL_TEXTURE_WRAP_S, wrap);
    GLES20.glTexParameteri(texTarget, GLES20.GL_TEXTURE_WRAP_T, wrap);
    GLES20.glTexParameteri(texTarget, GLES20.GL_TEXTURE_MIN_FILTER, minFilter);
    GLES20.glTexParameteri(texTarget, GLES20.GL_TEXTURE_MAG_FILTER, magFilter);
    return tex[0];
  }

  public static int[] initTexes(final int n,
                                final int texTarget, final int filterParam) {

    return initTexes(new int[n], texTarget,
        filterParam, filterParam, GLES20.GL_CLAMP_TO_EDGE);
  }

  public static int[] initTexes(@NonNull final int[] texIds,
                                final int texTarget, final int filterParam) {

    return initTexes(texIds, texTarget,
        filterParam, filterParam, GLES20.GL_CLAMP_TO_EDGE);
  }

  public static int[] initTexes(final int n,
                                final int texTarget, final int minFilter, final int magFilter,
                                final int wrap) {

    return initTexes(new int[n], texTarget, minFilter, magFilter, wrap);
  }

  public static int[] initTexes(@NonNull final int[] texIds,
                                final int texTarget, final int minFilter, final int magFilter,
                                final int wrap) {

    int[] textureUnits = new int[1];
    GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_IMAGE_UNITS, textureUnits, 0);
    Log.v(TAG, "GL_MAX_TEXTURE_IMAGE_UNITS=" + textureUnits[0]);
    final int n = texIds.length > textureUnits[0]
        ? textureUnits[0] : texIds.length;
    for (int i = 0; i < n; i++) {
      texIds[i] = GLHelper.initTex(texTarget, ShaderConst.TEX_NUMBERS[i],
          minFilter, magFilter, wrap);
    }
    return texIds;
  }

  public static int[] initTexes(final int n,
                                final int texTarget, final int texUnit,
                                final int minFilter, final int magFilter, final int wrap) {

    return initTexes(new int[n], texTarget, texUnit,
        minFilter, magFilter, wrap);
  }

  public static int[] initTexes(@NonNull final int[] texIds,
                                final int texTarget, final int texUnit, final int filterParam) {

    return initTexes(texIds, texTarget, texUnit,
        filterParam, filterParam, GLES20.GL_CLAMP_TO_EDGE);
  }

  public static int[] initTexes(@NonNull final int[] texIds,
                                final int texTarget, final int texUnit,
                                final int minFilter, final int magFilter, final int wrap) {

    int[] textureUnits = new int[1];
    GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_IMAGE_UNITS, textureUnits, 0);
    final int n = texIds.length > textureUnits[0]
        ? textureUnits[0] : texIds.length;
    for (int i = 0; i < n; i++) {
      texIds[i] = GLHelper.initTex(texTarget, texUnit,
          minFilter, magFilter, wrap);
    }
    return texIds;
  }

  public static void deleteTex(final int hTex) {
    final int[] tex = new int[] { hTex };
    GLES20.glDeleteTextures(1, tex, 0);
  }

  public static void deleteTex(@NonNull final int[] tex) {
    GLES20.glDeleteTextures(tex.length, tex, 0);
  }

  public static int loadTextureFromResource(final Context context, final int resId) {
    return loadTextureFromResource(context, resId, null);
  }

  @SuppressLint("NewApi")
  @SuppressWarnings("deprecation")
  public static int loadTextureFromResource(final Context context, final int resId,
                                            final Resources.Theme theme) {
    // Create an empty, mutable bitmap
    final Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
    // get a canvas to paint over the bitmap
    final Canvas canvas = new Canvas(bitmap);
    canvas.drawARGB(0, 0, 255, 0);

    // get a background image from resources
    // note the image format must match the bitmap format
    final Drawable background;
    if (BuildCheck.isAndroid5()) {
      background = context.getResources().getDrawable(resId, theme);
    } else {
      background = context.getResources().getDrawable(resId);
    }
    background.setBounds(0, 0, 256, 256);
    background.draw(canvas);

    final int[] textures = new int[1];

    //Generate one texture pointer...
    GLES20.glGenTextures(1, textures, 0);
    //...and bind it to our array
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

    //Create Nearest Filtered Texture
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

    //Different possible texture parameters, e.g. GLES20.GL_CLAMP_TO_EDGE
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
        GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
        GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

    //Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
    //Clean up
    bitmap.recycle();

    return textures[0];
  }

  public static int createTextureWithTextContent(final String text) {
    // Create an empty, mutable bitmap
    final Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
    // get a canvas to paint over the bitmap
    final Canvas canvas = new Canvas(bitmap);
    canvas.drawARGB(0, 0, 255, 0);

    // Draw the text
    final Paint textPaint = new Paint();
    textPaint.setTextSize(32);
    textPaint.setAntiAlias(true);
    textPaint.setARGB(0xff, 0xff, 0xff, 0xff);
    // draw the text centered
    canvas.drawText(text, 16, 112, textPaint);

    final int texture = initTex(GLES20.GL_TEXTURE_2D,
        GLES20.GL_TEXTURE0, GLES20.GL_NEAREST, GLES20.GL_LINEAR, GLES20.GL_REPEAT);

    // Alpha blending
    // GLES20.glEnable(GLES20.GL_BLEND);
    // GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

    // Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
    // Clean up
    bitmap.recycle();

    return texture;
  }

  /**
   * load, compile and link shader from Assets files
   *
   * @param vssAsset source file name in Assets of vertex shader
   */
  public static int loadShader(@NonNull final Context context,
                               final String vssAsset) {

    int program = 0;
    try {
      final String vss = AssetsHelper.loadString(context.getAssets(), vssAsset);
      final String fss = AssetsHelper.loadString(context.getAssets(), vssAsset);
      program = loadShader(vss, fss);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return program;
  }

  /**
   * load, compile and link shader
   *
   * @param vss source of vertex shader
   * @param fss source of fragment shader
   */
  public static int loadShader(final String vss, final String fss) {
    final int[] compiled = new int[1];
    // 编译顶点着色器
    final int vs = loadShader(GLES20.GL_VERTEX_SHADER, vss);
    if (vs == 0) {
      return 0;
    }
    // 编译片段着色器
    int fs = loadShader(GLES20.GL_FRAGMENT_SHADER, fss);
    if (fs == 0) {
      return 0;
    }
    // 链接
    final int program = GLES20.glCreateProgram();
    checkGlError("glCreateProgram");
    if (program == 0) {
      Log.e(TAG, "Could not create program");
    }
    GLES20.glAttachShader(program, vs);
    checkGlError("glAttachShader");
    GLES20.glAttachShader(program, fs);
    checkGlError("glAttachShader");
    GLES20.glLinkProgram(program);
    final int[] linkStatus = new int[1];
    GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
    if (linkStatus[0] != GLES20.GL_TRUE) {
      Log.e(TAG, "Could not link program: ");
      Log.e(TAG, GLES20.glGetProgramInfoLog(program));
      GLES20.glDeleteProgram(program);
      return 0;
    }
    return program;
  }

  /**
   * Compiles the provided shader source.
   *
   * @return A handle to the shader, or 0 on failure.
   */
  public static int loadShader(final int shaderType, final String source) {
    int shader = GLES20.glCreateShader(shaderType);
    checkGlError("glCreateShader type=" + shaderType);
    GLES20.glShaderSource(shader, source);
    GLES20.glCompileShader(shader);
    final int[] compiled = new int[1];
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
    if (compiled[0] == 0) {
      Log.e(TAG, "Could not compile shader " + shaderType + ":");
      Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader));
      GLES20.glDeleteShader(shader);
      shader = 0;
    }
    return shader;
  }

  /**
   * Checks to see if the location we obtained is valid.  GLES returns -1 if a label
   * could not be found, but does not set the GL error.
   * <p>
   * Throws a RuntimeException if the location is invalid.
   */
  public static void checkLocation(final int location, final String label) {
    if (location < 0) {
      throw new RuntimeException("Unable to locate '" + label + "' in program");
    }
  }

  /**
   * Writes GL version info to the log.
   */
  @SuppressLint("InlinedApi")
  public static void logVersionInfo() {
    Log.i(TAG, "vendor  : " + GLES20.glGetString(GLES20.GL_VENDOR));
    Log.i(TAG, "renderer: " + GLES20.glGetString(GLES20.GL_RENDERER));
    Log.i(TAG, "version : " + GLES20.glGetString(GLES20.GL_VERSION));

    if (BuildCheck.isAndroid43()) {
      final int[] values = new int[1];
      GLES30.glGetIntegerv(GLES30.GL_MAJOR_VERSION, values, 0);
      final int majorVersion = values[0];
      GLES30.glGetIntegerv(GLES30.GL_MINOR_VERSION, values, 0);
      final int minorVersion = values[0];
      if (GLES30.glGetError() == GLES30.GL_NO_ERROR) {
        Log.i(TAG, "version: " + majorVersion + "." + minorVersion);
      }
    }
  }
}