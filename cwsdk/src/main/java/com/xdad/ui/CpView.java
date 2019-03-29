package com.xdad.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xdad.ImgRes;
import com.xdad.http.TrackUtil;
import com.xdad.ui.CpScroll.OnPageChanged;
import com.xdad.util.CpUtils;
import com.xdad.util.Lg;

public class CpView extends FrameLayout {
	
	
	public interface CpEventListener{
		void onclickcp(int index);
		void onclosecp();
	}
	
	
	private CpEventListener cplistener;
	
	private Context ctx;
	
	private static int closecapturearea=100;

	private CpScroll adview;
	
	//点击插屏外是否关闭：1为关闭，0为不关闭
	public static int closeCpOnOutside = 0;
	
	public CpView(Context context) {
		super(context);
		ctx = getContext();
//		setBackgroundColor(Color.BLUE);
		
		System.out.println("newcpview--------------");
		//点击插屏外
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(closeCpOnOutside==1)
				{
					onclosecp();
				}
			}
		});
	}
	
	
	public static void setclosecapturearea(int a)
	{
		closecapturearea = 100;
//		closecapturearea = Math.max(1, Math.min(100, a));
	}
	
	
	public void setCpEventListener(CpEventListener l)
	{
		this.cplistener=l;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		TrackUtil.getclicklocation(this, event);
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		TrackUtil.getclicklocation(this, ev);
		return super.onInterceptTouchEvent(ev);
	}
	
	private Bitmap getclosebitmap()
	{
		
		if(true)
		{
			Bitmap bm = ImgRes.bmClose;
			if(bm!=null)return bm;
		}
		
		
		int w;
		w = CpUtils.dip2px(getContext(), 50);
		Bitmap bm = Bitmap.createBitmap(w, w, Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		Paint paint = new Paint();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(0xffb3b3b3);
		int stroke = CpUtils.dip2px(getContext(), 2);
		paint.setStrokeWidth(stroke);
		int r = w / 2;

		int jianxi = CpUtils.dip2px(getContext(), 1) + stroke;

		c.drawLine(r - r * (float) Math.cos(Math.PI / 4) + jianxi, r - r
				* (float) Math.cos(Math.PI / 4) + jianxi,
				r + r * (float) Math.cos(Math.PI / 4) - jianxi, r + r
						* (float) Math.cos(Math.PI / 4) - jianxi, paint);
		c.drawLine(r + r * (float) Math.cos(Math.PI / 4) - jianxi, r - r
				* (float) Math.cos(Math.PI / 4) + jianxi,
				r - r * (float) Math.cos(Math.PI / 4) + jianxi, r + r
						* (float) Math.cos(Math.PI / 4) - jianxi, paint);

		paint.setStyle(Style.STROKE);
		c.drawArc(new RectF(stroke / 2, stroke / 2, w - stroke / 2, w - stroke
				/ 2), 0, 360, true, paint);
		return bm;
	}
	
	
	private void onclickcp(int index)
	{
		Lg.d("onclickcp------");
		if(cplistener!=null)
		{
			cplistener.onclickcp(index);
		}
	}
	
	private void onclosecp()
	{
		Lg.d("onclosecp------");
		if(cplistener!=null)
		{
			cplistener.onclosecp();
		}
	}

	public void setBitmap(Bitmap[] images)
	{
		
		removeAllViews();
		
//		setBackgroundColor(Color.RED);
		setLayoutParams(new ViewGroup.LayoutParams(CpUtils.getscreenwidth(ctx), CpUtils.getscreenheight(ctx)- 100));
		
		
		Bitmap closebm = getclosebitmap();
		
		FrameLayout layout = new FrameLayout(ctx);
		int p = CpUtils.dip2px(ctx, 15);
		FrameLayout.LayoutParams lp2 =new FrameLayout.LayoutParams(-2, -2);
		lp2.gravity= Gravity.CENTER;
		
		addView(layout, lp2);
		
		adview = new CpScroll(getContext());
		ImageView[] ads = new ImageView[images.length];
		int i = 0;
		for (Bitmap bm:images)
		{
			ImageView ad = new ImageView(getContext());
			ad.setImageBitmap(bm);
//			ad.setScaleType(ScaleType.FIT_XY);
			
			ad.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int cc = adview.getChildCount();
					for(int i=0;i<cc;i++)
					{
						if(adview.getChildAt(i)==v)
						{
							onclickcp(i);
							break;
						}
					}
				}
			});
			
			ads[i++] = ad;
		}
		adview.setviews(ads);
		
		Bitmap first = images[0];
		int bmw = first.getWidth();
		int bmh = first.getHeight();
		
		int w = (int) (Math.min(CpUtils.getscreenwidth(ctx), CpUtils.getscreenheight(ctx))*7f/10f);
		int h = w;
		
		int sw = CpUtils.getscreenwidth(ctx);
		int sh = CpUtils.getscreenheight(ctx);
		if(sw<sh)
		{
			//横 
//			if(bmh*1f/bmw > sh*1.f/sw)
//			{
//				
//			}
			
			if(bmh*1f/bmw > 1.2)
			{
				// 图片长方形
				w = (int) (sw * 0.7f);
			}
			else 
			{
				w = (int) (sw * 0.8f);
			}
			
		}
		else
		{
			//竖
			if(bmh*1f/bmw > 1.3)
			{
				w = (int) (sh * 0.45f);
			}
			else 
			{
				w = (int) (sh * 0.6f);
			}
			
		}
		h =  (int) (1.f*bmh/bmw * w);
		
		LayoutParams lp = new LayoutParams(w,h); 
		int m = CpUtils.dip2px(ctx, 25);
