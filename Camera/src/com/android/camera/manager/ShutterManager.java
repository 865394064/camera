/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein is
 * confidential and proprietary to MediaTek Inc. and/or its licensors. Without
 * the prior written permission of MediaTek inc. and/or its licensors, any
 * reproduction, modification, use or disclosure of MediaTek Software, and
 * information contained herein, in whole or in part, shall be strictly
 * prohibited.
 * 
 * MediaTek Inc. (C) 2014. All rights reserved.
 * 
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER
 * ON AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NONINFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH
 * RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY,
 * INCORPORATED IN, OR SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES
 * TO LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 * RECEIVER EXPRESSLY ACKNOWLEDGES THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO
 * OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES CONTAINED IN MEDIATEK
 * SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK SOFTWARE
 * RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S
 * ENTIRE AND CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE
 * RELEASED HEREUNDER WILL BE, AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE
 * MEDIATEK SOFTWARE AT ISSUE, OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE
 * CHARGE PAID BY RECEIVER TO MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek
 * Software") have been modified by MediaTek Inc. All revisions are subject to
 * any receiver's applicable license agreements with MediaTek Inc.
 */
package com.android.camera.manager;

import android.view.View;
import android.view.View.OnClickListener;

import com.android.camera.CameraActivity;
import com.android.camera.CameraActivity.OnFullScreenChangedListener;
import com.android.camera.Log;
import com.android.camera.ModeChecker;
import com.android.camera.R;
import com.android.camera.ui.ShutterButton;
import com.android.camera.ui.ShutterButton.OnShutterButtonListener;

import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.setting.SettingConstants;
import com.android.camera.bridge.CameraAppUiImpl;//added by wangyouyou
import com.android.camera.bridge.CameraDeviceCtrl;//added by wangyouyou
import android.content.Context;//added by wangyouyou
import android.content.Intent;//added by xss for ForceTouch
public class ShutterManager extends ViewManager implements OnFullScreenChangedListener {
    private static final String TAG = "ShutterManager";
    
    public static final int SHUTTER_TYPE_PHOTO_VIDEO = 0;
    public static final int SHUTTER_TYPE_PHOTO = 1;
    public static final int SHUTTER_TYPE_VIDEO = 2;
    public static final int SHUTTER_TYPE_OK_CANCEL = 3;
    public static final int SHUTTER_TYPE_CANCEL = 4;
    public static final int SHUTTER_TYPE_CANCEL_VIDEO = 5;
    public static final int SHUTTER_TYPE_SLOW_VIDEO = 6;
    
    private int mShutterType = SHUTTER_TYPE_PHOTO_VIDEO;
    public static ShutterButton mPhotoShutter;//modified by wangyouyou
    private static ShutterButton mVideoShutter;//modified by wangyouyou
    private View mOkButton;
    private View mCancelButton;
    //begin:added by wangyouyou
    private static View totalTopLayer;
    private static View totalBottomLayer;
    private static boolean isMmsCameraTurn = false;
    private Context mContext;
    //private boolean mWyy = false;
    private static int mVideoType;//add by scq
    //end:added by wangyouyou
    private OnShutterButtonListener mPhotoListener;
    private OnShutterButtonListener mVideoListener;
    private OnClickListener mOklistener;
    private OnClickListener mCancelListener;
    private boolean mPhotoShutterEnabled = true;
    private boolean mVideoShutterEnabled = true;
    private boolean mCancelButtonEnabled = true;
    private boolean mOkButtonEnabled = true;
    private boolean mVideoShutterMasked;
    private boolean mFullScreen = true;
    private ISettingCtrl mISettingController;
    
    public ShutterManager(CameraActivity context) {
        super(context, VIEW_LAYER_SHUTTER);
        setFileter(false);
        context.addOnFullScreenChangedListener(this);
	 mContext = context;//added by wangyouyou
    }
    
