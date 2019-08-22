package com.android.camera.manager;

import android.app.Activity;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.camera.CameraActivity;
import com.android.camera.CameraManager.CameraProxy;


import com.android.camera.actor.CameraActor;
import com.android.camera.actor.PhotoActor;
import com.android.camera.ui.mySurfaceView;
import com.android.camera.ui.mySurfaceView.EFFECT;
import com.android.camera.ui.preViewListner;
import java.util.List;
import com.android.camera.R;
import com.android.camera.CameraManager;
import com.mediatek.camera.setting.preference.ListPreference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;//added by wangyouyou

public class ColorEffectManager extends ViewManager
  implements View.OnClickListener
{
  CameraActivity mContext;
  private boolean mDone = false;
  int mMinPicHeight = 0;
  int mMinPicWidth = 0;
  int mMinPreHeight = 0;
  int mMinPreWidth = 0;
  View mMyColorManagerView, mView;
  private android.hardware.Camera.Size mPicSize;
  private preViewListner[] mPl;
  private android.hardware.Camera.Size mPreSize;
  ListPreference mPreference;
  SharedPreferences mPreferences;
  SharedPreferences.Editor mEditor ;
  private int mFilterColorId = 0;

  public ColorEffectManager(CameraActivity paramCamera)
  {
    super(paramCamera);
    this.mContext = paramCamera;
    mPreferences = this.mContext.getSharedPreferences("color_id_preferences", Activity.MODE_PRIVATE);
    mEditor = mPreferences.edit();
  }

  public ColorEffectManager(CameraActivity paramCamera, int paramInt)  
  {
    super(paramCamera, paramInt);
    this.mContext = paramCamera;
  }

  protected View getView()
  {
      if (this.mMyColorManagerView == null)
      this.mMyColorManagerView =  inflate(R.layout.color_fitter_preview);
    int[] arrayOfInt = { R.id.surfaceView, R.id.surfaceView1, R.id.surfaceView2, R.id.surfaceView3, R.id.surfaceView4, R.id.surfaceView5, R.id.surfaceView6, R.id.surfaceView7, R.id.surfaceView8 };
    if (this.mPl == null)
    {
      this.mPl = new preViewListner[arrayOfInt.length];
      for (int i = 0; i < arrayOfInt.length; i++)
      {
        mySurfaceView localmySurfaceView = (mySurfaceView)this.mMyColorManagerView.findViewById(arrayOfInt[i]);
        if (i < mySurfaceView.EFFECT.values().length)
          localmySurfaceView.setColorEffect(mySurfaceView.EFFECT.values()[i]);
        localmySurfaceView.setOnClickListener(this);
        this.mPl[i] = localmySurfaceView;
      }
      this.mContext.getCameraActor().setPreViewListners(this.mPl);     
    }
    return this.mMyColorManagerView;
  }

  public void setCameraPreViewDone(boolean paramBoolean)
  {
    if (paramBoolean)
      this.mContext.getCameraDevice().setPreviewCallback(this.mContext.getCameraActor().getPreviewCallback());
    else 
	 this.mContext.getCameraDevice().setPreviewCallback(null);

  }

  public void hide()    
  {
	super.hide();
	mContext.getCameraActor().setPreViewListners(null);
	mySurfaceView.setPreViewSize(0, 0);       
	final android.hardware.Camera.Parameters localParameters = this.mContext.getParameters();
	//if(localParameters == null) return;
	int colorId = mPreferences.getInt("color_id", 0);
	if(colorId == 2) colorId = 3;
	else if(colorId == 3) colorId = 2;
	else if(colorId == 5) colorId = 6;
	else if(colorId == 6) colorId = 5;
	if(localParameters != null)
	localParameters.setColorEffect((String)localParameters.getSupportedColorEffects().get(colorId));
	/*mContext.lockRun(new Runnable()
      {
	        public void run()
	        {*/
			mySurfaceView.setLandScape(true);
			if(localParameters != null && ColorEffectManager.this.mPreSize != null)
			localParameters.setPreviewSize(ColorEffectManager.this.mPreSize.width, ColorEffectManager.this.mPreSize.height);
			mContext.doWork();
			if(((CameraActivity)mContext).getClickCloseColorEffect()){
				try {
					mContext.getCameraActor().onCameraParameterReady(true);  
					((CameraActivity)mContext).setClickCloseColorEffect(false);   
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			if(mContext.getCameraDevice()!= null)    
			mContext.getCameraDevice().setPreviewCallback(null);   
	        //}
      //});
      mContext.isStartFilterCamera = false;
	mContext.setShowOrHideColorEffectLayout(false);
  }


  public void onClick(View paramView)          
  {
     mFilterColorId = ((mySurfaceView)paramView).getColorEffect().ordinal();
     if(mFilterColorId != 7 && mFilterColorId != 8){
	     this.mPreference.setValueIndex(((mySurfaceView)paramView).getColorEffect().ordinal()); 
	     mEditor.putInt("color_id", ((mySurfaceView)paramView).getColorEffect().ordinal());
	     mEditor.commit();
     }
     ((CameraActivity)mContext).setClickCloseColorEffect(true);   
     Intent intent = new Intent();
     intent.setAction("iphone_color_filter");
     mContext.sendBroadcast(intent);
	 mContext.setShowOrHideColorEffectLayout(false);
  }

  public void show()
  {
       mContext.hideFlashAndHdrModeLayout();
	mPreference = mContext.getISettingCtrl().getListPreference("pref_camera_coloreffect_key");
	   super.show();
	mContext.setShowOrHideColorEffectLayout(true);
	mContext.isStartFilterCamera = true;
	mContext.getCameraActor().setPreViewListners(this.mPl);
	final android.hardware.Camera.Parameters localParameters = this.mContext.getParameters();
	this.mPreSize = localParameters.getPreviewSize();
       this.mPicSize = localParameters.getPictureSize();
	mMinPreWidth = ((android.hardware.Camera.Size)localParameters.getSupportedPreviewSizes().get(1)).width;
       mMinPreHeight = ((android.hardware.Camera.Size)localParameters.getSupportedPreviewSizes().get(1)).height;  

       mContext.lockRun(new Runnable()
      {
        public void run()
        {
             /*mySurfaceView.setLandScape(mContext.isLandScape());
          mySurfaceView.setPreViewSize(ColorEffectManager.this.mMinPreWidth, ColorEffectManager.this.mMinPreHeight);
          localParameters.setColorEffect((String)localParameters.getSupportedColorEffects().get(0));
          localParameters.setPreviewSize(ColorEffectManager.this.mMinPreWidth, ColorEffectManager.this.mMinPreHeight);
          mContext.applyParametersToServer();
          mContext.getCameraActor().onCameraParameterReady(true);*/
	    if(mContext.getMCameraId())
				mySurfaceView.setLandScape(false);
		       else mySurfaceView.setLandScape(true);
          mySurfaceView.setPreViewSize(ColorEffectManager.this.mMinPreWidth, ColorEffectManager.this.mMinPreHeight);
	   localParameters.setColorEffect((String)localParameters.getSupportedColorEffects().get(1));
	   localParameters.setPreviewSize(ColorEffectManager.this.mMinPreWidth, ColorEffectManager.this.mMinPreHeight);   
	   //mContext.doWork();
          mContext.getCameraActor().onCameraParameterReady(true);
        }
      });
  }
}
