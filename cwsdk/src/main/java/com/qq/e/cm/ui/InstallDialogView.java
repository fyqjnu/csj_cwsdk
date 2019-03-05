package com.qq.e.cm.ui;

import java.io.File;
import java.text.DecimalFormat;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qq.e.cm.util.CpUtils;
import com.qq.e.cm.util.Lg;


//安装对话框
public class InstallDialogView extends FrameLayout {

	
	public interface OnCancelDialog{
		void onremove();
	}
	
	private OnCancelDialog onCancelDialog;
	
	
	
	private File[] apkfiles;
	
	
	private Context ctx;
	
	private int color = 0xffff8402;
	
	private int colorpressed = 0xffd66e00;
	
	public InstallDialogView(Context context, File[] files) {
		super(context);
		this.apkfiles = files;
		
		ctx = getContext();
		init();
	}

	
	public void setOnCancelDialog(OnCancelDialog listener)
	{
		this.onCancelDialog = listener;
	}
	
	private void addclosebtn()
	{
		ImageView ivclose = new ImageView(ctx);
		int w = getpix(23);
		Bitmap b = Bitmap.createBitmap(w , w, Config.ARGB_8888);
		Canvas c = new Canvas(b);
		
		Paint paint =new Paint();
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(getpix(4));
		c.drawLine(0, 0, w, w, paint);
		c.drawLine(w, 0, 0, w, paint);
		
		FrameLayout.LayoutParams lpclose =new LayoutParams(-2, -2);
		lpclose.gravity= Gravity.RIGHT;
		lpclose.topMargin= lpclose.rightMargin = getpix(1);
		addView(ivclose, lpclose);
		ivclose.setImageBitmap(b);
		
		ivclose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(onCancelDialog!=null)
				{
					onCancelDialog.onremove();
				}
			}
		});
	}
	
	
	
	private void init() {
		removeAllViews();
		if(apkfiles == null)
			return ;

		setBackgroundColor(0xffffffff);
		
		
		
		LinearLayout parent = new LinearLayout(ctx);
		
		parent .setOrientation(1);
		
		addView(parent);
		int p;
		
		TextView tvtitle =new TextView(ctx);
		tvtitle.setBackgroundColor(color);
		p = getpix(10);
		tvtitle.setPadding(p/2, p, p, p);
		tvtitle.setText("检测到您已下载的应用需要安装");
		tvtitle.setTextSize(18);
		tvtitle.setTextColor(Color.WHITE);
		parent.addView(tvtitle);
		
		TextView tv2 = new TextView(ctx);
		tv2.setText("未安装应用占用资源，建议安装");
		tv2.setTextSize(15);
		p = getpix(5);
		tv2.setPadding(p, p, p, p);
		tv2.setTextColor(0xff990000);
		
		parent.addView(tv2);
		
		
		Lg.d(apkfiles.length);
		for(File f:apkfiles)
		{
			View line =new View(ctx);
			line.setBackgroundColor(0xffdddddd);
			parent.addView(line, -1, 1);
			
			
			
			
			Bitmap bmicon = null;
			String content= "content";
			PackageManager pm = ctx.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(f.getPath(), 0);
			info.applicationInfo.sourceDir = f.getPath();
			info.applicationInfo.publicSourceDir = f.getPath();
			String title  = ""+pm.getApplicationLabel(info.applicationInfo);
			
			DecimalFormat df =new DecimalFormat("大小：#.##M");
			content = df.format(f.length()/(1024*1024f));
			
			bmicon = CpUtils.getIconFromApk(ctx, f.getPath());
			
			LinearLayout layout = new LinearLayout(ctx);
			layout.setOrientation(0);
			layout.setGravity(Gravity.CENTER_VERTICAL);
			p=getpix(5);
			layout.setPadding(p, p, p, p);
			
			ImageView ivicon = new ImageView(ctx);
			ivicon.setImageBitmap(bmicon);
			LinearLayout layout1 = new LinearLayout(ctx);
			layout1.setOrientation(1);
			TextView tva =new TextView(ctx);
			tva.setTextColor(0xff333333);
			tva.setTextSize(16);
			TextView tvb =new TextView(ctx);
			tvb.setTextSize(12);
			
			tva.setText(title);
			tvb.setText(content);
			
			layout1.addView(tva);
			LinearLayout.LayoutParams lp3 =new LinearLayout.LayoutParams(-2, -2);
			lp3.topMargin=getpix(5);
			layout1.addView(tvb, lp3);
			
			
			TextView btn = new TextView(ctx);
			btn.setText("安装");
			btn.setGravity(Gravity.CENTER);
			btn.setTextColor(Color.WHITE);
			Drawable b = getbuttonbg();
			btn.setBackgroundDrawable(getbtndrawable());
			btn.setTag(f);
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				  File apk  = (File) v.getTag();
				  CpUtils.installapk(ctx, apk);
				  cancel();
				}
			});
			
			LinearLayout.LayoutParams lpa = new LinearLayout.LayoutParams(getpix(50), getpix(50));
			layout.addView(ivicon, lpa);
			
			lpa = new LinearLayout.LayoutParams(-2, -2);
			lpa.weight = 1;
			lpa.leftMargin = getpix(10);
			layout.addView(layout1,lpa);
			lpa = new LinearLayout.LayoutParams(getpix(60), getpix(33));
			
			layout.addView(btn, lpa);
			
			
			
			parent.addView(layout, -1, -2);
			
			
		}
		TextView tv3 = new TextView(ctx);
		p = getpix(5);
		tv3.setGravity(Gravity.CENTER);
		tv3.setPadding(p, p, p, p);
		tv3.setText("一键安装释放所有资源");
		tv3.setTextSize(15);
		tv3.setTextColor(0xffeeeeee);
		tv3.setBackgroundColor(0xff908e8e);
		parent.addView(tv3);
		
		LinearLayout layout1 =new LinearLayout(ctx);
		layout1.setGravity(Gravity.CENTER);
		parent.addView(layout1);

		Button btninstall = new Button(ctx);
		btninstall.setText("一键安装");
		btninstall.setTextColor(Color.WHITE);
		btninstall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for(File f:apkfiles)
				{
					CpUtils.installapk(ctx, f);
				}
				cancel();
			}
		});
		//007525
		
		GradientDrawable bg =new GradientDrawable();
		bg.setCornerRadius(10);
		bg.setColor(0xff007525);
		btninstall.setBackgroundDrawable(getbtndrawable());
		
		layout1.addView(btninstall, getpix(120), getpix(42));
		p = getpix(15);
		layout1.setPadding(p,p,p,p);
		
				
		
		addclosebtn();
		
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for(File f:apkfiles)
				{
					CpUtils.installapk(ctx, f);
				}
				cancel();
			}
		});
	}
	
	private Drawable getbtndrawable()
	{
		StateListDrawable d = new StateListDrawable();
		GradientDrawable pressedd =new GradientDrawable();
		pressedd.setCornerRadius(10);
		pressedd.setColor(colorpressed);
		d.addState(new int[]{android.R.attr.state_pressed}, pressedd);
		GradientDrawable normald =new GradientDrawable();
		normald.setCornerRadius(10);
		normald.setColor(color);
		d.addState(new int[]{android.R.attr.state_enabled}, normald);
		
		return d;
	}
	
	
	private void cancel()
	{
		if(onCancelDialog!=null)
		{
			onCancelDialog.onremove();
		}
	}
	
	
	private Drawable getbuttonbg()
	{
		GradientDrawable bg =new GradientDrawable();
		bg.setCornerRadius(10);
		bg.setColor(0xff007525);
		return bg;
	}

	
	private int getpix(int dp)
	{
		return CpUtils.dip2px(ctx, dp);
	}
	
	
	
}
