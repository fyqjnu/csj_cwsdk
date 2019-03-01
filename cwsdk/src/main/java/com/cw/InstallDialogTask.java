package com.cw;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Random;

import com.cw.download.FileUtil;
import com.cw.ui.InstallDialogView;
import com.cw.ui.InstallDialogView.OnCancelDialog;
import com.cw.util.CpUtils;
import com.cw.util.Lg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.view.Window;


//安装提示对话框
public class InstallDialogTask implements OnCancelDialog {

	
	private static Handler h = new Handler(Looper.getMainLooper());
	private Context ctx;
	
	
	private long period;
	private Dialog dialog;
	
	public InstallDialogTask(Context c, long period)
	{
		this.ctx = c;
		this.period  = period;
	}
	
	private void trynext()
	{
		h.postDelayed(new Runnable() {
			@Override
			public void run() {
				start();
			}
		}, period);
	}
	
	public void start()
	{
		
		Lg.d("start");
		
		File getrootdir = FileUtil.getrootdir();
		
		if(getrootdir==null||!getrootdir.exists()){
			trynext();
			return ;
		}
		File[] files = getrootdir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
//				Lg.d(dir);
//				Lg.d(filename);
				if(filename.endsWith(".apk")){
					PackageInfo pi = ctx.getPackageManager().getPackageArchiveInfo(new File(dir, filename).getPath(), 0);
					if(pi!=null)
					{
						String pkg = pi.applicationInfo.packageName;
						if(!CpUtils.ispackageinstalled(ctx, pkg))
							return true;
					}
				}
				return false;
			}
		});
		Lg.d("" +files + (files==null?"null":files.length));
		if(files==null||files.length==0)
		{
			trynext();
			return ;
		}
		Lg.d("apksize>"+ files.length);
		
		if(files.length>2)
		{
			File[] select =new File[2];
			Random r = new Random();
			for(int i=0;i<select.length;i++)
			{
				int index = r.nextInt(files.length-i);
				select[i]= files[index];  
				
				if(index < files.length-1-i)
				{
					files[index ] = files[files.length-1-i];
				}
				
			}
			showdialog(select);
		}
		else
		{
			showdialog(files);
		}
		
		
	}
	
	private void removedialog()
	{
		Lg.d("remove dialog");
		if(dialog!=null)
		{
			if(dialog.isShowing()) dialog.cancel();
		}
		
		trynext();
	}
	
	
	private void showdialog(File[] files)
	{

		Activity topActivity = CpUtils.getTopActivity();
		if(topActivity==null)
		{
			trynext();
			return ;
		}
		Context ctx = topActivity;
		dialog = new Dialog(ctx){
			@Override
			public void onBackPressed() {
			}
		};
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setFeatureInt(Window.FEATURE_NO_TITLE, Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		InstallDialogView view = new InstallDialogView(ctx, files);
		view.setOnCancelDialog(this);
		int w = (int) (CpUtils.getscreenwidth(ctx)*9/10f);
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(w, -2);
		dialog.setContentView(view, lp);
		dialog.show();
	}

	@Override
	public void onremove() {
		removedialog();
	}
	
	
}