    @Override
    protected View getView() {
        View view = null;
	 View totalView = null;//added by wangyouyou
        int layoutId = R.layout.camera_shutter_photo_video;
	 int totalLayoutId = R.layout.view_layers;//added by wangyouyou
        switch (mShutterType) {
        case SHUTTER_TYPE_PHOTO_VIDEO:
            layoutId = R.layout.camera_shutter_photo_video;
            break;
        case SHUTTER_TYPE_PHOTO:
            layoutId = R.layout.camera_shutter_photo;
            break;
        case SHUTTER_TYPE_VIDEO:
            layoutId = R.layout.camera_shutter_video;
            break;
        case SHUTTER_TYPE_OK_CANCEL:
            layoutId = R.layout.camera_shutter_ok_cancel;
            break;
        case SHUTTER_TYPE_CANCEL:
            layoutId = R.layout.camera_shutter_cancel;
            break;
        case SHUTTER_TYPE_CANCEL_VIDEO:
            layoutId = R.layout.camera_shutter_cancel_video;
            break;
        case SHUTTER_TYPE_SLOW_VIDEO:
            layoutId = R.layout.camera_shutter_slow_video;
        default:
            break;
        }
        view = inflate(layoutId);
	//begin: added by wangyouyou
	 totalView = inflate(totalLayoutId);
	 totalTopLayer = totalView.findViewById(R.id.top_layer);
	 totalBottomLayer = totalView.findViewById(R.id.bottom_layer);
	 if(!isMmsCameraTurn && CameraActivity.isMmsToCamera){
	 	if(CameraActivity.mMmsPicTextView != null)
		CameraActivity.mMmsPicTextView.setVisibility(View.VISIBLE);
		isMmsCameraTurn = true;
	 }else if(isMmsCameraTurn && CameraActivity.isMmsToCamera){
		CameraActivity.mMmsPicTextView.setVisibility(View.INVISIBLE);
		isMmsCameraTurn = false;
	 }
	 //end: added by wangyouyou
        mPhotoShutter = (ShutterButton) view.findViewById(R.id.shutter_button_photo);
        if (mShutterType == SHUTTER_TYPE_SLOW_VIDEO) {
            mVideoShutter = (ShutterButton) view.findViewById(R.id.shutter_button_slow_video);
        } else {
            mVideoShutter = (ShutterButton) view.findViewById(R.id.shutter_button_video);
        }
        mOkButton = view.findViewById(R.id.btn_done);
        mCancelButton = view.findViewById(R.id.btn_cancel);
        applyListener();
        return view;
    }
    
    @Override
    protected void onRelease() {
        if (mPhotoShutter != null) {
            mPhotoShutter.setOnShutterButtonListener(null);
        }
        if (mVideoShutter != null) {
            mVideoShutter.setOnShutterButtonListener(null);
        }
        if (mOkButton != null) {
            mOkButton.setOnClickListener(null);
        }
        if (mCancelButton != null) {
            mCancelButton.setOnClickListener(null);
        }
        mPhotoShutter = null;
        mVideoShutter = null;
        mOkButton = null;
        mCancelButton = null;
    }
    
    public void setSettingController(ISettingCtrl settingController) {
        mISettingController = settingController;
    }

    private void applyListener() {
        if (mPhotoShutter != null) {
            mPhotoShutter.setOnShutterButtonListener(mPhotoListener);
        }
        if (mVideoShutter != null) {
            mVideoShutter.setOnShutterButtonListener(mVideoListener);
        }
        if (mOkButton != null) {
            mOkButton.setOnClickListener(mOklistener);
        }
        if (mCancelButton != null) {
            mCancelButton.setOnClickListener(mCancelListener);
        }
        Log.d(TAG, "applyListener() mPhotoShutter=(" + mPhotoShutter + ", " + mPhotoListener
                + "), mVideoShutter=(" + mVideoShutter + ", " + mVideoListener + "), mOkButton=("
                + mOkButton + ", " + mOklistener + "), mCancelButton=(" + mCancelButton + ", "
                + mCancelListener + ")");
    }
    //begin:added by wangyouyou
    public static void doVideo(){   
	try{
	mPhotoShutter.setVisibility(View.GONE);
	mVideoShutter.setVisibility(View.VISIBLE);
	//CameraAppUiImpl.setViewGone();
	//CameraDeviceCtrl.setTotalViewBottomDown();
	} catch (Exception e) {
		// TODO: handle exception
	}
    }

    public static void setOtherMode(){
	try{
	mPhotoShutter.setVisibility(View.VISIBLE);
	mVideoShutter.setVisibility(View.GONE);
	//CameraAppUiImpl.setViewVisible();
	//CameraDeviceCtrl.setTotalViewBottomUp();  
	} catch (Exception e) {
		// TODO: handle exception
	} 
    }

    public static void setTemp(){
	//CameraAppUiImpl.setViewGone();
	//CameraDeviceCtrl.setTotalViewBottomUp();
    }

    /*public static void setVideoParams(){
	CameraDeviceCtrl.setVideoParams();
    }*/
    //end:added by wangyouyou
    public void setShutterListener(OnShutterButtonListener photoListener,
            OnShutterButtonListener videoListener, OnClickListener okListener,
            OnClickListener cancelListener) {
        mPhotoListener = photoListener;
        mVideoListener = videoListener;
        mOklistener = okListener;
        mCancelListener = cancelListener;
        applyListener();
    }
    
