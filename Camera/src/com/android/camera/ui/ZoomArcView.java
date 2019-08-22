package com.android.camera.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.android.gallery3d.R;
import com.hskj.iphoneutil.FeatureOption;
//FeatureOption.CENON_HD

public class ZoomArcView extends View implements View.OnTouchListener {
	private int mZoomRateIndex = 0;
	private float screenWidth;
	private float startPosition;
	private float endPosition;

	public ZoomArcView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ZoomArcView(Context context, AttributeSet attr) {
		super(context, attr);
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		screenWidth = wm.getDefaultDisplay().getWidth();
	}
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);  
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth((float) 4.0); 
		PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);  
		paint.setPathEffect(effects);
		float ss = getResources().getDimension(R.dimen.zoom_arc_rectf_left);
		//android.util.Log.d("wyy", "aaaaaaaaaa = "+ss);  
		RectF oval=new RectF();                     //RectF对象  
	       oval.left=getResources().getDimension(R.dimen.zoom_arc_rectf_left);                              //左边  
	       oval.top=getResources().getDimension(R.dimen.zoom_arc_rectf_top);                                  //上边  
	       oval.right=getResources().getDimension(R.dimen.zoom_arc_rectf_right);                             //右边  
	       oval.bottom=getResources().getDimension(R.dimen.zoom_arc_rectf_bottom);                    //下边  
	       //canvas.drawArc(oval, (float)(270 - mZoomRateIndex*8.3), 83, false, paint);
	       //mZoomRateIndex = 9;
		switch(mZoomRateIndex) {
			case 0:
				startPosition = 5;
				endPosition = -30;
				canvas.drawArc(oval, (float)(270 - mZoomRateIndex*8.3 + startPosition), 83+endPosition, false, paint);   
				break;
			case 1:
				startPosition = 0;
				endPosition = -78;
				canvas.drawArc(oval, (float)(270 - 1*8.3 + startPosition), 83+endPosition, false, paint);   
				canvas.drawArc(oval, (float)(275), (float)55, false, paint); 
				paint = new Paint();
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.STROKE);  
				paint.setColor(Color.WHITE);
				//paint.setStrokeWidth((float) 0.5); 
				paint.setTextSize(FeatureOption.CENON_HD ? 20 : 35);
				canvas.rotate(FeatureOption.CENON_HD ? 345 : 345, 
					FeatureOption.CENON_HD ? 270 : 400,
					FeatureOption.CENON_HD ? 100 : 150);
			      canvas.drawText("1x", 
					FeatureOption.CENON_HD ? 270 : 400,
					FeatureOption.CENON_HD ? 100 : 150,
					paint);  
			      canvas.rotate(FeatureOption.CENON_HD ? -345 : -345, 
					FeatureOption.CENON_HD ? 270 : 400,
					FeatureOption.CENON_HD ? 100 : 150); 
				break;
			case 2:
				canvas.drawArc(oval, (float)(270 - mZoomRateIndex*8.3), (float)12.1, false, paint);
				canvas.drawArc(oval, (float)(275), (float)55, false, paint);
				paint = new Paint();
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.STROKE);  
				paint.setColor(Color.WHITE);
				//paint.setStrokeWidth((float) 0.5); 
				paint.setTextSize(FeatureOption.CENON_HD ? 20 : 35);
			       canvas.rotate(FeatureOption.CENON_HD ? 341 : 336,
					FeatureOption.CENON_HD ? 210 : 315,
					FeatureOption.CENON_HD ? 115 : 175);  
			       canvas.drawText("1x", 
					FeatureOption.CENON_HD ? 210 : 315,
					FeatureOption.CENON_HD ? 115 : 175,
					paint);   
			       canvas.rotate(FeatureOption.CENON_HD ? -341 : -336, 
					FeatureOption.CENON_HD ? 210 : 315,
					FeatureOption.CENON_HD ? 115 : 175);
				break;
			case 3:
				canvas.drawArc(oval, (float)(270 - mZoomRateIndex*8.3), (float)19.9, false, paint);
				canvas.drawArc(oval, (float)(275), (float)53.1, false, paint);
				paint = new Paint();
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.STROKE);  
				paint.setColor(Color.WHITE);
				//paint.setStrokeWidth((float) 0.5); 
				paint.setTextSize(FeatureOption.CENON_HD ? 20 : 35);
			      canvas.rotate(FeatureOption.CENON_HD ? 335 : 325,
					FeatureOption.CENON_HD ? 160 : 235,
					FeatureOption.CENON_HD ? 138 : 218);  
			       canvas.drawText("1x", 
					FeatureOption.CENON_HD ? 160 : 235,
					FeatureOption.CENON_HD ? 138 : 218,
					paint);   
			       canvas.rotate(FeatureOption.CENON_HD ? -335 : -325, 
					FeatureOption.CENON_HD ? 160 : 235,
					FeatureOption.CENON_HD ? 138 : 218);
				break;
			case 4:
				canvas.drawArc(oval, (float)(270 - mZoomRateIndex*8.3), (float)28.2, false, paint);
				canvas.drawArc(oval, (float)(275), (float)44.8, false, paint);
				paint = new Paint();
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.STROKE);  
				paint.setColor(Color.WHITE);
				//paint.setStrokeWidth((float) 0.5); 
				paint.setTextSize(FeatureOption.CENON_HD ? 20 : 35);
			       canvas.rotate(FeatureOption.CENON_HD ? 325 : 325,
					FeatureOption.CENON_HD ? 113 : 168,
					FeatureOption.CENON_HD ? 168 : 258);  
			       canvas.drawText("1x", 
					FeatureOption.CENON_HD ? 113 : 168,
					FeatureOption.CENON_HD ? 168 : 258,
					paint);   
			       canvas.rotate(FeatureOption.CENON_HD ? -325 : -325, 
					FeatureOption.CENON_HD ? 113 : 168,
					FeatureOption.CENON_HD ? 168 : 258);
			    
			       canvas.rotate(FeatureOption.CENON_HD ? 50 : 50,
					FeatureOption.CENON_HD ? 655 : 990,
					FeatureOption.CENON_HD ? 227 : 357);  
			       canvas.drawText("10x", 
					FeatureOption.CENON_HD ? 655 : 990,
					FeatureOption.CENON_HD ? 227 : 357,
					paint);   
			       canvas.rotate(FeatureOption.CENON_HD ? -50 : -50, 
					FeatureOption.CENON_HD ? 655 : 990,
					FeatureOption.CENON_HD ? 227 : 357);
				break;
			case 5:
				canvas.drawArc(oval, (float)(270 - mZoomRateIndex*8.3), 36.5f, false, paint);
				canvas.drawArc(oval, (float)(275), 36.5f, false, paint);
				paint = new Paint();
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.STROKE);  
				paint.setColor(Color.WHITE);
				//paint.setStrokeWidth((float) 0.5); 
				paint.setTextSize(FeatureOption.CENON_HD ? 20 : 35);
			       canvas.rotate(FeatureOption.CENON_HD ? 318 : 318,
					FeatureOption.CENON_HD ? 75 : 110,
					FeatureOption.CENON_HD ? 208 : 313);  
			       canvas.drawText("1x", 
					FeatureOption.CENON_HD ? 75 : 110,
					FeatureOption.CENON_HD ? 208 : 313,
					paint);   
			       canvas.rotate(FeatureOption.CENON_HD ? -318 : -318, 
					FeatureOption.CENON_HD ? 75 : 110,
					FeatureOption.CENON_HD ? 208 : 313);
			    
			       canvas.rotate(FeatureOption.CENON_HD ? 50 : 50,
					FeatureOption.CENON_HD ? 625 : 930,
					FeatureOption.CENON_HD ? 198 : 287);  
			       canvas.drawText("10x", 
					FeatureOption.CENON_HD ? 625 : 930,
					FeatureOption.CENON_HD ? 198 : 287,
					paint);   
			       canvas.rotate(FeatureOption.CENON_HD ? -50 : -50, 
					FeatureOption.CENON_HD ? 625 : 930,
					FeatureOption.CENON_HD ? 198 : 287);
				break;
			case 6:
				canvas.drawArc(oval, (float)(270 - mZoomRateIndex*8.3), 44.8f, false, paint);
				canvas.drawArc(oval, (float)(275), 28.2f, false, paint);
				paint = new Paint();
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.STROKE);  
				paint.setColor(Color.WHITE);
				//paint.setStrokeWidth((float) 0.5); 
				paint.setTextSize(FeatureOption.CENON_HD ? 20 : 35);
			       canvas.rotate(FeatureOption.CENON_HD ? 313 : 308,
					FeatureOption.CENON_HD ? 38 : 58,
					FeatureOption.CENON_HD ? 248 : 383);  
			       canvas.drawText("1x", 
					FeatureOption.CENON_HD ? 38 : 58,
					FeatureOption.CENON_HD ? 248 : 383,
					paint);   
			       canvas.rotate(FeatureOption.CENON_HD ? -313 : -308, 
					FeatureOption.CENON_HD ? 38 : 58,
					FeatureOption.CENON_HD ? 248 : 383);
			    
			       canvas.rotate(FeatureOption.CENON_HD ? 45 : 45,
					FeatureOption.CENON_HD ? 578 : 865,
					FeatureOption.CENON_HD ? 150 : 240);  
			       canvas.drawText("10x", 
					FeatureOption.CENON_HD ? 578 : 865,
					FeatureOption.CENON_HD ? 150 : 240,
					paint);   
			       canvas.rotate(FeatureOption.CENON_HD ? -45 : -45, 
					FeatureOption.CENON_HD ? 578 : 865,
					FeatureOption.CENON_HD ? 150 : 240);
				break;
			case 7:
				canvas.drawArc(oval, (float)(270 - mZoomRateIndex*8.3), 53.1f, false, paint);
				canvas.drawArc(oval, (float)(275), 19.9f, false, paint);
				paint = new Paint();
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.STROKE);  
				paint.setColor(Color.WHITE);
				//paint.setStrokeWidth((float) 0.5); 
				paint.setTextSize(FeatureOption.CENON_HD ? 20 : 35);
				canvas.rotate(FeatureOption.CENON_HD ? 35 : 35,
					FeatureOption.CENON_HD ? 528 : 800,
					FeatureOption.CENON_HD ? 135 : 195);  
			       canvas.drawText("10x", 
					FeatureOption.CENON_HD ? 528 : 800,
					FeatureOption.CENON_HD ? 135 : 195,
					paint);   
			       canvas.rotate(FeatureOption.CENON_HD ? -35 : -35, 
					FeatureOption.CENON_HD ? 528 : 800,
					FeatureOption.CENON_HD ? 135 : 195);
				break;
			case 8:
				canvas.drawArc(oval, (float)(270 - 7*8.3), 53.1f, false, paint);
				canvas.drawArc(oval, (float)(275), 11.6f, false, paint);
				paint = new Paint();
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.STROKE);  
				paint.setColor(Color.WHITE);
				//paint.setStrokeWidth((float) 0.5); 
				paint.setTextSize(FeatureOption.CENON_HD ? 20 : 35);
				canvas.rotate(FeatureOption.CENON_HD ? 31 : 21,
					FeatureOption.CENON_HD ? 472 : 710,
					FeatureOption.CENON_HD ? 102 : 157);  
			       canvas.drawText("10x", 
					FeatureOption.CENON_HD ? 472 : 710,
					FeatureOption.CENON_HD ? 102 : 157,
					paint);   
			       canvas.rotate(FeatureOption.CENON_HD ? -31 : -21, 
					FeatureOption.CENON_HD ? 472 : 710,
					FeatureOption.CENON_HD ? 102 : 157);
				break;
			case 9:
				//canvas.drawArc(oval, (float)(270 - mZoomRateIndex*8.3), 83, false, paint);
				canvas.drawArc(oval, (float)(270 - 7*8.3), 53.1f, false, paint);
				canvas.drawArc(oval, (float)(275), 3.3f, false, paint);
				paint = new Paint();
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.STROKE);  
				paint.setColor(Color.WHITE);
				//paint.setStrokeWidth((float) 0.5); 
				paint.setTextSize(FeatureOption.CENON_HD ? 20 : 35);
				canvas.rotate(FeatureOption.CENON_HD ? 26 : 16,
					FeatureOption.CENON_HD ? 422 : 640,
					FeatureOption.CENON_HD ? 90 : 140);  
			       canvas.drawText("10x", 
					FeatureOption.CENON_HD ? 422 : 640,
					FeatureOption.CENON_HD ? 90 : 140,
					paint);   
			       canvas.rotate(FeatureOption.CENON_HD ? -26 : -16, 
					FeatureOption.CENON_HD ? 422 : 640,
					FeatureOption.CENON_HD ? 90 : 140); 
				break;
			case 10:
				//canvas.drawArc(oval, (float)(270 - mZoomRateIndex*8.3), 83, false, paint);
				canvas.drawArc(oval, (float)(270 - 7*8.3), 53.1f, false, paint);
				break;
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// TODO Auto-generated method stub
		float x = 0;
		float y = 0;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x = event.getX();
			y = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			mZoomRateIndex += (int)((event.getX()-x)/140);
			if(mZoomRateIndex < 0) {
				mZoomRateIndex = 0;
			} else if(mZoomRateIndex > 10) {
				mZoomRateIndex = 10;
			}
			invalidate();
			break;

		default:
			break;
		}
		return true;
	}

        public void setZoomRateIndex(int i) {
		mZoomRateIndex = i;
		invalidate();
	}
	
	public void addZoomRateIndex() {
		mZoomRateIndex++;
		if(mZoomRateIndex > 10) {
			mZoomRateIndex = 10;
		}
		invalidate();
	}
	
	public void delZoomRateIndex() {
		mZoomRateIndex--;
		if(mZoomRateIndex < 0) {
			mZoomRateIndex = 0;
		}
		invalidate();
	}

}
