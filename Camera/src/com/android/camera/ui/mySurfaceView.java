package com.android.camera.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import com.android.camera.Log;
import android.graphics.Matrix;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.graphics.YuvImage;
import android.graphics.ImageFormat;
import android.graphics.Rect;

public class mySurfaceView extends SurfaceView
  implements SurfaceHolder.Callback, preViewListner
{
  private static int mDegree;
  private static int mDisHeight;
  private static int mDisWidth = 0;
  private static int mHeight;
  private static int mWidth;
  ColorMatrix mCm = new ColorMatrix();
  private EFFECT mEffect = EFFECT.NONE;
  SurfaceHolder mHolder;
  Paint mPaint = new Paint(6);  
  private Surface mSurface;
  private int[] rgb;

  static
  {
    mDisHeight = 0;
    mDegree = 90;
    try
    {
      System.loadLibrary("jniTool");   
      jniTool.nativeInit();
    }
    catch (Exception localException)
    {
      Log.e("DHYCO", "jniTool init error," + localException.getMessage());
    }
  }

  public mySurfaceView(Context paramContext)
  {
    this(paramContext, null);
  }

  public mySurfaceView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    getHolder().addCallback(this);
    setWillNotDraw(false);
  }



  public static void setLandScape(boolean paramBoolean)
  {

  	
    if (paramBoolean)
    {
   

	mDegree = -90;	

      return;
    }
    mDegree = 90;
  }

  public static void setPreViewSize(int paramInt1, int paramInt2)
  {
    mWidth = paramInt1;
    mHeight = paramInt2;
    if (mDegree == 90)
    {
      mDisWidth = paramInt2;
      mDisHeight = paramInt1;
      return;
    }
    mDisWidth = paramInt1;
    mDisHeight = paramInt2;
  }
  	public Bitmap gBitmap;

  public void draw(Canvas paramCanvas)
  {
    if ((this.rgb != null) && (mDisWidth > 0))
    {
      this.mCm.reset();
	 
	//  Log.d("cenon","this.mEffect.ordinal()=="+this.mEffect.ordinal());
      switch (this.mEffect.ordinal())
      {
      default:
	  	
	  break;
      case 1:
	  	
            this.mCm.setSaturation(0.0F);
	  break;
      case 2:
		 float[] arrayOfFloat2 = { 0.393F, 0.769F, 0.189F, 0.0F, 50.0F, 0.349F, 0.686F, 0.168F, 0.0F, 50.0F, 0.272F, 0.534F, 0.131F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F };
      this.mCm.set(arrayOfFloat2);
	
	  	
	  break;
      case 3:
	 float[] arrayOfFloat3 = { -1.0F, 0.0F, 0.0F, 0.0F, 255.0F, 0.0F, -1.0F, 0.0F, 0.0F, 255.0F, 0.0F, 0.0F, -1.0F, 0.0F, 255.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F };
      this.mCm.set(arrayOfFloat3);
	  break;
      case 4:
  	float[] arrayOfFloat4 = { 0.2F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 50.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F };
      this.mCm.set(arrayOfFloat4);


	  break;
      case 5:
	float[] arrayOfFloat5 = { 1.0F, 1.0F, 1.0F, -2.0F, 0.0F, 1.0F, 1.0F, 1.0F, -2.0F, 0.0F, 1.0F, 1.0F, 1.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F };
      this.mCm.set(arrayOfFloat5);
	  break;
      case 6:
	float[] arrayOfFloat6 = { -1.0F, -1.0F, -1.0F, -1.0F, 255.0F, -1.0F, -1.0F, -1.0F, -1.0F, 255.0F, -1.0F, -1.0F, -1.0F, -1.0F, 255.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F };
      this.mCm.set(arrayOfFloat6);
	  break;
      case 7:
	  	 

	     float[] arrayOfFloat7 = { 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F };
      this.mCm.set(arrayOfFloat7);
	  break;
	  	
      case 8://red
	  		  	 float[] arrayOfFloat8 = { 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F };
      this.mCm.set(arrayOfFloat8);
	  break;
      }

	   this.mPaint.setColorFilter(new ColorMatrixColorFilter(this.mCm));

		 gBitmap=Bitmap.createBitmap(this.rgb, mDisWidth,mDisHeight ,Bitmap.Config.RGB_565);
			
		if(mDegree!=90){
			Matrix m = new Matrix();
			 
		      m.setRotate(mDegree,(float) gBitmap.getWidth() / 2, (float) gBitmap.getHeight() / 2);
			
        m.postScale(-1, 1, (float) gBitmap.getWidth() / 2, (float) gBitmap.getHeight() / 2);

		      final Bitmap bm = Bitmap.createBitmap(gBitmap, 0, 0, gBitmap.getWidth(), gBitmap.getHeight(), m, true);
			  paramCanvas.drawBitmap(bm, null, new RectF(0.0F, 0.0F, getWidth(), getHeight()), this.mPaint);
		}else{
     	 paramCanvas.drawBitmap(gBitmap, null, new RectF(0.0F, 0.0F, getWidth(), getHeight()), this.mPaint);
			}
	  	
    }
  
  } 

  public EFFECT getColorEffect()
  {
    return this.mEffect;
  }

  public Surface getSurface()
  {
    return this.mSurface;
  }

  public void resest(boolean paramBoolean)
  {
  }

  public void setColorEffect(EFFECT paramEFFECT)
  {
    this.mEffect = paramEFFECT;
  }

private Bitmap bitmap;
  public void startNPreView(byte[] paramArrayOfByte,android.hardware.Camera.Size size)
  {

	//Log.d("cenon","paramArrayOfByte=="+paramArrayOfByte.length+"  3 * (mWidth * mHeight) / 2=="+((3 * mWidth * mHeight) / 2));
  	//Log.d("cenon","mWidth=="+mWidth+" mHeight=="+mHeight+" mDegree=="+mDegree);
    if (this.rgb == null)
      this.rgb = new int[mWidth *  mHeight];
	jniTool.decodedYUV420spToRGBA(this.rgb, paramArrayOfByte, mWidth, mHeight, this.mEffect.ordinal(), mDegree);
    invalidate();
  }

  public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mHolder = paramSurfaceHolder;
    this.mSurface = paramSurfaceHolder.getSurface();
  }

  public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
  {
    this.mHolder = paramSurfaceHolder;
    this.mSurface = paramSurfaceHolder.getSurface();
  }

  public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
  {
    this.mHolder = paramSurfaceHolder;
    this.mSurface = null;
  }

  public static enum EFFECT
		  {
		  NONE("NONE",0),
		  MONO("MONO",1),
		  SEPIA("SEPIA",2),
		  NAGATIVE("NAGATIVE",3),
		  AQUE("AQUE",4),
		  BLACK_BOARD("BLACK_BOARD",5),
		  WHITE_BOARD("WHITE_BOARD",6),
		  SOLARIZE("SOLARIZE",7),
		  REDTINT("REDTINT",8);
		  
		  private EFFECT(String name,int _nCode) {

		      

		    }
		  }
}