//		m = 0;
//		lp.leftMargin = m;
//		lp.rightMargin = m;
//		lp.topMargin = m;
//		lp.bottomMargin = m;
		layout.addView(adview, lp);
		
		//indicator
		final LinearLayout llindex = new LinearLayout(getContext());
		if(ads.length>1)
			for(int k=0;k<ads.length;k++)
			{
				ImageView iv = new ImageView(getContext());
				iv.setBackgroundColor(Color.GRAY);
				LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(10, 10);
				p2.leftMargin = 10;
				llindex.addView(iv, p2);
			}
		lp = new LayoutParams(-2,-2);
		llindex.setPadding(10, 10, 10, 10);
		lp.gravity = Gravity.BOTTOM|Gravity.CENTER;
		layout.addView(llindex, lp);
		
		adview.setchangelistener(new OnPageChanged() {
			@Override
			public void onPageChanged(int index) {
				int cc = llindex.getChildCount();
				for(int i=0;i<cc;i++)
				{
					if(i==index){
						llindex.getChildAt(i).setBackgroundColor(Color.WHITE);
					}
					else
					{
						llindex.getChildAt(i).setBackgroundColor(Color.GRAY);
					}
				}
			}
		});
		
		ImageView logo = new ImageView(getContext());
		logo.setImageBitmap(ImgRes.bmLogo);
		 lp = new LayoutParams(-2, -2);
		 lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
		lp.width = (int)( Math.min(CpUtils.getscreenwidth(ctx), CpUtils.getscreenheight(ctx)) * 0.10f);
		lp.height = (int)(lp.width * 1.0f * ImgRes.bmLogo.getHeight() /  ImgRes.bmLogo.getWidth());
		logo.setScaleType(ImageView.ScaleType.FIT_XY);
//		logo.setBackgroundColor(Color.RED);
		layout.addView(logo, lp);
		
		
		FrameLayout closebtnfl = new FrameLayout(ctx);
		 lp = new LayoutParams(-2, -2);
		lp.gravity = Gravity.RIGHT;
		layout.addView(closebtnfl, lp);
		
		
		ImageView closebtn = new ImageView(ctx);
		int btnsize = CpUtils.dip2px(ctx, 28);
		btnsize =(int)( Math.min(CpUtils.getscreenwidth(ctx), CpUtils.getscreenheight(ctx)) * 0.07f);
		closebtnfl.addView(closebtn, btnsize, btnsize);
		closebtn.setImageBitmap(closebm);
		closebtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onclosecp();
			}
		});
		
		/*View capureview =new View(ctx);
//		capureview.setBackgroundColor(Color.BLUE);
		int w2 = (int) (closebm.getWidth() * closecapturearea/100f);
		FrameLayout.LayoutParams lp3 = new FrameLayout.LayoutParams(w2, w2);
		lp3.gravity=Gravity.CENTER;
		closebtnfl.addView(capureview, lp3);
		
		capureview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onclosecp();
			}
		});*/
		
		
		
	}
	

}
