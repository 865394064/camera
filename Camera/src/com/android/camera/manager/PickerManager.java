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

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.ModeChecker;
import com.android.camera.R;
import com.android.camera.Util;
import com.android.camera.ui.PickerButton;
import com.android.camera.ui.PickerButton.Listener;
import com.android.camera.ui.RotateImageView;
import com.mediatek.camera.setting.SettingConstants;
import com.mediatek.camera.setting.preference.IconListPreference;
import com.mediatek.camera.setting.preference.ListPreference;

import java.util.Timer;
import java.util.TimerTask;

//begin: added by wangyouyou
//end: added by wangyouyou
public class PickerManager extends ViewManager implements Listener, OnClickListener,
        CameraActivity.OnPreferenceReadyListener, CameraActivity.OnParametersReadyListener {
    private static final String TAG = "PickerManager";
    
    public interface PickerListener {
        boolean onSlowMotionPicked(String turnon);
        
        boolean onHdrPicked(String value);
        
        boolean onGesturePicked(String value);
        
        boolean onSmilePicked(String value);
        
        boolean onCameraPicked(int camerId);
        
        boolean onFlashPicked(String flashMode);
        
        boolean onStereoPicked(boolean stereoType);
        
        boolean onModePicked(int mode, String value, ListPreference preference);
    }
    
    private PickerButton mSlowMotion;
    private RotateImageView mGestureShot;//modify by scq : PickerButton mGestureShot;
    private PickerButton mHdr;
    private PickerButton mSmileShot;
    private PickerButton mFlashPicker;
    private RotateImageView mIndicator;// add by scq by shenchenqi
    private PickerButton mCameraPicker;
    private PickerButton mStereoPicker;
    private ImageView mDynamicView;//added by wangyouyou
    private PickerListener mListener;
    private boolean mNeedUpdate;
    private boolean mPreferenceReady;
    private CameraActivity mContext;
    /**Begin: added by scq**/
    private LinearLayout mOnscreenFlashProactivePicker;
    private RotateImageView mFlashProactivePicker;
    private LinearLayout mIphoneFlashAutoTextLinear;
    private TextView mIphoneFlashAutoText;
    private LinearLayout mIphoneFlashOnTextLinear;
    private TextView mIphoneFlashOnText;
    private LinearLayout mIphoneFlashOffTextLinear;
    private TextView mIphoneFlashOffText;
    private LinearLayout mOnscreenFlashLinear;
    private LinearLayout mOnscreenHdrLinear;
    private LinearLayout mOnscreenDynamicLinear;
    private LinearLayout mOnscreenGestureShotLinear;
    private LinearLayout mOnscreenCameraLinear;
    private int mNumber = 2;
    /**End: added by scq**/
    private LinearLayout timing_close_Layout;
    private LinearLayout timing_3s_Layout;
    private LinearLayout timing_10s_Layout;
    private TextView timing_close_Text;
    private TextView timing_3s_Text;
    private TextView timing_10s_Text;
    //hjz

    private static final int PICKER_BUTTON_NUM = 7;
    private static final int BUTTON_SMILE_SHOT = 0;
    private static final int BUTTON_HDR = 1;
    private static final int BUTTON_FLASH = 2;
    private static final int BUTTON_CAMERA = 3;
    private static final int BUTTON_STEREO = 4;
    private static final int BUTTON_SLOW_MOTION = 5;
    private static final int BUTTON_GESTURE_SHOT = 6;   
    public static PickerButton[] mPickerButtons = new PickerButton[PICKER_BUTTON_NUM];   //modified by wangyouyou
    private boolean mDynamicFlag = false;//added by wangyouyou
    private boolean isCameraPicker = false;//added by shencgengqi
    //begin: added by wangyouyou
    private Timer mTimer = null;  
    private TimerTask mTimerTask = null;  
    private Handler mTimerHandler = new Handler(){  
  
            @Override  
            public void handleMessage(Message msg) {  
                switch(msg.what) {
			case 0:
				if(mCameraPicker != null) {
					mCameraPicker.setVisibility(View.VISIBLE);
					if(mCameraPicker.getVisibility() == View.VISIBLE) {
						stopTimerTask();
					}
				}
				break;
			case 1:
				if(mCameraPicker != null) {
					mCameraPicker.setVisibility(View.GONE);
					if(mCameraPicker.getVisibility() == View.GONE) {
						stopTimerTask();
					}
				}
				break;
		   }
            }  
      };  

	private void stopTimerTask() {
		if(mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		if(mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}
    //end: added by wangyouyou
    
    private static final int MAX_NUM_OF_SHOWEN = 4;
    private int[] mButtonPriority = { BUTTON_SLOW_MOTION, BUTTON_HDR, BUTTON_FLASH, BUTTON_CAMERA,
            BUTTON_STEREO, BUTTON_GESTURE_SHOT, BUTTON_SMILE_SHOT };     
    private boolean mDefineOrder = false;
    private static boolean[] sShownStatusRecorder = new boolean[PICKER_BUTTON_NUM];
    static {
        sShownStatusRecorder[BUTTON_SLOW_MOTION] = false;
        sShownStatusRecorder[BUTTON_HDR] = false;
        sShownStatusRecorder[BUTTON_FLASH] = false;
        sShownStatusRecorder[BUTTON_CAMERA] = false;
        sShownStatusRecorder[BUTTON_STEREO] = false;
        sShownStatusRecorder[BUTTON_GESTURE_SHOT] = true;
        sShownStatusRecorder[BUTTON_SMILE_SHOT] = true;
    }
    
    public PickerManager(CameraActivity context) {
        super(context);
        mContext = context;
        context.addOnPreferenceReadyListener(this);
        context.addOnParametersReadyListener(this);
    }
    
    @Override
    protected View getView() {
        View view = inflate(R.layout.onscreen_pickers);
        
   	 /**Begin: added by scq**/
    	mOnscreenFlashProactivePicker = (LinearLayout) view.findViewById(R.id.onscreen_flash_proactive_linear);
    	mFlashProactivePicker = (RotateImageView) view.findViewById(R.id.onscreen_flash_proactive_picker);
    	mIphoneFlashAutoTextLinear = (LinearLayout) view.findViewById(R.id.iphone_flash_auto_text_linear);
    	mIphoneFlashAutoText = (TextView) view.findViewById(R.id.iphone_flash_auto_text_picker);
    	mIphoneFlashOnTextLinear = (LinearLayout) view.findViewById(R.id.iphone_flash_on_text_linear);
    	mIphoneFlashOnText = (TextView) view.findViewById(R.id.iphone_flash_on_text_picker);
    	mIphoneFlashOffTextLinear = (LinearLayout) view.findViewById(R.id.iphone_flash_off_text_linear);
    	mIphoneFlashOffText = (TextView) view.findViewById(R.id.iphone_flash_off_text_picker);
		
    	mOnscreenFlashLinear = (LinearLayout) view.findViewById(R.id.onscreen_flash_linear);
    	mOnscreenHdrLinear = (LinearLayout) view.findViewById(R.id.onscreen_hdr_linear);
    	mOnscreenDynamicLinear = (LinearLayout) view.findViewById(R.id.onscreen_dynamic_linear);
    	mOnscreenGestureShotLinear = (LinearLayout) view.findViewById(R.id.onscreen_gesture_shot_linear);
    	mOnscreenCameraLinear = (LinearLayout) view.findViewById(R.id.onscreen_camera_linear);
    	/**End: added by scq**/
        mSlowMotion = (PickerButton) view.findViewById(R.id.onscreen_slow_motion_picker);
        timing_close_Layout= (LinearLayout) view.findViewById(R.id.iphone_timing_close);
        timing_3s_Layout= (LinearLayout) view.findViewById(R.id.iphone_timing_5s);
        timing_10s_Layout= (LinearLayout) view.findViewById(R.id.iphone_timing_10s);
        timing_close_Text=(TextView) view.findViewById(R.id.iphone_text_close);
        timing_3s_Text=(TextView) view.findViewById(R.id.iphone__text_3s);
        timing_10s_Text=(TextView) view.findViewById(R.id.iphone__text_10s);

        mGestureShot = (RotateImageView) view.findViewById(R.id.onscreen_gesture_shot_picker);
        mGestureShot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                android.util.Log.d("hjz","mGestureShot click");
               // setHideOrShowFlashText(false);
                //setHideOrShowFlashProactiveView(false);
                setHideOrShowHdrView(true);
                setHideOrShowCameraView(true);
                setHideOrShowTimeView(false);
                hideOrShowDynamicView(true);
                setHideOrShowFlashView(true);
                //mFlashProactivePicker.setImageResource(R.drawable.ic_countdown_clock);
                timing_close_Layout.setVisibility(View.VISIBLE);
                timing_3s_Layout.setVisibility(View.VISIBLE);
                timing_10s_Layout.setVisibility(View.VISIBLE);
                //timing_close_Text.setTextColor(mContext.getResources().getColor(R.color.mode_title_color_white));
                //timing_3s_Text.setTextColor(mContext.getResources().getColor(R.color.mode_title_color_white));
                //timing_10s_Text.setTextColor(mContext.getResources().getColor(R.color.mode_title_color_white));
            }
        });
        timing_close_Layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                android.util.Log.d("hjz","timing_close_Text");
                timing_close_Layout.setVisibility(View.GONE);
                timing_3s_Layout.setVisibility(View.GONE);
                timing_10s_Layout.setVisibility(View.GONE);
                setHideOrShowHdrView(false);
                setHideOrShowCameraView(false);
                setHideOrShowTimeView(false);
                hideOrShowDynamicView(false);
                setHideOrShowFlashView(false);
                SystemProperties.set("persist.cenon.clockfor3s","1");
                timing_close_Text.setTextColor(mContext.getResources().getColor(R.color.mode_title_color));
                timing_3s_Text.setTextColor(mContext.getResources().getColor(R.color.mode_title_color_white));
                timing_10s_Text.setTextColor(mContext.getResources().getColor(R.color.mode_title_color_white));
            }
        });
        timing_3s_Layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                timing_close_Layout.setVisibility(View.GONE);
                timing_3s_Layout.setVisibility(View.GONE);
                timing_10s_Layout.setVisibility(View.GONE);
                setHideOrShowHdrView(false);
                setHideOrShowCameraView(false);
                setHideOrShowTimeView(false);
                hideOrShowDynamicView(false);
                setHideOrShowFlashView(false);
                SystemProperties.set("persist.cenon.clockfor3s","3000");
                timing_close_Text.setTextColor(mContext.getResources().getColor(R.color.mode_title_color_white));
                timing_3s_Text.setTextColor(mContext.getResources().getColor(R.color.mode_title_color));
                timing_10s_Text.setTextColor(mContext.getResources().getColor(R.color.mode_title_color_white));
            }
        });
        timing_10s_Layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                timing_close_Layout.setVisibility(View.GONE);
                timing_3s_Layout.setVisibility(View.GONE);
                timing_10s_Layout.setVisibility(View.GONE);
                setHideOrShowHdrView(false);
                setHideOrShowCameraView(false);
                setHideOrShowTimeView(false);
                hideOrShowDynamicView(false);
                setHideOrShowFlashView(false);
                SystemProperties.set("persist.cenon.clockfor3s","10000");
                timing_close_Text.setTextColor(mContext.getResources().getColor(R.color.mode_title_color_white));
                timing_3s_Text.setTextColor(mContext.getResources().getColor(R.color.mode_title_color_white));
                timing_10s_Text.setTextColor(mContext.getResources().getColor(R.color.mode_title_color));
            }
        });
        timing_close_Text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                android.util.Log.d("hjz","timing_close_Text");
            }
        });

        mSmileShot = (PickerButton) view.findViewById(R.id.onscreen_smile_shot_picker);
        mHdr = (PickerButton) view.findViewById(R.id.onscreen_hdr_picker);
        mFlashPicker = (PickerButton) view.findViewById(R.id.onscreen_flash_picker);
	 mCameraPicker = (PickerButton) getContext().getCameraPicker();//add by scq 20160920
        mIndicator = (RotateImageView) view.findViewById(R.id.onscreen_camera_picker);//add by scq 20160920
        mStereoPicker = (PickerButton) view.findViewById(R.id.onscreen_stereo3d_picker);
	 mDynamicView = (ImageView) view.findViewById(R.id.onscreen_dynamic_picker);//added by wangyouyou
        
        mPickerButtons[BUTTON_SLOW_MOTION] = mSlowMotion;
        //mPickerButtons[BUTTON_GESTURE_SHOT] = mGestureShot; //del by scq
        mPickerButtons[BUTTON_SMILE_SHOT] = mSmileShot;
        mPickerButtons[BUTTON_HDR] = mHdr;
        mPickerButtons[BUTTON_FLASH] = mFlashPicker;
        mPickerButtons[BUTTON_CAMERA] = mCameraPicker;
        mPickerButtons[BUTTON_STEREO] = mStereoPicker;
        applyListeners();
        return view;
    }
    
    private void applyListeners() {
        if (mSlowMotion != null) {
            mSlowMotion.setListener(this);   
        }
        if (mGestureShot != null) {
            //mGestureShot.setListener(this);//del by wangyouyou
        }
        if (mSmileShot != null) {
            mSmileShot.setListener(this);
        }
        if (mHdr != null) {
            mHdr.setListener(this);
        }
        if (mFlashPicker != null) {
            mFlashPicker.setListener(this);
        }
        if (mCameraPicker != null) {
            mCameraPicker.setListener(this);
	     setDefaultCameraId();//added by xss for ForceTouch 		
        }
	 //begin: added by shenchengqi	
	 if (mIndicator!= null) {
            mIndicator.setOnClickListener(setOnClickListener);
        }
	 if(mFlashProactivePicker != null){
		mFlashProactivePicker.setOnClickListener(setFlashProactiveOnClickListener);
	 }
	 if(mIphoneFlashAutoTextLinear != null){
		mIphoneFlashAutoTextLinear.setOnClickListener(setFlashProactiveOnClickListener);
	 }
	 if(mIphoneFlashOnTextLinear != null){
		mIphoneFlashOnTextLinear.setOnClickListener(setFlashProactiveOnClickListener);
	 }
	 if(mIphoneFlashOffTextLinear != null){
		mIphoneFlashOffTextLinear.setOnClickListener(setFlashProactiveOnClickListener);
	 }
	 //end: added by shenchengqi
        if (mStereoPicker != null) {
            mStereoPicker.setListener(this);
        }
	 //begin: added by wangyouyou	
	 if (mDynamicView!= null) {
            mDynamicView.setOnClickListener(this);
        }
	 //end: added by wangyouyou
        /*Log.d(TAG, "applyListeners() mFlashPicker=" + mFlashPicker + ", mCameraPicker="
                + mCameraPicker + ", mStereoPicker=" + mStereoPicker);*/
    }
    //begin: added by wangyouyou
	 //begin: added by shenchengqi	
    private View.OnClickListener setFlashProactiveOnClickListener = new View.OnClickListener(){
    	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id){
                case R.id.onscreen_flash_proactive_picker:{
                    Log.d("hjz","click flash");
		      setHideOrShowFlashText(true);
		      setHideOrShowHdrView(true);
		      setHideOrShowCameraView(true);
		      setHideOrShowTimeView(true);
		      hideOrShowDynamicView(true);
		      if(mNumber == 0){
		      		mIphoneFlashAutoText.setTextColor(mContext.getResources().getColor(R.color.mode_title_color));
		      		mIphoneFlashOnText.setTextColor(Color.WHITE);
		      		mIphoneFlashOffText.setTextColor(Color.WHITE);
		      }else if(mNumber == 1){
		      		mIphoneFlashAutoText.setTextColor(Color.WHITE);
		      		mIphoneFlashOnText.setTextColor(mContext.getResources().getColor(R.color.mode_title_color));
		      		mIphoneFlashOffText.setTextColor(Color.WHITE);
		      }else if(mNumber == 2){
		      		mIphoneFlashAutoText.setTextColor(Color.WHITE);
		      		mIphoneFlashOnText.setTextColor(Color.WHITE);
		      		mIphoneFlashOffText.setTextColor(mContext.getResources().getColor(R.color.mode_title_color));
		      }
                    break;
                }
                case R.id.iphone_flash_auto_text_linear:{
                    Log.d("hjz","click flash_text_1");
		      mFlashProactivePicker.setImageResource(R.drawable.ic_flash_auto_holo_light_iphone);
		      setHideOrShowFlashText(false);
		      setHideOrShowHdrView(false);
		      setHideOrShowCameraView(false);
		      setHideOrShowTimeView(false);
		      hideOrShowDynamicView(false);
		      mNumber = 0;
                    break;
                }
                case R.id.iphone_flash_on_text_linear:{
                    Log.d("hjz","click flash_text_2");
		      mFlashProactivePicker.setImageResource(R.drawable.ic_flash_on_holo_light_iphone);
		      setHideOrShowFlashText(false);
		      setHideOrShowHdrView(false);
		      setHideOrShowCameraView(false);
		      setHideOrShowTimeView(false);
		      hideOrShowDynamicView(false);
		      mNumber = 1;
                    break;
                }
                case R.id.iphone_flash_off_text_linear:{
                    Log.d("hjz","click flash_text_3");
		      mFlashProactivePicker.setImageResource(R.drawable.ic_flash_off_holo_light_iphone);
		      setHideOrShowFlashText(false);
		      setHideOrShowHdrView(false);
		      setHideOrShowCameraView(false);
		      setHideOrShowTimeView(false);
		      hideOrShowDynamicView(false);
		      mNumber = 2;
                    break;
                }
                default:
                    break;
            }
	}
    };
    private View.OnClickListener setOnClickListener = new View.OnClickListener(){
    	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
			getContext().onColorEffect(true);
		/*if(!isCameraPicker){
			getContext().onColorEffect(true);
			mIndicator.setImageResource(R.drawable.ic_setting_normal_iphone);
			isCameraPicker = true;
		}else{
			getContext().onColorEffect(false);
			mIndicator.setImageResource(R.drawable.ic_setting_focus_iphone);
			isCameraPicker = false;
		}*/
	}
    };






	public int getFlashProactiveNumber(){
		return mNumber;
	}
    public RotateImageView getIndicator(){
	return mIndicator;
    	}
	public void setHideOrShowFlashText(boolean flag){
		if(mIphoneFlashAutoTextLinear != null)mIphoneFlashAutoTextLinear.setVisibility(flag ? View.VISIBLE : View.GONE);
		if(mIphoneFlashOnTextLinear != null)mIphoneFlashOnTextLinear.setVisibility(flag ? View.VISIBLE : View.GONE);
		if(mIphoneFlashOffTextLinear != null)mIphoneFlashOffTextLinear.setVisibility(flag ? View.VISIBLE : View.GONE);
	}
	public void setHideOrShowView(boolean hdrFlag, boolean cameraFlag, boolean timeFlag, boolean dynamicFlag){
        android.util.Log.d("kay15", "PickerManager P371 hideOrShowCameraHdrIcon: hdrFlag:" + hdrFlag);
		setHideOrShowHdrView(hdrFlag);
		setHideOrShowCameraView(cameraFlag);
		setHideOrShowTimeView(timeFlag);
		hideOrShowDynamicView(dynamicFlag);
	}
	public void setHideOrShowFlashProactiveView(boolean flag){
		if(mOnscreenFlashProactivePicker != null)mOnscreenFlashProactivePicker.setVisibility(flag ? View.VISIBLE : View.GONE);
		if(mFlashProactivePicker != null)mFlashProactivePicker.setVisibility(flag ? View.VISIBLE : View.GONE);
    }
	public void setHideOrShowFlashView(boolean flag){
		if(mOnscreenFlashLinear != null)mOnscreenFlashLinear.setVisibility(flag ? View.GONE : View.VISIBLE);
	}	
	public void setHideOrShowHdrView(boolean flag){
		if(mOnscreenHdrLinear != null)mOnscreenHdrLinear.setVisibility(flag ? View.GONE : View.VISIBLE);
	}
	public void setHideOrShowCameraView(boolean flag){
		if(mIndicator != null) mIndicator.setVisibility(flag ? View.GONE : View.VISIBLE);
		if(mOnscreenCameraLinear != null)mOnscreenCameraLinear.setVisibility(flag ? View.GONE : View.VISIBLE);
	}	
	public void setHideOrShowTimeView(boolean flag){
		if(mGestureShot != null) mGestureShot.setVisibility(flag ? View.GONE : View.VISIBLE);
		if(mOnscreenGestureShotLinear != null)mOnscreenGestureShotLinear.setVisibility(flag ? View.GONE : View.VISIBLE);
	}
	public void setHideOrShowCameraPicker(final boolean flag){
		//if(mCameraPicker != null)mCameraPicker.setVisibility(flag ? View.VISIBLE : View.GONE);//del by wangyouyou
		//begin: added by wangyouyou
		if(mTimer == null) {
			mTimer = new Timer();
		}
		if(mTimerTask == null) {
			mTimerTask = new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mTimerHandler.sendEmptyMessage(flag ? 0 : 1);
				}
			};
		}
		if(mTimer != null && mTimerTask != null) {
			mTimer.schedule(mTimerTask, 0, 50);
		}
		//end: added by wangyouyou
	}
	 //end: added by shenchengqi
	
    @Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(!mDynamicFlag){
			mDynamicView.setImageResource(R.drawable.onscreen_dynamic_picker);
			mDynamicFlag = true;
		}else{
			mDynamicView.setImageResource(R.drawable.onscreen_dynamic_picker);
			mDynamicFlag = false;
		}
	}

	public void hideOrShowDynamicView(boolean flag){
		/*Begin:added by xss for ForceTouch*/
		if(mDynamicView==null){
			View view = inflate(R.layout.onscreen_pickers);
			mDynamicView = (ImageView) view.findViewById(R.id.onscreen_dynamic_picker);
		}
		/*End:added by xss for ForceTouch*/
		mDynamicView.setVisibility(flag ? View.GONE : View.VISIBLE);
		if(mOnscreenDynamicLinear != null)mOnscreenDynamicLinear.setVisibility(flag ? View.GONE : View.VISIBLE);//add by scq
	}
    //end: added by wangyouyou
    private void clearListeners() {
        if (mSlowMotion != null) {
            mSlowMotion.setListener(null);
        }
        if (mGestureShot != null) {
            //mGestureShot.setListener(null);//del by scq
        }
        if (mSmileShot != null) {
            mSmileShot.setListener(null);
        }
        if (mHdr != null) {
            mHdr.setListener(null);
        }
        if (mFlashPicker != null) {
            mFlashPicker.setListener(null);
        }
        if (mCameraPicker != null) {
            //mCameraPicker.setListener(null);
        }
        if (mStereoPicker != null) {
            mStereoPicker.setListener(null);
        }
	 //begin: added by shenchengqi	
	 if (mIndicator!= null) {
            mIndicator.setOnClickListener(null);
        }
	 if(mFlashProactivePicker != null){
		mFlashProactivePicker.setOnClickListener(null);
	 }
	 if(mIphoneFlashAutoTextLinear != null){
		mIphoneFlashAutoTextLinear.setOnClickListener(null);
	 }
	 if(mIphoneFlashOnTextLinear != null){
		mIphoneFlashOnTextLinear.setOnClickListener(null);
	 }
	 if(mIphoneFlashOffTextLinear != null){
		mIphoneFlashOffTextLinear.setOnClickListener(null);
	 }
	 //end: added by shenchengqi
    }
    
    public void setListener(PickerListener listener) {
        mListener = listener;
    }
    
    @Override
    public void onPreferenceReady() {
        Log.i(TAG, "onPreferenceReady()");
        mNeedUpdate = true;
        mPreferenceReady = true;
    }
    
    @Override
    public void onCameraParameterReady() {
        Log.i(TAG, "onCameraParameterReady(), mDefineOrder:" + mDefineOrder);
        if (mSlowMotion != null) {
            mSlowMotion.reloadPreference();
        }
        if (mGestureShot != null) {
            //mGestureShot.reloadPreference();//del by scq
        }
        if (mSmileShot != null) {
            mSmileShot.reloadPreference();
        }
        if (mHdr != null) {
            mHdr.reloadPreference();
        }
        if (mFlashPicker != null) {
            mFlashPicker.reloadPreference();
        }
        if (mCameraPicker != null) {
            mCameraPicker.reloadPreference();
        }
        if (mStereoPicker != null) {
            mStereoPicker.reloadPreference();
        }
        
        // the max number of button shown on PickerManager UI is 4, Slow motion,
        // hdr, flash, dual camera,
        // stereo camera have high priority, gesture, smile have low priority,
        // but gesture's priority is
        // higher than smile, if the order of button is definite, do not
        // redefine again.
        if (!mDefineOrder) {
            int count = 0;
            for (int i = 0; i < mButtonPriority.length; i++) {
                ListPreference pref = null;
                boolean visible = false;
                int buttonIndex = mButtonPriority[i];
                switch (buttonIndex) {
                case BUTTON_SLOW_MOTION:
                    pref = (IconListPreference) getContext().getListPreference(
                            SettingConstants.ROW_SETTING_SLOW_MOTION);
                    break;
                case BUTTON_HDR:
                    pref = (IconListPreference) getContext().getListPreference(
                            SettingConstants.ROW_SETTING_HDR);
                    break;
                case BUTTON_FLASH:
                    pref = (IconListPreference) getContext().getListPreference(
                            SettingConstants.ROW_SETTING_FLASH);
                    break;
                case BUTTON_CAMERA:
                    pref = (IconListPreference) getContext().getListPreference(
                            SettingConstants.ROW_SETTING_DUAL_CAMERA);
                    visible = ModeChecker.getCameraPickerVisible(getContext());
                    if (visible) {
                        count++;
                        if (pref != null) {
                            pref.showInSetting(false);
                        }
                    }
                    pref = null;
                    break;
                case BUTTON_STEREO:
                    pref = (IconListPreference) getContext().getListPreference(
                            SettingConstants.ROW_SETTING_STEREO_MODE);
                    visible = ModeChecker.getStereoPickerVisibile(getContext());
                    if (visible) {
                        count++;
                        if (pref != null) {
                            pref.showInSetting(false);
                        }
                        
                    }
                    pref = null;
                    break;
                case BUTTON_GESTURE_SHOT:
                    pref = (IconListPreference) getContext().getListPreference(
                            SettingConstants.ROW_SETTING_GESTURE_SHOT);
                    break;
                case BUTTON_SMILE_SHOT:
                    pref = (IconListPreference) getContext().getListPreference(
                            SettingConstants.ROW_SETTING_SMILE_SHOT);
                    break;
                default:
                    break;
                }
                
                if (pref != null && pref.getEntries() != null
                        && pref.getEntries().length > 1) {
                    pref.showInSetting(false);
                    count++;
                    if (BUTTON_GESTURE_SHOT == buttonIndex) {
                        sShownStatusRecorder[BUTTON_GESTURE_SHOT] = false;
                    } else if (BUTTON_SMILE_SHOT == buttonIndex) {
                        sShownStatusRecorder[BUTTON_SMILE_SHOT] = false;
                    }
                }
                
                Log.i(TAG, "count:" + count + ", buttonIndex:" + buttonIndex);
                if (count >= MAX_NUM_OF_SHOWEN) {
                    break;
                }
            }
            mDefineOrder = true;
        } else {
            for (int i = 0; i < mButtonPriority.length; i++) {
                ListPreference pref = null;
                int buttonIndex = mButtonPriority[i];
                switch (buttonIndex) {
                case BUTTON_SLOW_MOTION:
                    pref = (IconListPreference) getContext().getListPreference(
                            SettingConstants.ROW_SETTING_SLOW_MOTION);
                    break;
                case BUTTON_HDR:
                    pref = (IconListPreference) getContext().getListPreference(
                            SettingConstants.ROW_SETTING_HDR);
                    break;
                case BUTTON_FLASH:
                    pref = (IconListPreference) getContext().getListPreference(
                            SettingConstants.ROW_SETTING_FLASH);
                    break;
                case BUTTON_CAMERA:
                    pref = (IconListPreference) getContext().getListPreference(
                            SettingConstants.ROW_SETTING_DUAL_CAMERA);
                    break;
                case BUTTON_STEREO:
                    pref = (IconListPreference) getContext().getListPreference(
                            SettingConstants.ROW_SETTING_STEREO_MODE);
                    break;
                case BUTTON_GESTURE_SHOT:
                    pref = (IconListPreference) getContext().getListPreference(
                            SettingConstants.ROW_SETTING_GESTURE_SHOT);
                    break;
                case BUTTON_SMILE_SHOT:
                    pref = (IconListPreference) getContext().getListPreference(
                            SettingConstants.ROW_SETTING_SMILE_SHOT);
                    break;
                default:
                    break;
                }
                if (pref != null) {
                    pref.showInSetting(sShownStatusRecorder[buttonIndex]);
                }
            }
        }
        
        refresh();
    }
    
    @Override
    public void hide() {
        if (mContext.getCurrentMode() == ModePicker.MODE_VIDEO
                && "on".equals(mContext.getISettingCtrl().getSettingValue(
                        SettingConstants.KEY_HDR))) {
            for (int i = PICKER_BUTTON_NUM - 1; i >= 0; i--) {
                if (mPickerButtons[i] == mHdr) {
                    mPickerButtons[i].setEnabled(true);
                    mPickerButtons[i].setClickable(false);
                    mPickerButtons[i].setVisibility(View.VISIBLE);
                    super.fadeIn();
                } else {
                    Util.fadeOut(mPickerButtons[i]);
                }
            }
        } else {
            super.hide();
        }
    }
    
    @Override
    public boolean onPicked(PickerButton button, ListPreference pref, String newValue) {
        boolean picked = false;
        String key = pref.getKey();
        if (mListener != null) {
            int index = -1;
            for (int i = 0; i < PICKER_BUTTON_NUM; i++) {
                if (button.equals(mPickerButtons[i])) {
                    index = i;
                    break;
                }
            }
            
            switch (index) {
            case BUTTON_SLOW_MOTION:
                picked = mListener.onSlowMotionPicked(newValue);
                break;
            case BUTTON_GESTURE_SHOT:
                button.setValue(newValue);
                picked = mListener.onGesturePicked(newValue);
                break;
            case BUTTON_SMILE_SHOT:
                button.setValue(newValue);
                picked = mListener.onSmilePicked(newValue);
                break;
            case BUTTON_HDR:
                button.setValue(newValue);
                picked = mListener.onHdrPicked(newValue);
                break;
            case BUTTON_FLASH:
                picked = mListener.onFlashPicked(newValue);
                break;
            case BUTTON_CAMERA:
                picked = mListener.onCameraPicked(Integer.parseInt(newValue));
                break;
            case BUTTON_STEREO:
                picked = mListener.onStereoPicked("1".endsWith(newValue) ? true : false);
                break;
            default:
                break;
            }
            
        }
        Log.i(TAG, "onPicked(" + key + ", " + newValue + ") mListener=" + mListener + " return "
                + picked);
        return picked;
    }
    
    public void setCameraId(int cameraId) {
        if (mCameraPicker != null) {
            mCameraPicker.setValue("" + cameraId);
        }
    }
    
    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh() mPreferenceReady=" + mPreferenceReady + ", mNeedUpdate="
                + mNeedUpdate);
        if (mPreferenceReady && mNeedUpdate) {
            mSlowMotion.initialize((IconListPreference) getContext().getListPreference(
                    SettingConstants.ROW_SETTING_SLOW_MOTION));
            //mGestureShot.initialize((IconListPreference) getContext().getListPreference(
            //        SettingConstants.ROW_SETTING_GESTURE_SHOT));
            mSmileShot.initialize((IconListPreference) getContext().getListPreference(
                    SettingConstants.ROW_SETTING_SMILE_SHOT));
            mHdr.initialize((IconListPreference) getContext().getListPreference(
                    SettingConstants.ROW_SETTING_HDR));
            mFlashPicker.initialize((IconListPreference) getContext().getListPreference(
                    SettingConstants.ROW_SETTING_FLASH));
            mCameraPicker.initialize((IconListPreference) getContext().getListPreference(
                    SettingConstants.ROW_SETTING_DUAL_CAMERA));
            mStereoPicker.initialize((IconListPreference) getContext().getListPreference(
                    SettingConstants.ROW_SETTING_STEREO_MODE));

        if (mSlowMotion != null) {
            mSlowMotion.reloadValue();
            mSlowMotion.reloadPreference();
        }
        if (mGestureShot != null) {
            //mGestureShot.reloadPreference();//del by scq
        }
        if (mSmileShot != null) {
            mSmileShot.reloadPreference();
        }
        if (mFlashPicker != null) {
            mFlashPicker.updateView();
        }
        if (mCameraPicker != null) {
            boolean visible = ModeChecker.getCameraPickerVisible(getContext());
            ListPreference pref = getContext().getListPreference(
                    SettingConstants.ROW_SETTING_DUAL_CAMERA);
            if (pref == null || !pref.isEnabled()) {
                visible = false;
            }
            mCameraPicker.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        if (mStereoPicker != null) {
            boolean visible = ModeChecker.getStereoPickerVisibile(getContext());
            mStereoPicker.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        if (mHdr != null) {
            mHdr.reloadPreference();
        }
        mNeedUpdate = false;
        }
    }
    //begin added by wangyouyou
    public PickerButton getHdrButton(){
		return mHdr;
    }
//end added by wangyouyou
    @Override
    protected void onRelease() {
        super.onRelease();
        mNeedUpdate = true;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (mSlowMotion != null) {
            mSlowMotion.setEnabled(enabled);
            mSlowMotion.setClickable(enabled);
        }
        if (mGestureShot != null) {
            mGestureShot.setEnabled(enabled);
            mGestureShot.setClickable(enabled);
        }
        if (mSmileShot != null) {
            mSmileShot.setEnabled(enabled);
            mSmileShot.setClickable(enabled);
        }
        if (mFlashPicker != null) {
            mFlashPicker.setEnabled(enabled);
            mFlashPicker.setClickable(enabled);
        }
        if (mCameraPicker != null) {
            mCameraPicker.setEnabled(enabled);
            mCameraPicker.setClickable(enabled);
        }
        if (mStereoPicker != null) {
            mStereoPicker.setEnabled(enabled);
            mStereoPicker.setClickable(enabled);
        }
        if (mHdr != null) {
            mHdr.setEnabled(enabled);
            mHdr.setClickable(enabled);
        }
    }
    /*Begin:added by xss for ForceTouch*/
    private void setDefaultCameraId(){
            CameraActivity activity = (CameraActivity) mContext;
            Intent intent = activity.getIntent();
            String action = intent.getAction();
	     int mode=intent.getIntExtra("camera_current_mode",6);
	     boolean isSelf=intent.getBooleanExtra("is_self", false);
	     Log.i("forcetouch","setDefaultCameraModeView()   mode="+mode+"   isSelf= "+isSelf);	 
	     if(isSelf){
                      new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.i("forcetouch","setDefaultCameraId()   mCameraPicker="+mCameraPicker);	 
					if(mCameraPicker!=null)mCameraPicker.performClick();
				}
			}, 0);
	     }
    }	
 /*End:added by xss for ForceTouch*/
 public void timeclick(View v) {
     switch (v.getId()){
         case R.id.iphone_text_close:
             android.util.Log.d("hjz","mGestureShot click");
             timing_close_Layout.setVisibility(View.GONE);
             timing_3s_Text.setVisibility(View.GONE);
             timing_10s_Text.setVisibility(View.GONE);
             break;
         case R.id.iphone__text_3s:
             android.util.Log.d("hjz","mGestureShot click");
             timing_close_Layout.setVisibility(View.GONE);
             timing_3s_Text.setVisibility(View.GONE);
             timing_10s_Text.setVisibility(View.GONE);
             break;
         case R.id.iphone__text_10s:
             android.util.Log.d("hjz","mGestureShot click");
             timing_close_Layout.setVisibility(View.GONE);
             timing_3s_Text.setVisibility(View.GONE);
             timing_10s_Text.setVisibility(View.GONE);
             break;


     }
 }
}
