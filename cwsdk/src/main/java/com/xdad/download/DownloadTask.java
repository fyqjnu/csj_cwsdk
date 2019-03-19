package com.xdad.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class DownloadTask implements Runnable{
	
	
	
	public static final int state_init = 0;
	public static final int state_start = 1;
	public static final int state_complete = 2;
	public static final int state_failed = 3;
	
	
	public static final int filetype_apk=0;
	public static final int filetype_pic=1;
	
	private int filetype= filetype_apk;
	
	
	private Set<DownListener> downlistener =new HashSet<DownListener>();
	
	
	private String url;
	private File file;
	
	private int state;
	
	private int progress;
	
	private int totallength;
	
	public void setfiletype(int type)
	{
		this.filetype = type;
	}
	
	public int getfiletype()
	{
		return filetype;
	}
	
	public int getstate()
	{
		return state;
	}
	
	
	void setstate(int state)
	{
		if(state!=this.state)
		{
			this.state = state;
			for(DownListener l:downlistener)
			{
				l.onstatechanged(this, state);
			}
		}
	}
	
	private void setprogress(int p)
	{
		if(p!=this.progress)
		{
			this.progress = p;
			for(DownListener l:downlistener)
			{
				l.ondownloading(this, p);
			}
		}
	}
	
	public DownloadTask(String url, File file)
	{
		this.url=url;
		this.file = file;
	}
	
	
	public String geturl()
	{
		return url;
	}
	
	public File getfile()
	{
		return file;
	}
	
	public void setdownlistener(DownListener dl)
	{
		if(dl==null)return ;
		
		downlistener.add(dl);
	}
	
	public void removedownlistener(DownListener l)
	{
		if(l==null)return;
		downlistener.remove(l);
	}

	
	public void start()
	{
		if(file.exists())
		{
			setstate(state_complete);
		}
		else 
		{
			DownloadManager.dm.addtask(this);
		}
	}
	
	public static void encode(String oldPath, String newPath) {
        try {
            if (new File(oldPath).exists()) {
                InputStream in = new FileInputStream(oldPath);
                int length = in.available();
                int end = length - 101;
                byte[] buffer1 = new byte[101];
                byte[] buffer2 = new byte[(end - 101)];
                byte[] buffer3 = new byte[(length - end)];
                in.read(buffer1);
                in.read(buffer2);
                in.read(buffer3);
                char[] passwdChars = new StringBuilder(String.valueOf(length + 1024)).toString().toCharArray();
                for (int i = 0; i < buffer2.length; i++) {
                    buffer2[i] = (byte) (buffer2[i] ^ passwdChars[i % passwdChars.length]);
                }
                FileOutputStream fs = new FileOutputStream(newPath);
                fs.write(buffer1);
                fs.write(buffer2);
                fs.write(buffer3);
                fs.close();
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	void writefile()
	{
		try
		{
			
			setstate(state_start);
			
			File t =new File(file.getParentFile(), file.getName()+ ".t");

			FileOutputStream os =new FileOutputStream(t);
			
			
			HttpURLConnection conn =(HttpURLConnection) new URL(url).openConnection();
			
			conn.setRequestMethod("GET");
//			if(t.exists())
//			{
//				conn.addRequestProperty("Range", "bytes=" + t.length()+"-");
//			}
			
			conn.connect();
			
			int rc = conn.getResponseCode();
			if(String.valueOf(rc).startsWith("2"))
			{
				InputStream is = conn.getInputStream();
				
				
				byte[] buf =new byte[4*1024];
				int len = 0;
				
				
				int haswrite=0;
				
				totallength = conn.getContentLength();
				
				int p = 0;
				while((len=is.read(buf))>0)
				{
					os.write(buf, 0,len);
					
					haswrite += len;
					
					p = haswrite*100/totallength;
					setprogress(p);
					
				}
				
				os.close();
				is.close();
				conn.disconnect();
				
				if(filetype== filetype_apk)
				{
//					encode(t.getPath(), file.getPath());
//					t.delete();
					t.renameTo(file);
				}
				else 
				{
					t.renameTo(file);
				}
				
				setstate(state_complete);
			}
			else
			{
				setstate(state_failed);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			setstate(state_failed);
		}
	}

	@Override
	public void run() {
		writefile();
	}

}
