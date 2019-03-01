package com.cw.ui;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.cw.util.Lg;

public class CpScroll extends ViewGroup implements Runnable {
	
	public interface OnPageChanged {
		public void onPageChanged(int index);
	}
	
	OnPageChanged changelistener;
	
	
	Scroller scroller;
	
	int leftborder;
	
	int rightborder;
	
	int touchslop = 32;
	
	int autodirection = 0;
	
	int zouqi = 4000;
	
	int currentindex= 0;
	
	int duration = 1000;
	
	
	public void setchangelistener(OnPageChanged l)
	{
		changelistener = l;
	}
	
	public void setviews(View[] views)
	{
		if(Lg.d) System.out.println("viewsize>" + views.length);
		for(View view:views)
		{
			addView(view,-1,-1);
		}
	}
	

	public CpScroll(Context context) {
		super(context);
		scroller = new Scroller(getContext());
//		ViewConfiguration configuration = ViewConfiguration.get(getContext());
//		touchslop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
//		if(Lg.d) System.out.println("touchslop>>" + touchslop);
	}
	
	boolean hasstartauto;
	boolean hasmeasure;
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int cc = getChildCount();
		for(int i=0;i<cc;i++)
		{
			View cv = getChildAt(i);
			measureChild(cv, widthMeasureSpec, heightMeasureSpec);
		}
		
		if(!hasstartauto && cc> 1)
		{
			hasstartauto = true;
			postDelayed(this, zouqi);
		}
		hasmeasure= true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(changed)
		{
			int cc = getChildCount();
			for(int i=0;i<cc;i++)
			{
				View cv = getChildAt(i);
				cv.layout(i * cv.getMeasuredWidth(), 0, (i+1)*cv.getMeasuredWidth(), cv.getMeasuredHeight());
			}
			
			leftborder = getChildAt(0).getLeft();
			rightborder = getChildAt(cc-1).getRight();
		}
	}
	
	public int getcurrentpage()
	{
		return currentindex;
	}

	
	
	long lastscroll;
	
	@Override
	public void computeScroll() {
		
		if(scroller.computeScrollOffset())
		{
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			invalidate();
		}
		lastscroll = System.currentTimeMillis();
		int index = (getScrollX() + getWidth()/2) / getWidth();
		if(index !=currentindex){
			currentindex = index;
			if(changelistener!=null)
			{
				changelistener.onPageChanged(index);
			}
		}
	}
	
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		if(visibility != View.VISIBLE)
		{
			if(Lg.d) System.out.println("不可见");
			hasstartauto = false;
		}
		else 
		{
			if(Lg.d) System.out.println("可见");
			if(!hasstartauto && hasmeasure) 
			{
				hasstartauto = true;
				postDelayed(this, zouqi);
			}
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		switch(ev.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			
			lastdownx = ev.getRawX();
			
			break;
		case MotionEvent.ACTION_MOVE:
			float x = ev.getRawX();
			lastmovex = x;
			if(Math.abs(x - lastdownx) > touchslop)
			{
				return true;
			}
			break;
		}
		
		return super.onInterceptTouchEvent(ev);
	}
	
	
	
	float lastmovex;
	float lastdownx;
	
	boolean istouch;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction())
		{
		case MotionEvent.ACTION_MOVE:
			
			istouch =true;
			
			float x = event.getRawX();
			float dx = lastmovex - x;
			if(getScrollX() + dx < leftborder)
			{
				scrollTo(leftborder, 0);
				return true;
			}
			
			if(getScrollX() + getWidth() + dx > rightborder)
			{
				scrollTo(rightborder - getWidth(), 0);
				return true;
			}
			scrollBy((int)dx, 0);
			lastmovex = x;
			break;
		case MotionEvent.ACTION_UP:
			int targetindex = (getScrollX() + getWidth()/2) / getWidth();
			int dx2 = targetindex * getWidth() - getScrollX();
			scroller.startScroll(getScrollX(), 0, dx2, 0);
			invalidate();
			
			istouch = false;
			
			break;
		}
		
		return super.onTouchEvent(event);
	}

	@Override
	public void run() {
		if(getChildCount()<2)return;
		
		if(!hasstartauto)
		{
			return ;
		}
		
		
		if(istouch)
		{
			postDelayed(this, zouqi);
			return ;
		}
		
		if(System.currentTimeMillis() - lastscroll < 2000)
		{
			postDelayed(this, zouqi + lastscroll - System.currentTimeMillis());
			return;
		}
		
//		if(Lg.d) System.out.println("" + autodirection + "," + currentindex);
		if(autodirection == 0)
		{
			if(currentindex == getChildCount() -1 )
			{
				autodirection = 1;
				autoright();
			}
			else 
			{
				autoleft();
			}
		}
		else 
		{
			if(currentindex == 0)
			{
				autodirection = 0;
				autoleft();
			}
			else {
				autoright();
			}
		}
		
		postDelayed(this, zouqi);
	}
	
	void stopauto()
	{
		hasstartauto = false;
	}
	
	void autoright()
	{
//		scrollBy(-getWidth(), 0);
		scroller.startScroll(getScrollX(), 0, -getWidth(), 0, duration);
		invalidate();
	}
	
	void autoleft()
	{
//		scrollBy(getWidth(), 0);
		scroller.startScroll(getScrollX(), 0, getWidth(), 0, duration);
		invalidate();
	}
	
}
