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

import android.content.Context;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.R;
import com.android.camera.Storage;
import com.android.camera.Util;

import java.util.Locale;
//added by lzp
import com.mediatek.storage.StorageManagerEx;
import android.os.StatFs;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.widget.Toast;


public class RemainingManager extends ViewManager implements CameraActivity.Resumable,
        CameraActivity.OnFullScreenChangedListener, CameraActivity.OnParametersReadyListener {
    private static final String TAG = "RemainingManager";
    
    private static final int TYPE_COUNT = 0;
    private static final int TYPE_TIME = 1;
    private static final Long REMAIND_THRESHOLD = 100L;
    
    private static final int[] MATRIX_REMAINING_TYPE = new int[ModePicker.MODE_NUM_ALL];
    static {
        MATRIX_REMAINING_TYPE[ModePicker.MODE_PHOTO] = TYPE_COUNT;
        MATRIX_REMAINING_TYPE[ModePicker.MODE_HDR] = TYPE_COUNT;
        MATRIX_REMAINING_TYPE[ModePicker.MODE_FACE_BEAUTY] = TYPE_COUNT;
        MATRIX_REMAINING_TYPE[ModePicker.MODE_PANORAMA] = TYPE_COUNT;
        MATRIX_REMAINING_TYPE[ModePicker.MODE_MAV] = TYPE_COUNT;
        MATRIX_REMAINING_TYPE[ModePicker.MODE_ASD] = TYPE_COUNT;
        MATRIX_REMAINING_TYPE[ModePicker.MODE_MOTION_TRACK] = TYPE_COUNT;
        MATRIX_REMAINING_TYPE[ModePicker.MODE_PHOTO_PIP] = TYPE_COUNT;
        MATRIX_REMAINING_TYPE[ModePicker.MODE_VIDEO] = TYPE_TIME;
        MATRIX_REMAINING_TYPE[ModePicker.MODE_VIDEO_PIP] = TYPE_TIME;
        MATRIX_REMAINING_TYPE[ModePicker.MODE_LIVE_PHOTO] = TYPE_TIME;
    }
    
    private TextView mRemainingView;
    private OnScreenHint mStorageHint;
    private WorkerHandler mWorkerHandler;
    private int mType = TYPE_COUNT;
    private String mRemainingText;
    private boolean mResumed;
    private boolean mParametersReady;
    private CamcorderProfile mProfile;
    
    private static final int MSG_UPDATE_STORAGE = 0;
    private Long mAvaliableSpace;
    
    private static final int DELAY_MSG_MS = 1500;
    
    private class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }
        
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_UPDATE_STORAGE:
                mAvaliableSpace = Storage.getAvailableSpace();
                removeMessages(MSG_UPDATE_STORAGE);
                sendEmptyMessageDelayed(MSG_UPDATE_STORAGE, DELAY_MSG_MS);
                break;
            default:
                break;
            }
        }
    }
    
    private Handler mMainHandler = new Handler();
    private CameraActivity mContext;
    
    public RemainingManager(CameraActivity context) {
        super(context);
        mContext = context;
        context.addResumable(this);
        context.addOnFullScreenChangedListener(this);
        context.addOnParametersReadyListener(this);
        // disable animation for cross with remaining.
        // disable fade in, in order to not affect setVisibility(View.Gone)
        setAnimationEnabled(false, false);
    }
    
    @Override
    public void begin() {
        if (mWorkerHandler == null) {
            HandlerThread t = new HandlerThread("thumbnail-creation-thread");
            t.start();
            mWorkerHandler = new WorkerHandler(t.getLooper());
            mWorkerHandler.sendEmptyMessage(MSG_UPDATE_STORAGE);
        }
    }
    
    @Override
    public void resume() {
        Log.d(TAG, "resume()");
        mResumed = true;
        showHint();
    }
    
    @Override
    public void pause() {
        Log.d(TAG, "pause()");
        mResumed = false;
        if (mStorageHint != null) {
            mStorageHint.cancel();
            mStorageHint = null;
        }
    }
    
    @Override
    public void finish() {
        if (mWorkerHandler != null) {
            mWorkerHandler.getLooper().quit();
        }
    }
    
    @Override
    protected View getView() {
        View view = inflate(R.layout.remaining);
        mRemainingView = (TextView) view.findViewById(R.id.remaining_view);
        return view;
    }
    
    @Override
    protected void onRefresh() {
        super.onRefresh();
        if (mRemainingView != null) {
            mRemainingView.setText(mRemainingText);
        }
    }
    
    public boolean showIfNeed() {
        if (mParametersReady) {
            if (mAvaliableSpace == null || mAvaliableSpace.longValue() <= 0) {
                mAvaliableSpace = Storage.getAvailableSpace();
            }
            long leftSpace = mAvaliableSpace.longValue();
            mType = MATRIX_REMAINING_TYPE[getContext().getCurrentMode() % ModePicker.OFFSET];
            leftSpace = computeStorage(leftSpace);
            updateStorageHint(leftSpace);
            updateRemainingView(REMAIND_THRESHOLD, leftSpace);
        }
        Log.d(TAG, "showIfNeed() return " + isShowing() + ", mParametersReady=" + mParametersReady);
        return isShowing();
    }
    
    public void show() {
        if (!isShowing()) {
            showAways();
        }
    }
    
    public boolean showAways() {
        if (mParametersReady) {
            if (mAvaliableSpace == null || mAvaliableSpace.longValue() <= 0) {
                mAvaliableSpace = Storage.getAvailableSpace();
            }
            long leftSpace = mAvaliableSpace.longValue();
            mType = MATRIX_REMAINING_TYPE[getContext().getCurrentMode() % ModePicker.OFFSET];
            leftSpace = computeStorage(leftSpace);
            updateStorageHint(leftSpace);
            updateRemainingView(Long.MAX_VALUE, leftSpace);
        }
        Log.d(TAG, "showAways() return " + isShowing() + ", mParametersReady=" + mParametersReady);
        return isShowing();
    }
    
    public void clearAvaliableSpace() {
        Log.d(TAG, "clearAvaliableSpace() mAvaliableSpace=" + mAvaliableSpace
                + ", mParametersReady=" + mParametersReady);
        mAvaliableSpace = null;
    }
    
    public void showHint() {
        boolean isCameraOpened = mContext.isCameraOpened();
        Log.d(TAG, "[showHint]mAvaliableSpace=" + mAvaliableSpace + ", mParametersReady="
                + mParametersReady + ",isCameraOpened = " + isCameraOpened);
        if (mParametersReady && isCameraOpened) {
            mMainHandler.post(new Runnable() { // delay for after resume
                        @Override
                        public void run() {
                            if (mAvaliableSpace == null || mAvaliableSpace.longValue() <= 0) {
                                mAvaliableSpace = Storage.getAvailableSpace();
                            }
                            long leftSpace = mAvaliableSpace.longValue();
                            leftSpace = computeStorage(leftSpace);
                            updateStorageHint(leftSpace);
                        }
                    });
        }
    }
    
    public long updateStorage() {
        Log.i(TAG, "updateStorage()");
        if (mAvaliableSpace == null || mAvaliableSpace.longValue() <= 0) {
            mAvaliableSpace = Storage.getAvailableSpace();
        }
        long leftSpace = mAvaliableSpace.longValue();
        return computeStorage(leftSpace);
    }
    
    public long getLeftStorage() {
        return Storage.getAvailableSpace() - Storage.LOW_STORAGE_THRESHOLD;
    }
    
    private long computeStorage(long avaliableSpace) {
        if (avaliableSpace > Storage.LOW_STORAGE_THRESHOLD) {
            avaliableSpace = (avaliableSpace - Storage.LOW_STORAGE_THRESHOLD)
                    / (mType == TYPE_COUNT ? pictureSize() : videoFrameRate());
        } else if (avaliableSpace > 0) {
            avaliableSpace = 0;
        }
        Storage.setLeftSpace(avaliableSpace);
        Log.d(TAG, "computeStorage(" + avaliableSpace + ") return " + avaliableSpace);
        return avaliableSpace;
    }
    
    private void updateRemainingView(long threshold, long leftSpace) {
        boolean needShow = (mType == TYPE_COUNT) ? (leftSpace <= threshold) : true;
        if (needShow) { // only show remaining view in some cases
            if (leftSpace < 0) {
                mRemainingText = mType == TYPE_COUNT ? stringForCount(0) : stringForTime(0);
            } else {
                mRemainingText = mType == TYPE_COUNT ? stringForCount(leftSpace)
                        : stringForTime(leftSpace);
            }
            super.show(); // show remaining view
        }
        Log.d(TAG, "updateRemainingView(" + threshold + ", " + leftSpace + ") mType=" + mType);
    }
    /**Begin: added by lzp **/
    private final static int STORAGE1_TO_STORAGE2 = 1,STORAGE2_TO_STORAGE1 = 2,CLEAN_STORAGE = 3;
    private final static String STORAGE0 = "/storage/sdcard0",STORAGE1 = "/storage/sdcard1";
    private final static boolean MTK_2SDCARD_SWAP = android.os.SystemProperties.get("ro.mtk_2sdcard_swap").equals("1");//added by lzp
    private static int noSpaceState = 0;
    private static int getNospaceState(){
		final String currentDefautlPath = StorageManagerEx.getDefaultPath();
        	String checkStoragePath = "";
		int nospace_state = 0;
		if(STORAGE0.equals(currentDefautlPath)) {
			checkStoragePath = STORAGE1;
			nospace_state = MTK_2SDCARD_SWAP ? STORAGE2_TO_STORAGE1 : STORAGE1_TO_STORAGE2;
		} else if(STORAGE1.equals(currentDefautlPath)) {
			checkStoragePath = STORAGE0;
			nospace_state = MTK_2SDCARD_SWAP ? STORAGE1_TO_STORAGE2 : STORAGE2_TO_STORAGE1;
		}
		
        	final StatFs checkStatFs = new StatFs(checkStoragePath);
		if(checkStatFs.getAvailableBytes() < Storage.LOW_STORAGE_THRESHOLD) {
			return CLEAN_STORAGE;
		}

        return nospace_state;
    }

    private static boolean isCurrentSpaceEnough(){
		final String currentDefautlPath = StorageManagerEx.getDefaultPath();
        final StatFs checkStatFs = new StatFs(currentDefautlPath);

        return checkStatFs.getAvailableBytes() > Storage.LOW_STORAGE_THRESHOLD;
    }
	/**End: added by lzp **/    

    private void updateStorageHint(long leftSpace) {
        Log.d(TAG, "updateStorageHint(" + leftSpace + ") isFullScreen="
                + getContext().isFullScreen());
        String message = null;
        if (leftSpace == Storage.UNAVAILABLE) {
            message = getContext().getString(R.string.no_storage);
        } else if (leftSpace == Storage.PREPARING) {
            message = getContext().getString(R.string.preparing_sd);
        } else if (leftSpace == Storage.UNKNOWN_SIZE) {
            message = getContext().getString(R.string.access_sd_fail);
        } else if (leftSpace <= 0) {
			/**Begin: modified  by lzp **/
			noSpaceState = getNospaceState();
			switch(noSpaceState) {
			case STORAGE1_TO_STORAGE2:
				message = getContext().getString(R.string.storage1_to_storage2);
				break;
			case STORAGE2_TO_STORAGE1:
				message = getContext().getString(R.string.storage1_to_storage2);
				break;
			case CLEAN_STORAGE:
				message = getContext().getString(R.string.clean_storage);
				break;
			}
			/**End: modified  by lzp **/
            //message = getContext().getString(Util.getNotEnoughSpaceAlertMessageId());
        }
		/**begin: mod by lzp on 20150630 **/
        if (message != null && getContext().isFullScreen() && !isCurrentSpaceEnough()) {//mod by lzp
			if(noSpaceState > 0) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
                    mContext, AlertDialog.THEME_DEVICE_DEFAULT_DARK );
				builder.setMessage(message);
				builder.setNegativeButton(
		                android.R.string.ok, new DialogInterface.OnClickListener() {
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                        switch(noSpaceState) {
									case STORAGE1_TO_STORAGE2:
										StorageManagerEx.setDefaultPath(MTK_2SDCARD_SWAP?STORAGE0:STORAGE1);
										break;
									case STORAGE2_TO_STORAGE1:
										StorageManagerEx.setDefaultPath(MTK_2SDCARD_SWAP?STORAGE1:STORAGE0);
										break;
									case CLEAN_STORAGE:
										Intent fileManagerIntent = new Intent();
										fileManagerIntent.setClassName("com.mediatek.filemanager","com.mediatek.filemanager.FileManagerOperationActivity");
										mContext.startActivity(fileManagerIntent);
										return;
								}
								Toast.makeText(mContext, R.string.storage_changed_toast, Toast.LENGTH_LONG).show();
		                    }
		                });

		        builder.setPositiveButton(android.R.string.cancel, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener(){
					public void onDismiss(DialogInterface dialog){
						mMainHandler.postDelayed(new Runnable() { // delay for after resume
							@Override
							public void run() {
							    showIfNeed();
							}
						    },500);
						
					}
				}); 
				
				builder.create().show();
			}else{
		        if (mStorageHint == null) {
		            mStorageHint = OnScreenHint.makeText(getContext(), message);
		        } else {
		            mStorageHint.setText(message);
		        }
		        mStorageHint.show();
			}
			/**End: mod by lzp on 20150630 **/
        } else if (mStorageHint != null) {
            mStorageHint.cancel();
            mStorageHint = null;
        }
    }
    
    public long pictureSize() {
        String pictureFormat;
        switch (getContext().getCameraActor().getMode()) {
        case ModePicker.MODE_PANORAMA:
            pictureFormat = "autorama";
            break;
        case ModePicker.MODE_MAV:
            pictureFormat = "mav";
            break;
        default:
            if (getContext().getParameters() != null) {
                Size size = getContext().getParameters().getPictureSize();
                String pictureSize = ((size.width > size.height) ? (size.width + "x" + size.height)
                        : (size.height + "x" + size.width));
                pictureFormat = pictureSize + "-" + "superfine";
            } else {
                pictureFormat = "2048x1360-superfine";
            }
            break;
        }
        long psize = Storage.getSize(pictureFormat);
        Log.d(TAG, "pictureSize() pictureFormat=" + pictureFormat + " return " + psize);
        return psize;
    }
    
    public void setCamcorderProfile(CamcorderProfile profile) {
        mProfile = profile;
    }
    
    public long videoFrameRate() {
            long bytePerMs = ((mProfile.videoBitRate + mProfile.audioBitRate) >> 3) / 1000;
            return bytePerMs;
    }
    
    private static String stringForTime(final long millis) {
        final int totalSeconds = (int) millis / 1000;
        final int seconds = totalSeconds % 60;
        final int minutes = (totalSeconds / 60) % 60;
        final int hours = totalSeconds / 3600;
        if (hours > 0) {
            return String.format(Locale.ENGLISH, "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
        }
    }
    
    private static String stringForCount(final long count) {
        return String.format("%d", count);
    }
    
    @Override
    public void onFullScreenChanged(boolean full) {
        Log.i(TAG, "onFullScreenChanged(" + full + ")");
        if (full) {
            resume();
        } else {
            pause();
        }
    }
    
    @Override
    public void onCameraParameterReady() {
        mParametersReady = true;
    }
    
    private class Holder {
        long mThreshold;
        long mLeftSpace;
        
        public Holder() {
        }
        
        public Holder(long threshold, long leftSpace) {
            mThreshold = threshold;
            mLeftSpace = leftSpace;
        }
        
        @Override
        public String toString() {
            return new StringBuilder().append("Holder(mThreshold=").append(mThreshold)
                    .append(", mLeftSpace=").append(mLeftSpace).append(")").toString();
        }
    }
}
