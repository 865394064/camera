package com.android.camera.ui;

public class jniTool
{
  public static native void decodedYUV420spToRGBA(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public static native void nativeInit();

  public static native void nativeResetIntArray(int[] paramArrayOfInt, int paramInt);
}