    public void switchShutter(int type) {
        Log.i(TAG, "switchShutterType(" + type + ") mShutterType=" + mShutterType);
        if (mShutterType != type) {
            mShutterType = type;
            reInflate();
        }
    }
    
    public int getShutterType() {
        return mShutterType;
    }
    //begin:added by wangyouyou
    public static void modifyVideoBtnBg(boolean flag, boolean extraFlag){
	  if(flag&&extraFlag&&(mVideoType == ModePicker.MODE_MOTION_TRACK)){//modify by scq
		mVideoShutter.setImageResource(R.drawable.btn_video_slow);
	  }else if(!flag&&extraFlag&&(mVideoType == ModePicker.MODE_MAV)){//modify by scq
		mVideoShutter.setImageResource(R.drawable.btn_video_delay);
	  }else if(!flag&&!extraFlag&&(mVideoType == ModePicker.MODE_LIVE_PHOTO)){//modify by scq
		mVideoShutter.setImageResource(R.drawable.btn_video);
	  }
    }
    //end:added by wangyouyou
    /**Begin: addde by scq 20161019**/
    public static void setVideoType(int type){
       Log.i("Alinscq","type = " + type);
	mVideoType = type;
	if(mVideoType == ModePicker.MODE_LIVE_PHOTO) {
		mVideoShutter.setImageResource(R.drawable.btn_video);
	}
    }
    /**End: added by scq 20161019**/
    
    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh() mPhotoShutterEnabled=" + mPhotoShutterEnabled + ", mFullScreen="
                + mFullScreen + ", isEnabled()=" + isEnabled());
        if (mVideoShutter != null) {
            boolean visible = ModeChecker.getModePickerVisible(getContext(), getContext()
                    .getCameraId(), ModePicker.MODE_VIDEO);
            boolean enabled = mVideoShutterEnabled && isEnabled() && mFullScreen && visible;
                    //&& !getContext().getWfdManagerLocal().isWfdEnabled();
            mVideoShutter.setEnabled(enabled);
            mVideoShutter.setClickable(enabled);
            //begin: added by wangyouyou
	     if(((CameraActivity)mContext).getModePicker().getCurrentMode() == 6 ||((CameraActivity)mContext).getModePicker().getCurrentMode() == 9 ){
	        //SettingManager.showOrHideIndicator(true);	
	     }else {
	     	SettingManager.showOrHideIndicator(false);	
	     	}
	     //end: added by wangyouyou
		 
            boolean isSlowMotionOn = false;
            if (mISettingController != null) {
                isSlowMotionOn = "on".equals(mISettingController
                        .getSettingValue(SettingConstants.KEY_SLOW_MOTION));
            }
            
            if (mVideoShutterMasked) {
                if (isSlowMotionOn) {
                    mVideoShutter.setImageResource(R.drawable.btn_slow_video_mask);
                } else {
                /**Begin: add by scq 20161019**/
                    switch(mVideoType){
		      case ModePicker.MODE_LIVE_PHOTO:{
                    	     mVideoShutter.setImageResource(R.drawable.btn_video_mask);
		            break;
		      }	
		      case ModePicker.MODE_MOTION_TRACK:{
                    	     mVideoShutter.setImageResource(R.drawable.btn_video_slow_mask);
		            break;
		      }	
		      case ModePicker.MODE_MAV:{
                    	     mVideoShutter.setImageResource(R.drawable.btn_video_delay_mask);
		      	     break;
		      }	
		      default:
		   	     break;
                    }
                }
		 /**End: add by scq 20161019**/
            } else {
                if (isSlowMotionOn) {
                    mVideoShutter.setImageResource(R.drawable.btn_slow_video);
                } else {
                	/**Begin: add by scq 20161019**/
                    switch(mVideoType){
		      case ModePicker.MODE_LIVE_PHOTO:{
                    	     mVideoShutter.setImageResource(R.drawable.btn_video);
		      	     setDefaultShutterButtonViewResource();//added by  xss for ForceTouch
		            break;
		      }	
		      case ModePicker.MODE_MOTION_TRACK:{
                    	     mVideoShutter.setImageResource(R.drawable.btn_video_slow);
		            break;
		      }	
		      case ModePicker.MODE_MAV:{
			     mVideoShutter.setImageResource(R.drawable.btn_video_delay);
		      	     break;
		      }	
		      default:
		   	     break;
                    }
		      /**End: add by scq 20161019**/
                }
            }
        }
        if (mPhotoShutter != null) {
            boolean enabled = mPhotoShutterEnabled && isEnabled() && mFullScreen;
            mPhotoShutter.setEnabled(enabled);
            mPhotoShutter.setClickable(enabled);
        }
        if (mOkButton != null) {
            boolean enabled = mOkButtonEnabled && isEnabled() && mFullScreen;
            mOkButton.setEnabled(enabled);
            mOkButton.setClickable(enabled);
        }
        if (mCancelButton != null) {
            boolean enabled = mCancelButtonEnabled && isEnabled() && mFullScreen;
            mCancelButton.setEnabled(enabled);
            mCancelButton.setClickable(enabled);
        }
    }
    
    public ShutterButton getPhotoShutter() {
        return mPhotoShutter;
    }
    
    public ShutterButton getVideoShutter() {
        return mVideoShutter;
    }
    
    public View getOkShutter() {
        return mOkButton;
    }
    
    public boolean performPhotoShutter() {
        boolean performed = false;
        if (mPhotoShutter != null && mPhotoShutter.isEnabled()) {
            mPhotoShutter.performClick();
            performed = true;
        }
        Log.d(TAG, "performPhotoShutter() mPhotoShutter=" + mPhotoShutter + ", return "
                + mPhotoShutter);
        return performed;
    }
    //begin: added by wangyouyou
    public boolean performVideoShutter() {
        boolean performed = false;
        if (mVideoShutter != null && mVideoShutter.isEnabled()) {
            mVideoShutter.performClick();
            performed = true;
        }
        Log.d(TAG, "performPhotoShutter() mPhotoShutter=" + mVideoShutter + ", return "
                + mVideoShutter);
        return performed;
    }
    //end: added by wangyouyou	
    
    public void setPhotoShutterEnabled(boolean enabled) {
        Log.d(TAG, "setPhotoShutterEnabled(" + enabled + ")");
        mPhotoShutterEnabled = enabled;
        refresh();
    }
    
    public boolean isPhotoShutterEnabled() {
        Log.v(TAG, "isPhotoShutterEnabled() return " + mPhotoShutterEnabled);
        return mPhotoShutterEnabled;
    }
    
    public void setVideoShutterEnabled(boolean enabled) {
        Log.d(TAG, "setVideoShutterEnabled(" + enabled + ")");
        mVideoShutterEnabled = enabled;
        refresh();
    }
    
    public boolean isVideoShutterEnabled() {
        Log.d(TAG, "isVideoShutterEnabled() return " + mVideoShutterEnabled);
        return mVideoShutterEnabled;
    }
    
    public void setCancelButtonEnabled(boolean enabled) {
        Log.d(TAG, "setCancelButtonEnabled(" + enabled + ")");
        mCancelButtonEnabled = enabled;
        refresh();
    }
    
    public void setOkButtonEnabled(boolean enabled) {
        Log.d(TAG, "setOkButtonEnabled(" + enabled + ")");
        mOkButtonEnabled = enabled;
        refresh();
    }
    
    public boolean isCancelButtonEnabled() {
        Log.d(TAG, "isCancelButtonEnabled() return " + mCancelButtonEnabled);
        return mCancelButtonEnabled;
    }
    
    public void setVideoShutterMask(boolean mask) {
        Log.d(TAG, "setVideoShutterMask(" + mask + ")");
        mVideoShutterMasked = mask;
        refresh();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        refresh();
    }
    
    @Override
    public void onFullScreenChanged(boolean full) {
        mFullScreen = full;
        refresh();
    }

 /*Begin:added by xss for ForceTouch*/
    private void setDefaultShutterButtonViewResource(){
            CameraActivity activity = (CameraActivity) mContext;
            Intent intent = activity.getIntent();
            String action = intent.getAction();
	     int mode=intent.getIntExtra("camera_current_mode",6);
	     boolean isSelf=intent.getBooleanExtra("is_self", false);
	     Log.i("forcetouch","setDefaultCameraModeView()   mode="+mode+"   isSelf= "+isSelf);	 
	     switch(mode){
                  case ModePicker.MODE_MOTION_TRACK:
			     modifyVideoBtnBg(true,true);
                           break;
		   case ModePicker.MODE_LIVE_PHOTO:
		   	     modifyVideoBtnBg(false,false);
			     break;
		   default:
		   	     break;
	     }
    }	
 /*End:added by xss for ForceTouch*/
}
