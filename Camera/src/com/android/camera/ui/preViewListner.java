package com.android.camera.ui;

public abstract interface preViewListner
{
  public abstract void resest(boolean paramBoolean);

  public abstract void startNPreView(byte[] paramArrayOfByte,android.hardware.Camera.Size size);
}