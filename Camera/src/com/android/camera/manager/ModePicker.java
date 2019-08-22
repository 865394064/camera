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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.android.camera.CameraActivity;
import com.android.camera.FeatureSwitcher;
import com.android.camera.Log;
import com.android.camera.ModeChecker;
import com.android.camera.R;
import com.android.camera.ui.ModePickerScrollView;
import com.android.camera.ui.RotateImageView;

import com.mediatek.camera.setting.preference.ListPreference;
//begin:added by wangyouyou
import com.android.camera.ui.ShutterButton;
import com.android.camera.ui.ShutterButton.OnShutterButtonListener;
import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;
import com.android.gallery3d.ui.PhotoView;  
import com.android.camera.bridge.CameraDeviceCtrl;
import com.mediatek.camera.setting.SettingUtils;
import com.android.camera.bridge.CameraDeviceExt;
import com.android.camera.bridge.CameraAppUiImpl;
import com.mediatek.camera.platform.ICameraAppUi;
import android.widget.TextView;


//end:added by wangyouyou
public class ModePicker extends ViewManager implements View.OnClickListener,
        View.OnLongClickListener, CameraActivity.OnFullScreenChangedListener {
    private static final String TAG = "ModePicker";
    
    private ListPreference mModePreference;
    
    public interface OnModeChangedListener {
        void onModeChanged(int newMode);
    }
    
    // can not change this sequence
    // Before MODE_VIDEO is "capture mode" for UI,switch "capture mode"
    // remaining view should not show
    public static final int MODE_PHOTO = 6;
    public static final int MODE_PHOTO_HUMANFACE = 5;//added by scq
    public static final int MODE_PHOTO_Q = 14;	
    public static final int MODE_HDR = 1;
    public static final int MODE_FACE_BEAUTY = 9;//modified by wangyouyou from 2 to 9
    public static final int MODE_PANORAMA = 0;
    public static final int MODE_MAV = 4;
    public static final int MODE_ASD = 5;
    public static final int MODE_MOTION_TRACK = 3;//modified by wangyouyou from 6 to 0 to 3
    public static final int MODE_PHOTO_PIP = 7;
    public static final int MODE_STEREO_CAMERA = 8;
    public static final int MODE_LIVE_PHOTO = 2;//modified by wangyouyou from 9 to 2
    
    public static final int MODE_VIDEO = 10;
    public static final int MODE_VIDEO_PIP = 11;
    
    
    public static final int MODE_NUM_ALL = 12;
    public static final int OFFSET = 100;
    private static final int OFFSET_STEREO_PREVIEW = OFFSET;
    private static final int OFFSET_STEREO_SINGLE = OFFSET * 2;
    
    public static final int MODE_PHOTO_3D = OFFSET_STEREO_PREVIEW + MODE_PHOTO;
    public static final int MODE_VIDEO_3D = OFFSET_STEREO_PREVIEW + MODE_VIDEO;
    
    public static final int MODE_PHOTO_SGINLE_3D = OFFSET_STEREO_SINGLE + MODE_PHOTO;
    public static final int MODE_PANORAMA_SINGLE_3D = OFFSET_STEREO_SINGLE + MODE_PANORAMA;
    
    private static final int DELAY_MSG_HIDE_MS = 3000; // 3s
    private static final int MODE_DEFAULT_MARGINBOTTOM = 100;
    private static final int MODE_DEFAULT_PADDING = 20;
    private static final int MODE_MIN_COUNTS = 4;
    private LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    
    private static final int[] MODE_ICONS_HIGHTLIGHT = new int[MODE_NUM_ALL];
    private static final int[] MODE_ICON_ORDER = {MODE_PHOTO, MODE_STEREO_CAMERA, MODE_PHOTO_PIP, MODE_LIVE_PHOTO, 
            MODE_MOTION_TRACK, MODE_FACE_BEAUTY, MODE_PANORAMA, MODE_MAV };
    static {
        MODE_ICONS_HIGHTLIGHT[MODE_PHOTO] = R.drawable.ic_mode_photo_focus;
        MODE_ICONS_HIGHTLIGHT[MODE_FACE_BEAUTY] = R.drawable.btn_shutter_video_focus;//modified by wangyouyou from ic_mode_facebeauty_focus to btn_shutter_video_focus
        MODE_ICONS_HIGHTLIGHT[MODE_PANORAMA] = R.drawable.ic_mode_panorama_focus;
        MODE_ICONS_HIGHTLIGHT[MODE_MAV] = R.drawable.ic_mode_mav_focus;
        MODE_ICONS_HIGHTLIGHT[MODE_LIVE_PHOTO] = R.drawable.ic_mode_live_photo_focus;
        MODE_ICONS_HIGHTLIGHT[MODE_MOTION_TRACK] = R.drawable.ic_mode_motion_track_focus;
        MODE_ICONS_HIGHTLIGHT[MODE_PHOTO_PIP] = R.drawable.ic_mode_pip_focus;
        MODE_ICONS_HIGHTLIGHT[MODE_STEREO_CAMERA] = R.drawable.ic_mode_refocus_focus;
    };
    private static final int[] MODE_ICONS_NORMAL = new int[MODE_NUM_ALL];
    static {
        MODE_ICONS_NORMAL[MODE_PHOTO] = R.drawable.ic_mode_photo_normal;
        MODE_ICONS_NORMAL[MODE_FACE_BEAUTY] = R.drawable.btn_shutter_video;//modified by wangyouyou from ic_mode_facebeauty_normal to btn_shutter_video
        MODE_ICONS_NORMAL[MODE_PANORAMA] = R.drawable.ic_mode_panorama_normal;
        MODE_ICONS_NORMAL[MODE_MAV] = R.drawable.ic_mode_mav_normal;
        MODE_ICONS_NORMAL[MODE_LIVE_PHOTO] = R.drawable.ic_mode_live_photo_normal;
        MODE_ICONS_NORMAL[MODE_MOTION_TRACK] = R.drawable.ic_mode_motion_track_normal;
        MODE_ICONS_NORMAL[MODE_PHOTO_PIP] = R.drawable.ic_mode_pip_normal;
        MODE_ICONS_NORMAL[MODE_STEREO_CAMERA] = R.drawable.ic_mode_refocus_normal;
    };
    
    private static final RotateImageView[] mModeViews = new RotateImageView[MODE_NUM_ALL];//modified by wangyouyou
    private static ModePickerScrollView mScrollView;
    //public static RotateImageView[] mThreeModeViews = new RotateImageView[3]; //added by wangyouyou
    private static int mCurrentMode = -1;
    private OnModeChangedListener mModeChangeListener;
    private OnScreenToast mModeToast;
    private int mDisplayWidth;
    private int mModeWidth;
    private int mModeMarginBottom = MODE_DEFAULT_MARGINBOTTOM;
    //begin:added by wangyouyou
    private CameraActivity ca;
    private LayoutParams para;
    public static RotateImageView mTempView;
    public static final int PHOTO_AND_LIVE_PHOTO_DISTANCE = 140;
    public static final int LIVE_PHOTO_AND_MOTION_TRACK_DISTANCE = 160;
    public static final int MOTION_TRACK_AND_FACE_BEAUTY_DISTANCE = 160;
    public static final int FACE_BEAUTY_AND_PANORAMA_DISTANCE = 180;
    public static final int PANORAMA_AND_MAV_DISTANCE = 150;
    public static final int MODE_DISTANCE = 49;
    public static boolean modeVisible = true;
    public static boolean mIsTurnCamera = false;
    public static boolean mIsTurnedCamera = true;
    private boolean mIsAction = false;
    public static boolean isDoneCamera = false;
    public static int tempModePosition = -1;
    public static int mCurrentModeId = MODE_PHOTO;
    public static int mModeId = 0;
    public static boolean isModeId = false;
    public static boolean isModeIdUpdate = false;
    private View mView, tempModeView;
    private boolean isCanGetView = true;
    public static boolean isRecording = false;
    private TextView modeOne, modeTwo, modeThree, modeFour, modeFive;
    private boolean isBackCamera = true;
	 private boolean isLeaveVideoPreview = false;
	 private final String PICTURE_RATIO_16_9 = "1.7778";
	 private final String PICTURE_RATIO_4_3 = "1.3333";
	 private final String PICTURE_RATIO_1_1 = "1.0";
    //end:added by wangyouyou
    
    public ModePicker(CameraActivity context) {
        super(context);
	 ca = context;//added by wangyouyou
        context.addOnFullScreenChangedListener(this);
    }
    
    public int getCurrentMode() {
        return mCurrentMode;
    }
	 //begin: added by wangyouyou
	 public void setMCurrentMode(int modeId) {
         mCurrentMode = modeId;
    }

	 public void setIsLeaveVideoPreviewFlag(boolean flag){
		isLeaveVideoPreview = flag;
	 }
	 //end: added by wangyouyou
    
    private void setRealMode(int mode) {
        Log.d(TAG, "setRealMode(" + mode + ") mCurrentMode=" + mCurrentMode);
        
        if (mode == MODE_PHOTO_PIP) {
            MMProfileManager.triggerPIPModeChange();
        }
        // in photo mode, if the hdr, asd, smile shot, gesture shot is on, we
        // should set the current mode is hdr or asd or smile shot. in hdr, asd,
        // smile shot, gesture shot mode if its values is off in
        // sharepreference,
        // we should set the current mode
        // as photo mode
       /* if (mode == MODE_PHOTO || mode == MODE_HDR || mode == MODE_ASD) {
            mode = getRealMode(mModePreference);
        }*/
        
        if (mCurrentMode != mode) {
            mCurrentMode = mode;
            highlightCurrentMode();
            notifyModeChanged();
            if (mModeToast != null) {
                mModeToast.cancel();
            }
        } else {
            // if mode do not change, we should reset ModePicker view enabled
            setEnabled(true);
        }
    }
    
    public void setCurrentMode(int mode) {
        int realmode = getModeIndex(mode);
        if (getContext().isStereoMode()) {
            if (FeatureSwitcher.isStereoSingle3d()) {
                realmode += OFFSET_STEREO_SINGLE;
            } else {
                realmode += OFFSET_STEREO_PREVIEW;
            }
        }
        Log.i(TAG, "setCurrentMode(" + mode + ") realmode=" + realmode);
        setRealMode(realmode);
    }
    
    private void highlightCurrentMode() {
	 //begin:added by wangyouyou
	 ((CameraActivity)ca).hideFlashAndHdrModeLayout();//added by wangyouyou
	 switch (mCurrentMode) {
	case MODE_MOTION_TRACK:
		ShutterManager.modifyVideoBtnBg(true, true);
		break;
	case MODE_MAV:
		ShutterManager.modifyVideoBtnBg(false, true);
		break;
	case MODE_LIVE_PHOTO:
		ShutterManager.modifyVideoBtnBg(false, false);
		break;

	default:
		break;
	}
	 if(mCurrentMode == MODE_MAV || mCurrentMode == MODE_MOTION_TRACK || mCurrentMode == MODE_LIVE_PHOTO ||mCurrentMode == MODE_PANORAMA)
	 	SettingManager.showOrHideIndicator(false);
	 else SettingManager.showOrHideIndicator(true);
	 //end:added by wangyouyou
        int index = getModeIndex(mCurrentMode);
        for (int i = 0; i < MODE_NUM_ALL; i++) {
            if (mModeViews[i] != null) {
                if (i == index) {
		      //begin:added by wangyouyou
		      String titleStr = "";
		      para = mModeViews[i].getLayoutParams(); 
		      para.height = 100;  
		      para.width = 130;  
		      mModeViews[i].setLayoutParams(para); 
		      //end:added by wangyouyou
                    //mModeViews[i].setImageResource(Color.BLACK);    
		      //mModeViews[i].setRotation(270);//added by wangyouyou
		      /*switch (i) {
			case MODE_PHOTO:titleStr = getContext().getResources().getString(R.string.mode_photo_title);break;
			case MODE_FACE_BEAUTY:titleStr = getContext().getResources().getString(R.string.mode_face_beauty_title);break;
			case MODE_PANORAMA:titleStr = getContext().getResources().getString(R.string.mode_panorama_title);break;
			case MODE_MAV:titleStr = getContext().getResources().getString(R.string.mode_mav_title);break;
			case MODE_LIVE_PHOTO:titleStr = getContext().getResources().getString(R.string.mode_live_photo_title);break;
			case MODE_MOTION_TRACK:titleStr = getContext().getResources().getString(R.string.mode_motion_track_title);break;
			case MODE_PHOTO_PIP:titleStr = getContext().getResources().getString(R.string.mode_photo_pip_title);break;
			default:
				break;   
			}*/
		      //mModeViews[i].setPicText(titleStr, getContext().getResources().getColor(R.color.mode_title_color));//added by wangyouyou   
                } else {
                    //begin:added by wangyouyou
                    String titleStr = "";
		      para = mModeViews[i].getLayoutParams(); 
		      para.height = 100;  
		      para.width =130;  
		      //if(i == MODE_PHOTO_PIP) para.width = 200;  
		      mModeViews[i].setLayoutParams(para); 
		      //end:added by wangyouyou
                    //mModeViews[i].setImageResource(Color.BLACK);
		      //mModeViews[i].setRotation(270);//added by wangyouyou
		      /*switch (i) {
			case MODE_PHOTO:titleStr = getContext().getResources().getString(R.string.mode_photo_title);break;
			case MODE_FACE_BEAUTY:titleStr = getContext().getResources().getString(R.string.mode_face_beauty_title);break;
			case MODE_PANORAMA:titleStr = getContext().getResources().getString(R.string.mode_panorama_title);break;
			case MODE_MAV:titleStr = getContext().getResources().getString(R.string.mode_mav_title);break;
			case MODE_LIVE_PHOTO:titleStr = getContext().getResources().getString(R.string.mode_live_photo_title);break;
			case MODE_MOTION_TRACK:titleStr = getContext().getResources().getString(R.string.mode_motion_track_title);break;
			case MODE_PHOTO_PIP:titleStr = getContext().getResources().getString(R.string.mode_photo_pip_title);break;
			default:
				break;
			}*/
		      //mModeViews[i].setPicText(titleStr, Color.WHITE);//added by wangyouyou
                }
            }
            if (MODE_HDR == index || MODE_ASD == index
                    || (FeatureSwitcher.isVfbEnable() && MODE_FACE_BEAUTY == index)) {
                mModeViews[MODE_PHOTO].setImageResource(MODE_ICONS_HIGHTLIGHT[MODE_PHOTO]);
            }
        }
    }
    
    public int getModeIndex(int mode) {
        int index = mode % OFFSET;
        Log.d(TAG, "getModeIndex(" + mode + ") return " + index);
        return index;
    }
    
    public void setListener(OnModeChangedListener l) {
        mModeChangeListener = l;
    }
    
    @Override
    protected View getView() {
        //clearListener();
        if(tempModeView == null){
        tempModeView = inflate(R.layout.mode_picker);
	 mView = tempModeView;
        mScrollView = (ModePickerScrollView) tempModeView.findViewById(R.id.mode_picker_scroller);
        mModeViews[MODE_PHOTO] = (RotateImageView) tempModeView.findViewById(R.id.mode_photo);    //0
        mModeViews[MODE_PHOTO_HUMANFACE] = (RotateImageView) tempModeView.findViewById(R.id.mode_photo_humanface);    //5 //added by scq 
        mModeViews[MODE_PHOTO_PIP] = (RotateImageView) tempModeView.findViewById(R.id.mode_photo_pip);  //7
        mModeViews[MODE_STEREO_CAMERA] = (RotateImageView) tempModeView.findViewById(R.id.mode_stereo_camera);  //8
        mModeViews[MODE_LIVE_PHOTO] = (RotateImageView) tempModeView.findViewById(R.id.mode_live_photo);  //9
        mModeViews[MODE_MOTION_TRACK] = (RotateImageView) tempModeView.findViewById(R.id.mode_motion_track);  //6
        mModeViews[MODE_FACE_BEAUTY] = (RotateImageView) tempModeView.findViewById(R.id.mode_face_beauty);  ///2
        mModeViews[MODE_PANORAMA] = (RotateImageView) tempModeView.findViewById(R.id.mode_panorama);//3//3
        mModeViews[MODE_MAV] = (RotateImageView) tempModeView.findViewById(R.id.mode_mav);///4

        modeOne = (TextView )tempModeView.findViewById(R.id.one);
	 modeTwo = (TextView )tempModeView.findViewById(R.id.two);
	 modeThree = (TextView )tempModeView.findViewById(R.id.three);
	 modeFour = (TextView )tempModeView.findViewById(R.id.four);
	 modeFive = (TextView )tempModeView.findViewById(R.id.five);
	 setDefaultCameraModeView(PhotoView.mModePostion,PhotoView.mCameraId);//added by xss fro ForceTouch	
        //begin:added by wangyouyou   
        //if(mThreeModeViews[0] == null){
        /*mThreeModeViews[0] = (RotateImageView) tempModeView.findViewById(R.id.a);   
	 mThreeModeViews[1] = (RotateImageView) tempModeView.findViewById(R.id.b);
	 mThreeModeViews[2] = (RotateImageView) tempModeView.findViewById(R.id.c);*/
	 /*mThreeModeViews[0].setVisibility(View.GONE);
	 mThreeModeViews[1].setVisibility(View.GONE);
	 mThreeModeViews[2].setVisibility(View.GONE); */ 
        //}
	 mTempView = mModeViews[MODE_FACE_BEAUTY];
        //end:added by wangyouyou
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        mDisplayWidth = Math.min(metrics.widthPixels, metrics.heightPixels);
        mModeWidth = getModeWidth();
        mModeMarginBottom = getDefaultMarginBottom();
        applyListener();
        highlightCurrentMode();
	 if(isCanGetView){
		isCanGetView = false;
	 }else {
		/*mThreeModeViews[0].setVisibility(View.VISIBLE);
		mThreeModeViews[1].setVisibility(View.VISIBLE);
		mThreeModeViews[2].setVisibility(View.GONE);*/
	 }
        	}
        return tempModeView;
    }

    //begin:added by wangyouyou
    public static void appearView(){
       /*if(mThreeModeViews != null){     
		mThreeModeViews[0].setVisibility(View.VISIBLE);
		mThreeModeViews[1].setVisibility(View.VISIBLE);
		mThreeModeViews[2].setVisibility(View.VISIBLE);
       }*/
    } 
	
    public static void disappearView(){
	if(mCurrentMode == MODE_PHOTO){
		/*if(mThreeModeViews != null){
			mThreeModeViews[0].setVisibility(View.GONE);
			mThreeModeViews[1].setVisibility(View.GONE);
			mThreeModeViews[2].setVisibility(View.GONE);
		}*/
	}
	//mScrollView.scrollTo(720, 0);
    }

    public static void setVideoMode(int i, boolean flag){
	   mCurrentMode = i;
	   isDoneCamera = flag;
	}

   public static int getVideoMode(){
         return tempModePosition;
   }
    //end:added by wangyouyou
    
    private void applyListener() {
        modeOne.setOnClickListener(this);//added by wangyouyou
        modeTwo.setOnClickListener(this);//added by wangyouyou
        modeThree.setOnClickListener(this);//added by wangyouyou
        modeFour.setOnClickListener(this);//added by wangyouyou
        modeFive.setOnClickListener(this);//added by wangyouyou
        for (int i = 0; i < MODE_NUM_ALL; i++) {
            if (mModeViews[i] != null) {
                mModeViews[i].setOnClickListener(this);
                mModeViews[i].setOnLongClickListener(this);
            }
        }
	 //mModeViews[MODE_PANORAMA].performClick();//added by wangyouyou
    }
    
    private void clearListener() {
        for (int i = 0; i < MODE_NUM_ALL; i++) {
            if (mModeViews[i] != null) {
                mModeViews[i].setOnClickListener(null);
                mModeViews[i].setOnLongClickListener(null);
                mModeViews[i] = null;
            }
        }
    }
//begin: added by shenchengqi
     private static boolean isHumanface = false;
     private static void setShowOrHideHumanface(boolean humanface){
		isHumanface = humanface;
     }
//end: added by shenchengqi
     //begin: added by wangyouyou
     public void hideAndShowPanorama(boolean flag){
	if(flag){
		if(mCurrentMode == MODE_PHOTO){
			Log.d("hjz","mCurrentMode == MODE_PHOTO1");
			modeFour.setText(R.string.mode_photo_humanface_title_sss);//add by hjz
			modeFive.setText(R.string.mode_face_beauty_title_sss);
		}else if(mCurrentMode == MODE_FACE_BEAUTY){
			modeOne.setText(R.string.mode_photo_title_sss);//add by hjz
			modeTwo.setText(R.string.mode_photo_humanface_title_sss);//add by hjz
			//hjz
			Log.d("hjz","mCurrentMode == MODE_FACE_BEAUTY");
			modeFour.setText(R.string.mode_panorama_title_sss);
		}
	}else{
		if(mCurrentMode == MODE_PHOTO){
			modeFour.setText(R.string.mode_photo_humanface_title_sss);//add by scq
			modeFive.setText(R.string.mode_face_beauty_title_sss);//modify by scq
		}else if(mCurrentMode == MODE_FACE_BEAUTY){
			modeOne.setText(R.string.mode_photo_title_sss);//add by scq
			modeTwo.setText(R.string.mode_photo_humanface_title_sss);//add by scq
			modeFour.setText(R.string.mode_panorama_title_sss);
		}
	}
     }

     public void setPanoramaFlag(boolean flag){
		isBackCamera = flag;
	 }

     public void setVideoModeText(boolean flag) {
		if(flag) { //hou
			modeFive.setText(R.string.mode_photo_humanface_title_sss);
		} else {//qian
			modeFive.setText(R.string.mode_face_beauty_title_sss);
		}
	 }
     //end: added by wangyouyou
    
    @Override
    public void onClick(View view) {   
        Log.d(TAG, "onClick(" + view + ") isEnabled()=" + isEnabled() + ", view.isEnabled()="
                + view.isEnabled() + ", getContext().isFullScreen()=" + getContext().isFullScreen()
                + ",mCurrentMode = " + mCurrentMode);
        if (FeatureSwitcher.isVfbEnable() && mCurrentMode == MODE_FACE_BEAUTY
                && view == mModeViews[MODE_PHOTO]) {
            Log.i(TAG, "onClick(,will return");
            return;
        }
        setEnabled(false);
	 //begin: added by wangyouyou
		android.util.Log.d("kay15", "ModePicker onClick: " + mCurrentMode);
	switch (mCurrentMode) {
		case MODE_PANORAMA:
			Log.d("hjz","MODE_PANORAMA="+MODE_PANORAMA);
			if(modeOne == view){
				setShowOrHideHumanface(true);//add by scq
				mModeViews[MODE_PHOTO_HUMANFACE].performClick();//modify by scq : MODE_PHOTO
			}else if(modeTwo == view){
				mModeViews[MODE_FACE_BEAUTY].performClick();
			}
			break;
		case MODE_FACE_BEAUTY:
			Log.d("hjz","MODE_FACE_BEAUTY="+MODE_FACE_BEAUTY);
			if(modeOne == view){
				Log.d("hjz","1");
				setShowOrHideHumanface(false);//add by scq
				mModeViews[MODE_PHOTO].performClick();//modify by scq : MODE_LIVE_PHOTO
			}else if(modeTwo == view){
				Log.d("hjz","2");
				setShowOrHideHumanface(true);//add by scq
				mModeViews[MODE_PHOTO_HUMANFACE].performClick();//modify by scq : MODE_PHOTO
			}else if(modeFour == view){
				Log.d("hjz","3");
				mModeViews[MODE_PANORAMA].performClick();
			}
			break;
		case MODE_PHOTO_HUMANFACE:
			Log.d("hjz","MODE_PHOTO_HUMANFACE="+MODE_PHOTO_HUMANFACE);
			if(modeOne == view){
				mModeViews[MODE_LIVE_PHOTO].performClick();
			}else if(modeTwo == view){
				setShowOrHideHumanface(false);//add by scq
				mModeViews[MODE_PHOTO].performClick();
			}else if(modeFour == view){
				mModeViews[MODE_FACE_BEAUTY].performClick();
			}else if(modeFive == view){
				mModeViews[MODE_PANORAMA].performClick();
			}
			break;
		case MODE_PHOTO:
			Log.d("hjz","MODE_PHOTO="+MODE_PHOTO);
			if(modeOne == view){
				Log.d("hjz","MODE_PHOTO=1");
				mModeViews[MODE_MOTION_TRACK].performClick();
			}else if(modeTwo == view){
				Log.d("hjz","MODE_PHOTO=2");
				mModeViews[MODE_LIVE_PHOTO].performClick();
			}else if(modeFour == view){
				Log.d("hjz","MODE_PHOTO=3");
				setShowOrHideHumanface(true);//add by scq
				if(ca.getMCameraId()) {
					Log.d("hjz","MODE_PHOTO=4");
					mModeViews[MODE_PHOTO_HUMANFACE].performClick();//modify by hjz : MODE_FACE_BEAUTY
				} else {
					//mModeViews[MODE_FACE_BEAUTY].performClick();//
					mModeViews[MODE_PHOTO_HUMANFACE].performClick();//modify by hjz : MODE_FACE_BEAUTY
				}
			}else if(modeFive == view){
				Log.d("hjz","MODE_PHOTO=5");
				mModeViews[MODE_FACE_BEAUTY].performClick();//modify by hjz : MODE_PANORAMA
			}
			break;
		case MODE_LIVE_PHOTO:
			Log.d("hjz","MODE_LIVE_PHOTO="+MODE_LIVE_PHOTO);
			if(modeOne == view){
				mModeViews[MODE_MAV].performClick();
			}else if(modeTwo == view){
				mModeViews[MODE_MOTION_TRACK].performClick();
			}else if(modeFour == view){
				setShowOrHideHumanface(false);//add by hjz
				mModeViews[MODE_PHOTO].performClick();
			}else if(modeFive == view){
				setShowOrHideHumanface(true);//add by hjz
				mModeViews[MODE_PHOTO_HUMANFACE].performClick();//modify by hjz : MODE_FACE_BEAUTY
			}
			break;
		case MODE_MOTION_TRACK:
			Log.d("hjz","MODE_MOTION_TRACK="+MODE_MOTION_TRACK);
			if(modeTwo == view){
				mModeViews[MODE_MAV].performClick();
			}else if(modeFour == view){
				mModeViews[MODE_LIVE_PHOTO].performClick();
			}else if(modeFive == view){
				setShowOrHideHumanface(false);//add by hjz
				mModeViews[MODE_PHOTO].performClick();
			}
			break;
		case MODE_MAV:
			Log.d("hjz","MODE_MAV="+MODE_MAV);
			if(modeFour == view){
				mModeViews[MODE_MOTION_TRACK].performClick();
			}else if(modeFive == view){
				mModeViews[MODE_LIVE_PHOTO].performClick();
			}
			break;

		default:
			break;
		}
	 //end: added by wangyouyou
		//hjz
        if (getContext().isFullScreen()) {
            for (int i = 0; i < MODE_NUM_ALL; i++) {
                if (mModeViews[i] == view) {
					((CameraActivity)ca).setShadeLayerVisible(i != MODE_PANORAMA);
					if(i == MODE_MAV ||i == MODE_MOTION_TRACK ||i == MODE_LIVE_PHOTO){
						((CameraActivity)ca).setShadeLayerBlackOrTransparent(false);
					 }else{    
						((CameraActivity)ca).setShadeLayerBlackOrTransparent(true);
					 }
			switch (i) {
		case MODE_PANORAMA:
			Log.d("hjz","    1=  "+MODE_PANORAMA);
			if(isBackCamera) {modeOne.setText(R.string.mode_photo_humanface_title_sss);
				modeTwo.setText(R.string.mode_face_beauty_title_sss);
				modeThree.setText(R.string.mode_panorama_title_sss);
				modeFour.setText(null);
				modeFive.setText(null);
			}//modify by scq :mode_photo_title_sss mode_face_beauty_title_sss modeFour.setText(R.string.mode_face_beauty_title_sss);
			else {modeOne.setText(R.string.mode_photo_humanface_title_sss);
			modeTwo.setText(R.string.mode_face_beauty_title_sss);
			modeThree.setText(R.string.mode_panorama_title_sss);
			modeFour.setText(null);
			modeFive.setText(null);}
			((CameraAppUiImpl)ca.getCameraAppUI()).getPickerManager().hideOrShowDynamicView(true);
			ca.hideOrShowCameraHdrIcon(true);
			ca.hideOrShowCameraFlashIcon(true);
			ca.setHideOrShowCameraView(true);//add by hjz
			ca.setHideOrShowTimeView(true);//add by hjz
			ca.setRecordingTimeShow(true);//add by hjz
			break;
		//begin add by hjz for click zhengfangxing
		case MODE_FACE_BEAUTY:
			Log.d("hjz","    2="+MODE_FACE_BEAUTY);
			if(isBackCamera) {
				modeOne.setText(R.string.mode_photo_title_sss);//modify by scq :mode_live_photo_title_sss
				modeTwo.setText(R.string.mode_photo_humanface_title_sss);//modify by scq :mode_photo_title_sss
				modeThree.setText(R.string.mode_face_beauty_title_sss);
				modeFour.setText(R.string.mode_panorama_title_sss);
				modeFive.setText(null);
			}else {
			modeOne.setText(R.string.mode_photo_title_sss);
			modeTwo.setText(R.string.mode_photo_humanface_title_sss);
				modeThree.setText(R.string.mode_face_beauty_title_sss);
				modeFour.setText(R.string.mode_panorama_title_sss);
				modeFive.setText(null);
			}
			//if(isBackCamera) modeFour.setText(R.string.mode_panorama_title_sss);
			//else modeFour.setText(null);
			//modeFive.setText(null);
			((CameraAppUiImpl)ca.getCameraAppUI()).getPickerManager().hideOrShowDynamicView(true);//add by hjz
			ca.hideOrShowCameraHdrIcon(false);
			ca.hideOrShowCameraFlashIcon(false);
			ca.setHideOrShowCameraView(false);//add by hjz
			ca.setHideOrShowTimeView(false);//add by hjz
			ca.setRecordingTimeShow(false);//add by hjz
			if(!ca.getMCameraId()){//add by hjz
				Log.d("hjz","Z .click");
				ca.setHideOrShowFlashProactiveView(true);//add by hjz
				ca.setHideOrShowFlashProactiveTextView(false);//add by hjz
				ca.setHideOrShowView(false, false, false, true);//add by hjz
				ca.hideOrShowCameraFlashIcon(true);//add by hjz
			}
			break;
		/**Begin: added by scq **/
		case MODE_PHOTO_HUMANFACE:
			Log.d("hjz","    3="+MODE_PHOTO_HUMANFACE);
			//mModeViews[MODE_PHOTO].performClick();

			modeOne.setText(R.string.mode_live_photo_title_sss);
			modeTwo.setText(R.string.mode_photo_title_sss);
			modeThree.setText(R.string.mode_photo_humanface_title_sss);
			modeFour.setText(R.string.mode_face_beauty_title_sss);
			if(isBackCamera) modeFive.setText(R.string.mode_panorama_title_sss);
			else modeFive.setText(R.string.mode_panorama_title_sss);
			((CameraAppUiImpl)ca.getCameraAppUI()).getPickerManager().hideOrShowDynamicView(true);
			ca.hideOrShowCameraHdrIcon(false);
			ca.hideOrShowCameraFlashIcon(false);
			ca.setHideOrShowCameraView(false);//add by scq
			ca.setHideOrShowTimeView(false);//add by scq
			ca.setRecordingTimeShow(false);//add by scq
			ca.setHideOrShowCameraPicker(false);//add by scq

			break;
		/**End: added by scq**/
		case MODE_PHOTO:
			Log.d("hjz","    4="+MODE_PHOTO);
			//begin: added by wangyouyou
			if(!ca.getMCameraId() && mCurrentMode == MODE_FACE_BEAUTY) {
				isHumanface = false;
			}
			//end: added by wangyouyou
			if(isHumanface){
				modeOne.setText(R.string.mode_live_photo_title_sss);
				modeTwo.setText(R.string.mode_photo_title_sss);
				modeThree.setText(R.string.mode_photo_humanface_title_sss);
				modeFour.setText(R.string.mode_face_beauty_title_sss);
				if(isBackCamera) 
					modeFive.setText(R.string.mode_panorama_title_sss);
				else 
					modeFive.setText(null);
				((CameraAppUiImpl)ca.getCameraAppUI()).getPickerManager().hideOrShowDynamicView(true);
				ca.hideOrShowCameraHdrIcon(false);//by kay ca.hideOrShowCameraHdrIcon(true);
				ca.hideOrShowCameraFlashIcon(false); //by kay  ca.hideOrShowCameraFlashIcon(true);
				ca.setHideOrShowCameraView(false); //by kay ca.setHideOrShowCameraView(true);//add by scq
				ca.setHideOrShowTimeView(false);//add by scq
				ca.setRecordingTimeShow(false);//add by scq
				ca.setHideOrShowCameraPicker(false);//add by scq
				break;
			}else{
				if(isBackCamera){
					android.util.Log.d("hjz","click three 1");
					modeOne.setText(R.string.mode_motion_track_title_sss);
					modeTwo.setText(R.string.mode_live_photo_title_sss);
					modeThree.setText(R.string.mode_photo_title_sss);
					modeFour.setText(R.string.mode_photo_humanface_title_sss);//modify by scq :mode_photo_title_sss 
					modeFive.setText(R.string.mode_face_beauty_title_sss);//added by scq
					ca.setHideOrShowCameraPicker(true);//add by scq
				}else {
					modeOne.setText(R.string.mode_motion_track_title_sss);
					modeTwo.setText(R.string.mode_live_photo_title_sss);
					modeThree.setText(R.string.mode_photo_title_sss);
					modeFour.setText(R.string.mode_photo_humanface_title_sss);
					android.util.Log.d("hjz","click three 2");
					modeFive.setText(R.string.mode_face_beauty_title_sss);//added by scq
				}
				/*if(isBackCamera) modeFive.setText(R.string.mode_panorama_title_sss);
				else modeFive.setText(null);*/// del by scq
			((CameraAppUiImpl)ca.getCameraAppUI()).getPickerManager().hideOrShowDynamicView(false);
			ca.hideOrShowCameraHdrIcon(false);
			ca.hideOrShowCameraFlashIcon(false);
			ca.setHideOrShowCameraView(false);//add by scq
			ca.setHideOrShowTimeView(false);//add by scq
			ca.setRecordingTimeShow(false);//add by scq
			if(!ca.getMCameraId()){//add by scq
				ca.setHideOrShowFlashProactiveView(true);//add by scq
				ca.setHideOrShowFlashProactiveTextView(false);//add by scq
				ca.setHideOrShowView(false, false, false, false);//add by scq
				ca.hideOrShowCameraFlashIcon(true);//add by scq
			}
			break;
			}
		case MODE_LIVE_PHOTO:
			Log.d("hjz","    5="+MODE_LIVE_PHOTO);
			modeOne.setText(R.string.mode_mav_title_sss);
			modeTwo.setText(R.string.mode_motion_track_title_sss);
			modeThree.setText(R.string.mode_live_photo_title_sss);
			modeFour.setText(R.string.mode_photo_title_sss);
			if(isBackCamera) modeFive.setText(R.string.mode_photo_humanface_title_sss);//modify by scq :mode_photo_title_sss 
			else modeFive.setText(R.string.mode_face_beauty_title_sss);//adde by scq
			//modeFive.setText(R.string.mode_photo_humanface_title_sss);//del by scq : mode_face_beauty_title_sss
			((CameraAppUiImpl)ca.getCameraAppUI()).getPickerManager().hideOrShowDynamicView(true);
			ca.hideOrShowCameraHdrIcon(true);
			ca.hideOrShowCameraFlashIcon(false);
			ca.setHideOrShowCameraView(true);//add by scq
			ca.setHideOrShowTimeView(true);//add by scq
			ca.setRecordingTimeShow(true);//add by scq
			ShutterManager.setVideoType(MODE_LIVE_PHOTO);//add by scq
			if(!ca.getMCameraId()){//add by scq
				ca.setHideOrShowFlashProactiveView(false);//add by scq
				ca.setHideOrShowFlashProactiveTextView(false);//add by scq
				ca.setHideOrShowView(true, true, true, true);//add by scq
				ca.hideOrShowCameraFlashIcon(true);//add by scq
			}
			break;
		case MODE_MOTION_TRACK:
			Log.d("hjz","    6= "+MODE_MOTION_TRACK);
			modeOne.setText(null);
			modeTwo.setText(R.string.mode_mav_title_sss);
			modeThree.setText(R.string.mode_motion_track_title_sss);
			modeFour.setText(R.string.mode_live_photo_title_sss);
			modeFive.setText(R.string.mode_photo_title_sss);
			((CameraAppUiImpl)ca.getCameraAppUI()).getPickerManager().hideOrShowDynamicView(true);
			ca.hideOrShowCameraHdrIcon(true);
			ca.hideOrShowCameraFlashIcon(false);
			ca.setHideOrShowCameraView(true);//add by scq
			ca.setHideOrShowTimeView(true);//add by scq
			ca.setRecordingTimeShow(true);//add by scq
			ShutterManager.setVideoType(MODE_MOTION_TRACK);//add by scq
			if(!ca.getMCameraId()){//add by scq
				ca.setHideOrShowFlashProactiveView(false);//add by scq
				ca.setHideOrShowFlashProactiveTextView(false);//add by scq
				ca.setHideOrShowView(true, true, true, true);//add by scq
				ca.hideOrShowCameraFlashIcon(true);//add by scq
			}
			break;
		case MODE_MAV:
			Log.d("hjz","    7=   "+MODE_MAV);
			modeOne.setText(null);   
			modeTwo.setText(null);
			modeThree.setText(R.string.mode_mav_title_sss);
			modeFour.setText(R.string.mode_motion_track_title_sss);
			modeFive.setText(R.string.mode_live_photo_title_sss);
			((CameraAppUiImpl)ca.getCameraAppUI()).getPickerManager().hideOrShowDynamicView(true);
			ca.hideOrShowCameraHdrIcon(true);
			ca.hideOrShowCameraFlashIcon(false);// by kay ca.hideOrShowCameraFlashIcon(true); //add by scq
			ca.setHideOrShowCameraView(true);//add by scq
			ca.setHideOrShowTimeView(true);//add by scq
			ca.setRecordingTimeShow(true); //by kay ca.setRecordingTimeShow(false);//add by scq
			ShutterManager.setVideoType(MODE_MAV);//add by scq
			if(!ca.getMCameraId()){//add by scq
				ca.setHideOrShowFlashProactiveView(false);//add by scq
				ca.setHideOrShowFlashProactiveTextView(false);//add by scq
				ca.setHideOrShowView(true, true, true, true);//add by scq
				ca.hideOrShowCameraFlashIcon(true);//add by scq
			}
			break;

		default:
			break;
		}
			//begin:added by wangyouyou
			mCurrentModeId = i;
			if(mCurrentMode == 9 && i == 2){
			       mModeViews[6].performClick();
				mIsAction = false;
				mModeViews[2].performClick();
				mScrollView.smoothScrollBy(0, 310);   
				return;
			 }
			appearView();
			if(!mIsAction){
				/*if(i == MODE_FACE_BEAUTY && mCurrentMode == MODE_PHOTO){
					mScrollView.smoothScrollBy(0, 140);
				}else if(i == MODE_LIVE_PHOTO && mCurrentMode == MODE_PHOTO){
					mScrollView.smoothScrollBy(0, 450);
				}else if(i == MODE_MOTION_TRACK && mCurrentMode == MODE_PHOTO){
					mScrollView.smoothScrollBy(0, 590);
				}else if(i == MODE_MOTION_TRACK && mCurrentMode == MODE_PHOTO){
					mScrollView.smoothScrollBy(0, 590);
				}else if(i == MODE_PANORAMA && mCurrentMode == MODE_FACE_BEAUTY){
					mScrollView.smoothScrollBy(0, -140);
				}else if(i == MODE_PHOTO && mCurrentMode == MODE_FACE_BEAUTY){
					mScrollView.smoothScrollBy(0, 160);
				}else if(i == MODE_LIVE_PHOTO && mCurrentMode == MODE_FACE_BEAUTY){
					mScrollView.smoothScrollBy(0, 310);
				}*/
				mIsAction = true;
			}else   
			     getMoveDistance(mCurrentMode, i);
		       PhotoView.setModePosition(i);    
			   /*if(i == 2 ||i == 4|| i==3){
			   	mModeViews[6].performClick();  
				setEnabled(true);
				mCurrentMode = i;
				highlightCurrentMode();
				ShutterManager.doVideo();
				return;
			   }*/
			//SettingUtils.changePreview("1080x1080", ca);
		       //end:added by wangyouyou
			if((mCurrentMode == MODE_LIVE_PHOTO && i == MODE_MAV) ||(mCurrentMode == MODE_LIVE_PHOTO && i == MODE_MOTION_TRACK)
				||(mCurrentMode == MODE_MAV && i == MODE_LIVE_PHOTO)||(mCurrentMode == MODE_MAV && i == MODE_MOTION_TRACK)
				||(mCurrentMode == MODE_MOTION_TRACK && i == MODE_LIVE_PHOTO)||(mCurrentMode == MODE_MOTION_TRACK && i == MODE_MAV)){}   
			
		        else if((mCurrentMode != MODE_LIVE_PHOTO && i == MODE_MAV) ||(mCurrentMode != MODE_LIVE_PHOTO && i == MODE_MOTION_TRACK)
				||(mCurrentMode != MODE_MAV && i == MODE_LIVE_PHOTO)||(mCurrentMode != MODE_MAV && i == MODE_MOTION_TRACK)
				||(mCurrentMode != MODE_MOTION_TRACK && i == MODE_LIVE_PHOTO)||(mCurrentMode != MODE_MOTION_TRACK && i == MODE_MAV)){
				 ShutterManager.doVideo();
			 }else{
				ShutterManager.setOtherMode();
			 }
			 //if(mModeViews[MODE_PANORAMA].getVisibility() != 8){
				 if(i == MODE_FACE_BEAUTY){
					CameraDeviceExt.setDisplaySize(PICTURE_RATIO_1_1);   
					((CameraActivity)ca).setTopAndBottomHeight(45, 150);
					((CameraActivity)ca).hideCameraTopAndBottomView();
					((CameraActivity)ca).setSurfaceViewLocation(false);
				 }else if(i == MODE_PANORAMA ||i == MODE_LIVE_PHOTO   || i == MODE_MOTION_TRACK || i == MODE_MAV){
				       CameraDeviceExt.setDisplaySize(PICTURE_RATIO_16_9);     
					((CameraActivity)ca).setTopAndBottomHeight(0, 0);
					((CameraActivity)ca).hideCameraTopAndBottomView();
					((CameraActivity)ca).setSurfaceViewLocation(true);
				 } else{
				       CameraDeviceExt.setDisplaySize(PICTURE_RATIO_4_3); 
					((CameraActivity)ca).setTopAndBottomHeight(45, 150);
					((CameraActivity)ca).hideCameraTopAndBottomView();
					((CameraActivity)ca).setSurfaceViewLocation(false);
				 }     
			 //}
			 if(isLeaveVideoPreview){
				 if((mCurrentMode == 2 && i == 6)||(mCurrentMode == 3 && i == 6)){
					setEnabled(true);
					setCurrentMode(9);   
					isLeaveVideoPreview = false;
				 }
			 }
			 if((mCurrentMode == 6 && i == 2)||(mCurrentMode == 6 && i == 3)||(mCurrentMode == 6 && i == 4)||
			 	(mCurrentMode == 2 && i == 3)||(mCurrentMode == 2 && i == 4)||(mCurrentMode == 3 && i == 4)||
			 	(mCurrentMode == 4 && i == 3)||(mCurrentMode == 3 && i == 2)||(mCurrentMode == 4 && i == 2)){
				 setEnabled(true);
				 if((mCurrentMode == 6 && i == 2)||(mCurrentMode == 6 && i == 3)||(mCurrentMode == 6 && i == 4)||
				 	(mCurrentMode == 3 && i == 6)||(mCurrentMode == 2 && i == 6)||(mCurrentMode == 2 && i == 9)){
				 	setCurrentMode(9);      
				 	}
			        mCurrentMode = i;
				 highlightCurrentMode();
				 //((CameraActivity)ca).initCameraState(true);
				 try {
			           	 if(ca.getCameraAppUI() instanceof CameraAppUiImpl){
					 	if(mCurrentMode == 4 || mCurrentMode == 3 ||mCurrentMode == 2){
					       	isModeIdUpdate = true;
						}else{ 
							isModeIdUpdate = false;
						}
					 		((CameraAppUiImpl)ca.getCameraAppUI()).getPickerManager().getHdrButton().reloadPreference();
			           	 }
				 } catch (Exception e) {
						// TODO: handle exception
				 }
			 } /*else if(i == MODE_FACE_BEAUTY){
			        mCurrentMode = i;
				 setEnabled(true);
				 highlightCurrentMode();
			 }*/ else{
				       try {
						isModeIdUpdate = false;
						((CameraAppUiImpl)ca.getCameraAppUI()).getPickerManager().getHdrButton().reloadPreference();
		                      	setCurrentMode(i);   
					} catch (Exception e) {
						// TODO: handle exception
					}
			      
			       }
		        //begin:added by wangyouyou
	 		if(mCurrentMode == 4 ||mCurrentMode == 3 ||mCurrentMode == 2 || mCurrentMode == 6 || mCurrentMode == 9 ||mCurrentMode == 0)
	 		tempModePosition = mCurrentMode;
	 		//end:added by wangyouyou
                    break;
                }
            }
            Log.i(TAG, "onClick,isCameraOpened:" + getContext().isCameraOpened());
            if(getContext().isCameraOpened()) {
                setEnabled(true);
            }
        } else {
            // if the is not full screen, we should reset PickMode view enable
            setEnabled(true);
        }
        
        if (view.getContentDescription() != null) {
            if (mModeToast == null) {
                mModeToast = OnScreenToast.makeText(getContext(), view.getContentDescription());
            } else {
                mModeToast.setText(view.getContentDescription());   
            }
            	  //mModeToast.showToast();    //deleted by wangyouyou
	     //}
        }
    }

    //begin:added by wangyouyou
    public static void goToNewMode(int i){
      if(mModeViews != null && mModeViews[i] != null){  //added by gaojunbin
	  		if(mCurrentMode == MODE_PHOTO && i == MODE_PHOTO_HUMANFACE) {
			  	setShowOrHideHumanface(true);
				mModeViews[MODE_PHOTO_HUMANFACE].performClick();  
		      	} else if(mCurrentMode == MODE_FACE_BEAUTY && i == MODE_PHOTO_HUMANFACE) {
			  	setShowOrHideHumanface(true);
				mModeViews[MODE_PHOTO_HUMANFACE].performClick();  
		      	} else if(mCurrentMode == MODE_PHOTO_HUMANFACE && i == MODE_PHOTO) {
			  	setShowOrHideHumanface(false);
				mModeViews[MODE_PHOTO].performClick();  
		      	} else if(mCurrentMode == MODE_PHOTO_HUMANFACE && i == MODE_FACE_BEAUTY) {
				mModeViews[MODE_FACE_BEAUTY].performClick();  
		      	} else {
	    			mModeViews[i].performClick();  
			}
	}
    }

    public void getMoveDistance(int mCurrentModePosition, int mDesignationPosition){
	/*if(mCurrentModePosition == MODE_PANORAMA && mDesignationPosition == MODE_FACE_BEAUTY)
      		mScrollView.smoothScrollBy(0, 140);
	else if(mModeViews[MODE_PANORAMA].getVisibility() != 8 && mCurrentModePosition == MODE_FACE_BEAUTY && mDesignationPosition == MODE_PHOTO)
      		mScrollView.smoothScrollBy(0, 160);
	else if(mCurrentModePosition == MODE_PHOTO && mDesignationPosition == MODE_LIVE_PHOTO)
      		mScrollView.smoothScrollBy(0, 150);
	else if(mCurrentModePosition == MODE_LIVE_PHOTO && mDesignationPosition == MODE_MOTION_TRACK)
      		mScrollView.smoothScrollBy(0, 140);
	else if(mCurrentModePosition == MODE_MOTION_TRACK && mDesignationPosition == MODE_MAV)
      		mScrollView.smoothScrollBy(0, 140);

	else if(mCurrentModePosition == MODE_MAV && mDesignationPosition == MODE_MOTION_TRACK)
      		mScrollView.smoothScrollBy(0, -140);
	else if(mCurrentModePosition == MODE_MOTION_TRACK && mDesignationPosition == MODE_LIVE_PHOTO)
      		mScrollView.smoothScrollBy(0, -140);
	else if(mCurrentModePosition == MODE_LIVE_PHOTO && mDesignationPosition == MODE_PHOTO)
      		mScrollView.smoothScrollBy(0, -150);
	
	else if(mCurrentModePosition == MODE_PHOTO && mDesignationPosition == MODE_FACE_BEAUTY)
      		mScrollView.smoothScrollBy(0, -160);
	else if(mCurrentModePosition == MODE_FACE_BEAUTY && mDesignationPosition == MODE_PANORAMA)
      		mScrollView.smoothScrollBy(0, -140);

	else if(mCurrentModePosition == MODE_PANORAMA && mDesignationPosition == MODE_PHOTO)
      		mScrollView.smoothScrollBy(0, 140+160);

	else if(mCurrentModePosition == MODE_FACE_BEAUTY && mDesignationPosition == MODE_LIVE_PHOTO)
      		mScrollView.smoothScrollBy(0, 160+150);
	else if(mCurrentModePosition == MODE_PHOTO && mDesignationPosition == MODE_PANORAMA)
      		mScrollView.smoothScrollBy(0, -(140+160));
	else if(mCurrentModePosition == MODE_PHOTO && mDesignationPosition == MODE_MOTION_TRACK)
      		mScrollView.smoothScrollBy(0, 150+140);
	else if(mCurrentModePosition == MODE_LIVE_PHOTO && mDesignationPosition == MODE_FACE_BEAUTY)
      		mScrollView.smoothScrollBy(0, -(150+160));
	else if(mCurrentModePosition == MODE_MOTION_TRACK && mDesignationPosition == MODE_PHOTO)
      		mScrollView.smoothScrollBy(0, -(150+140));
	else if(mCurrentModePosition == MODE_LIVE_PHOTO && mDesignationPosition == MODE_MAV)
      		mScrollView.smoothScrollBy(0, 140+140);
	else if(mCurrentModePosition == MODE_MAV && mDesignationPosition == MODE_LIVE_PHOTO)
      		mScrollView.smoothScrollBy(0, -(140+140));*/
    }
    //end:added by wangyouyou
    
    public void hideToast() {
        Log.i(TAG, "hideToast(), mModeToast:" + mModeToast);
        if (mModeToast != null) {
            mModeToast.hideToast();
        }
    }
    
    private void notifyModeChanged() {
        if (mModeChangeListener != null) {
            mModeChangeListener.onModeChanged(getCurrentMode());
        }
    }
    
    public void onRefresh() {
        Log.d(TAG, "onRefresh() mCurrentMode=" + mCurrentMode);  
        // get counts of mode supported by back camera and compute the margin
        // bottom
        // between mode icon.
        int supportModes = ModeChecker.modesSupportedByCamera(getContext(), 0);
        if (supportModes < MODE_MIN_COUNTS && supportModes > 1) {   
            mModeMarginBottom = (mDisplayWidth - supportModes * mModeWidth) / (supportModes - 1);
        }
        Log.d(TAG, "mModeMarginBottom:" + mModeMarginBottom);    
        mLayoutParams.setMargins(0, 0, 0, MODE_DISTANCE);//modified by wangyouyou from mModeMarginBottom to MODE_DISTANCE
        
        int visibleCount = 0;
        for (int i = 0; i < MODE_NUM_ALL; i++) {
            if (mModeViews[i] != null) {
                boolean visible = ModeChecker.getModePickerVisible(getContext(), getContext()
                        .getCameraId(), i);
                // check vFB
                // if vFB support, FB not need show in the mode picker line
                if (MODE_FACE_BEAUTY == i && FeatureSwitcher.isVfbEnable()) {
                    visible = false;
                }
                mModeViews[i].setVisibility(visible ? View.VISIBLE : View.GONE);
                mModeViews[i].setLayoutParams(mLayoutParams);
                mModeViews[i].setPadding(MODE_DEFAULT_PADDING, MODE_DEFAULT_PADDING,
                        MODE_DEFAULT_PADDING, MODE_DEFAULT_PADDING);
		  //begin:added by wangyouyou
		  if(mModeViews[MODE_MOTION_TRACK].getVisibility() == View.GONE){
			mModeViews[MODE_MAV].setVisibility(View.GONE);
		  }
		  //end:added by wangyouyou
                if (visible) {
                    visibleCount++;
                }
            }
        }
        // set margin botton of the last mode icon as 0.
        for (int i = MODE_ICON_ORDER.length - 1; i >= 0; i--) {   
            int index = MODE_ICON_ORDER[i];
            if (mModeViews[index] != null && mModeViews[index].getVisibility() == View.VISIBLE) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                mModeViews[index].setLayoutParams(params);
                break;
            }
        }
        
        if (visibleCount <= 1) { // to enable/disable background
            mScrollView.setVisibility(View.GONE);
        } else {
            mScrollView.setVisibility(View.VISIBLE);
        }
        highlightCurrentMode();      
	 //begin:added by wangyouyou
	 boolean flag = true;
	 if(mModeViews[MODE_PANORAMA].getVisibility() == 8 && mCurrentMode == MODE_LIVE_PHOTO && !isDoneCamera){
		mModeViews[MODE_LIVE_PHOTO].setVisibility(View.VISIBLE);
		mModeViews[MODE_MOTION_TRACK].setVisibility(View.VISIBLE);
		mModeViews[MODE_MAV].setVisibility(View.VISIBLE);      
		//mScrollView.smoothScrollBy(0, -100);
		flag = false;
		mIsAction = true;
		mIsTurnCamera = true;
	 }else if(mModeViews[MODE_PANORAMA].getVisibility() == 8 && mCurrentMode == MODE_FACE_BEAUTY){   
	 	mModeViews[MODE_LIVE_PHOTO].setVisibility(View.VISIBLE);
		mModeViews[MODE_MOTION_TRACK].setVisibility(View.VISIBLE);
		mModeViews[MODE_MAV].setVisibility(View.VISIBLE);
		//mScrollView.scrollTo(720, 0);
		//mScrollView.smoothScrollBy(0, -110);
		flag = false;
		mIsAction = true;
		mIsTurnCamera = true;
	 }else if(mModeViews[MODE_PANORAMA].getVisibility() == 8 && mCurrentMode == MODE_PHOTO){
	 	mModeViews[MODE_LIVE_PHOTO].setVisibility(View.VISIBLE);
		mModeViews[MODE_MOTION_TRACK].setVisibility(View.VISIBLE);
		mModeViews[MODE_MAV].setVisibility(View.VISIBLE);
		//mScrollView.scrollTo(720, 0);
		//mScrollView.smoothScrollBy(0, 97);     
		flag = false;
		mIsAction = true;
		mIsTurnCamera = true;
	 }else if(mModeViews[MODE_PANORAMA].getVisibility() == 8 && mCurrentMode == MODE_MOTION_TRACK){
	 	mModeViews[MODE_LIVE_PHOTO].setVisibility(View.VISIBLE);
		mModeViews[MODE_MOTION_TRACK].setVisibility(View.VISIBLE);
		mModeViews[MODE_MAV].setVisibility(View.VISIBLE);   
		//mScrollView.scrollTo(720, 0);
		//mScrollView.smoothScrollBy(0, 390);  
		flag = false;
		mIsAction = true;
		mIsTurnCamera = true;
	 }else if(mModeViews[MODE_PANORAMA].getVisibility() == 8 && mCurrentMode == MODE_MAV){
	 	mModeViews[MODE_LIVE_PHOTO].setVisibility(View.VISIBLE);
		mModeViews[MODE_MOTION_TRACK].setVisibility(View.VISIBLE);
		mModeViews[MODE_MAV].setVisibility(View.VISIBLE);
		//mScrollView.scrollTo(720, 0);
		//mScrollView.smoothScrollBy(0, 530);
		flag = false;
		mIsAction = true;
		mIsTurnCamera = true;
	 }else if(mModeViews[MODE_PANORAMA].getVisibility() == 8 && mCurrentMode == MODE_LIVE_PHOTO && isDoneCamera){
		mModeViews[MODE_LIVE_PHOTO].setVisibility(View.VISIBLE);
		mModeViews[MODE_MOTION_TRACK].setVisibility(View.VISIBLE);
		mModeViews[MODE_MAV].setVisibility(View.VISIBLE);       
		//mScrollView.smoothScrollBy(0, 0);   
		flag = false;
		mIsAction = true;   
		mIsTurnCamera = true;   
		isDoneCamera = true;
	 }
	 PhotoView.getCameraStatus(flag);
	 if(mModeViews[MODE_PANORAMA].getVisibility() == 0 && mIsTurnCamera && mCurrentMode == MODE_PHOTO){
		mIsTurnCamera = false;
		//mScrollView.smoothScrollBy(0, -140);
		mIsAction = false;
	 }else if(mModeViews[MODE_PANORAMA].getVisibility() == 0 && mIsTurnCamera && mCurrentMode == MODE_FACE_BEAUTY){
		mIsTurnCamera = false;
		//mScrollView.smoothScrollBy(0, 140);
		mIsAction = false;
	 }else if(mModeViews[MODE_PANORAMA].getVisibility() == 0 && mIsTurnCamera && mCurrentMode == MODE_LIVE_PHOTO){
		mIsTurnCamera = false;
		mIsAction = false;
		mModeViews[MODE_LIVE_PHOTO].performClick(); 
		//mScrollView.smoothScrollBy(0, 200);
	 }else if(mModeViews[MODE_PANORAMA].getVisibility() == 0 && mIsTurnCamera && mCurrentMode == MODE_MOTION_TRACK){
		mIsTurnCamera = false;
		mIsAction = false;
		mModeViews[MODE_MOTION_TRACK].performClick(); 
		//mScrollView.smoothScrollBy(0, 200);
	 }else if(mModeViews[MODE_PANORAMA].getVisibility() == 0 && mIsTurnCamera && mCurrentMode == MODE_MAV){
		mIsTurnCamera = false;
		mIsAction = false;
		//mModeViews[MODE_MAV].performClick(); 
		//mModeViews[MODE_MOTION_TRACK].performClick(); 
		//mScrollView.smoothScrollBy(0, 65);
	 }
	 //end:added by wangyouyou
    }
    
    @Override
    public boolean onLongClick(View view) {
        Log.d(TAG, "onLongClick(" + view + ")");
        if (view.getContentDescription() != null) {
            if (mModeToast == null) {
                mModeToast = OnScreenToast.makeText(getContext(), view.getContentDescription());
            } else {
                mModeToast.setText(view.getContentDescription());   
            }
            mModeToast.showToast();
        }
        // don't consume long click event
        return false;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (mScrollView != null) {
            mScrollView.setEnabled(enabled);
        }
        for (int i = 0; i < MODE_NUM_ALL; i++) {
            if (mModeViews[i] != null) {
                mModeViews[i].setEnabled(enabled);
                mModeViews[i].setClickable(enabled);
            }
        }
    }
    
    @Override
    protected void onRelease() {
        super.onRelease();
        mModeToast = null;
    }
    
    @Override
    public void onFullScreenChanged(boolean full) {
        Log.d(TAG, "onFullScreenChanged(" + full + ") mModeToast=" + mModeToast);
        if (mModeToast != null && !full) {
            mModeToast.cancel();
        }
    }
    
    private int getModeWidth() {
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),
                MODE_ICONS_NORMAL[MODE_PHOTO]);
        int bitmapWidth = bitmap.getWidth();
        return bitmapWidth + MODE_DEFAULT_PADDING * 2;
        
    }
    
    private int getDefaultMarginBottom() {
        // default show three and half mode icons
        return (mDisplayWidth - MODE_MIN_COUNTS * mModeWidth) / (MODE_MIN_COUNTS - 1)
                + (mModeWidth / (2 * (MODE_MIN_COUNTS - 1)));
    }
    
    public void setModePreference(ListPreference pref) {
        mModePreference = pref;
    }
    
    /*
     * when change capture mode from other modes to MODE_PHOTO, if the
     * SmileShot, HDR, ASD is on, the capture mode should be SmileShot, HDR,
     * ASD.
     
    public int getRealMode(ListPreference pref) {
        mModePreference = pref;
        int mode = ModePicker.MODE_PHOTO;
        if (pref != null) {
            pref.reloadValue();
            if (pref.getValue().equals("on")) {
                String key = pref.getKey();
                if (key.equals(CameraSettings.KEY_HDR)) {
                    mode = ModePicker.MODE_HDR;
                    // in hdr mode slow motion should be closed,and when out hdr
                    // slow motion not need back to on if it is
                    // on befor hdr open
                    ListPreference mPref = getContext().getListPreference(
                            CameraSettings.KEY_SLOW_MOTION);
                    if (mPref != null) {
                        mPref.setValue("off");
                    }
                } else if (key.equals(CameraSettings.KEY_ASD)) {
                    mode = ModePicker.MODE_ASD;
                }
                // if smile shot is on, set hdr, asd sharepreference value as
                // off
                // so does hdr, asd.
                rewritePreference(getRewriteKeys(key), "off");
            }
        }
        Log.i(TAG, "getRealMode(), pref:" + pref + " ,mode:" + mode);
        return mode;
    }
    
    private String[] getRewriteKeys(String key) {
        String[] keys = { CameraSettings.KEY_SMILE_SHOT, CameraSettings.KEY_HDR,
                CameraSettings.KEY_ASD };
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(key)) {
                keys[i] = null;
            }
        }
        return keys;
    }
    
    private void rewritePreference(String[] keys, String value) {
        for (String key : keys) {
            if (key == null) {
                continue;
            }
            ListPreference pref = getContext().getListPreference(key);
            if (pref != null) {
                pref.setValue(value);
            }
        }
    }*/

    /*Begin:added by xss for ForceTouch*/	
    public void setDefaultCameraModeView(int mode,int cameraId){
        Log.i("forcetouch","setDefaultCameraModeView()   mode="+mode+"   cameraId="+cameraId); 
	 setEnabled(true);
	 mCurrentMode=mode;
        switch (mode) {
		case MODE_PHOTO:
			modeOne.setText(R.string.mode_motion_track_title_sss);
			modeTwo.setText(R.string.mode_live_photo_title_sss);
			modeThree.setText(R.string.mode_photo_title_sss);
			if(isBackCamera) modeFour.setText(R.string.mode_photo_humanface_title_sss);//modify by scq :mode_photo_title_sss 
			else modeFour.setText(R.string.mode_face_beauty_title_sss);//adde by scq
			//modeFour.setText(R.string.mode_photo_humanface_title_sss);//modify by scq : mode_face_beauty_title_sss
			modeFive.setText(R.string.mode_face_beauty_title_sss);//added by scq
			/*if(isBackCamera) modeFive.setText(R.string.mode_panorama_title_sss);
			else modeFive.setText(null);*///del by scq
			((CameraAppUiImpl)ca.getCameraAppUI()).getPickerManager().hideOrShowDynamicView(false);
			// del by scq : ((CameraAppUiImpl)ca.getCameraAppUI()).getPickerManager().setCameraId(cameraId);
			ca.hideOrShowCameraHdrIcon(false);
			ca.hideOrShowCameraFlashIcon(false);
			((CameraActivity)ca).setShadeLayerBlackOrTransparent(true);
			CameraDeviceExt.setDisplaySize(PICTURE_RATIO_4_3); 
			((CameraActivity)ca).setTopAndBottomHeight(45, 150);
			((CameraActivity)ca).hideCameraTopAndBottomView();
			((CameraActivity)ca).setSurfaceViewLocation(false);
			SettingManager.showOrHideIndicator(true);
			ShutterManager.setOtherMode();
			break;
		case MODE_LIVE_PHOTO:
			modeOne.setText(R.string.mode_mav_title_sss);
			modeTwo.setText(R.string.mode_motion_track_title_sss);
			modeThree.setText(R.string.mode_live_photo_title_sss);
			modeFour.setText(R.string.mode_photo_title_sss);
			if(isBackCamera) modeFive.setText(R.string.mode_photo_humanface_title_sss);//modify by scq :mode_photo_title_sss 
			else modeFive.setText(R.string.mode_face_beauty_title_sss);//adde by scq
			//modeFive.setText(R.string.mode_photo_humanface_title_sss);//modify by scq : mode_face_beauty_title_sss
			((CameraAppUiImpl)ca.getCameraAppUI()).getPickerManager().hideOrShowDynamicView(true);
			ca.hideOrShowCameraHdrIcon(true);
			ca.hideOrShowCameraFlashIcon(false);
			((CameraActivity)ca).setShadeLayerBlackOrTransparent(false);
			CameraDeviceExt.setDisplaySize(PICTURE_RATIO_16_9);     
			((CameraActivity)ca).setTopAndBottomHeight(0, 0);
			((CameraActivity)ca).hideCameraTopAndBottomView();
			((CameraActivity)ca).setSurfaceViewLocation(true);
			SettingManager.showOrHideIndicator(false);
			ShutterManager.doVideo();
			//ShutterManager.modifyVideoBtnBg(false, false);
			break;
		case MODE_MOTION_TRACK:
			modeOne.setText(null);
			modeTwo.setText(R.string.mode_mav_title_sss);
			modeThree.setText(R.string.mode_motion_track_title_sss);
			modeFour.setText(R.string.mode_live_photo_title_sss);
			modeFive.setText(R.string.mode_photo_title_sss);
			((CameraAppUiImpl)ca.getCameraAppUI()).getPickerManager().hideOrShowDynamicView(true);
			ca.hideOrShowCameraHdrIcon(true);
			ca.hideOrShowCameraFlashIcon(false);
			((CameraActivity)ca).setShadeLayerBlackOrTransparent(false);
			CameraDeviceExt.setDisplaySize(PICTURE_RATIO_16_9);     
			((CameraActivity)ca).setTopAndBottomHeight(0, 0);
			((CameraActivity)ca).hideCameraTopAndBottomView();
			((CameraActivity)ca).setSurfaceViewLocation(true);
			SettingManager.showOrHideIndicator(false);
			ShutterManager.doVideo();
			//ShutterManager.modifyVideoBtnBg(true, true);
			Log.i("forcetouch","setDefaultCameraModeView()   mode="+mode+"   cameraId="+cameraId); 
			break;
		default:
			break;
		}
		tempModePosition =mode;
    }
    /*End:added by xss for ForceTouch*/	
}
