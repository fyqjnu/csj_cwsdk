package com.xdad.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import com.xdad.util.Lg;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class GetStringHttp implements Runnable{
    
    private String url ;
    
    public OnResult listener ;
    
    public int statuscode=-1;
    
    
    public String data;
    
    
    public String ua;
    
    public String result;
    
    
    private Handler handler = new Handler(Looper.getMainLooper()){
        public void handleMessage(android.os.Message msg) {
            String s = (String) msg.obj;
            listener.onResult(GetStringHttp.this, s);
        };
    };
    
    public GetStringHttp(String url ){
    	this(url, null);
    }
    
    
    public GetStringHttp(String url, OnResult l)
    {
    	this.url = url;
    	listener = l;
    }

    static String readstream(InputStream is ) throws IOException{
    	if(is==null)return "";
        byte[] buf = new byte[1024*4];
        int len = -1;
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        while((len=is.read(buf))!=-1){
            ba.write(buf, 0, len);
        }
        return new String(ba.toByteArray());
    }
    
    
    public String runoncurrentthread()
    {
    	run();
    	return result;
    	
    }
    
    public void run() {

        InputStream is=null;
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setConnectTimeout(60*1000);
            conn.setReadTimeout(60*1000);
            
//            conn.addRequestProperty("Content-Type", "application/octet-stream; charset=utf-8" );
            
            if(ua!=null)
            {
            	String ua2 = URLDecoder.decode(ua, "utf-8");
                conn.addRequestProperty("User-Agent",ua2);
            }
            
//            conn.addRequestProperty("Accept-Charset", "UTF-8;");  
//            conn.addRequestProperty("User-Agent",  
//                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");  
//            HttpURLConnection.setFollowRedirects(false);
            
            if(data!=null)
            {
            	conn.setRequestMethod("POST");
            	conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
            }
            conn.connect(); 
            
            statuscode = conn.getResponseCode();
            
            if(Lg.d) Lg.d("http statuscode>>" + statuscode); 
            
            is = conn.getInputStream();
            
            result = readstream(is);
            
            if(Lg.d) Lg.d("http result>>" + result);
            
            conn.disconnect();
            
            
            
           /* HttpPost post =new HttpPost(url);
            ByteArrayEntity entity = new ByteArrayEntity(Util.e(data).getBytes());
            post.setEntity(entity);
            HttpClient client =new DefaultHttpClient();
            client.execute(post);
            
            InputStream content = post.getEntity().getContent();
            System.out.println(Util.d(readstream(content)));
            */
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(is!=null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(listener!=null ) 
            {
                Message msg = handler.obtainMessage();
                msg.obj= result;
                msg.sendToTarget();
            }
        }
        
        
        
    
    };
    
    
 /*   void trustallhost() {
        try {

            TrustManager tm = new X509TrustManager() {
                
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
                
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }
                
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[] { tm }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/
    
    public void execute(){
        new Thread(this).start();
    }
    
    

}
