package com.qq.e.cm.util;

public class Lg {

	
	public static final boolean d = false;
	
	public static void d(Object o)
	{
		if(!d)return;
		Throwable t = new Throwable();
		t.fillInStackTrace();
		StackTraceElement[] st = t.getStackTrace();
		StackTraceElement ele = st[1];
		String prefix = ele.getClassName() + "." + ele.getMethodName() +"(" + ele.getLineNumber() + ")";
		
		
		if(o==null)
		{
			System.out.println("xxxx " +prefix + ">null");
		}
		else
		{
			System.out.println("xxxx " +prefix + ">" + o.toString());
		}
	}
	
}
