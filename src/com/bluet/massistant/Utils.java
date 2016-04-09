package com.bluet.massistant;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
    public static void RunTaskOnWorkThread(final File file) {
    	new Thread(){
    		@Override
    		public void run(){
    			post(file.toString(), "http://115.28.178.225:3000/post");
    		}
    		}.start();
    }
	public static void UploadFile(File file) {
		RunTaskOnWorkThread(file);
   }
	public static String post(String pathToOurFile,String urlServer) {
	      HttpClient httpclient = new DefaultHttpClient();
	      //设置通信协议版本
	      httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	       
	      //File path= Environment.getExternalStorageDirectory(); //取得SD卡的路径
	       
	      //String pathToOurFile = path.getPath()+File.separator+"ak.txt"; //uploadfile
	      //String urlServer = "http://192.168.1.88/test/upload.php"; 
	       
	      HttpPost httppost = new HttpPost(urlServer);
	      File file = new File(pathToOurFile);
	   
	      MultipartEntity mpEntity = new MultipartEntity(); //文件传输
	      ContentBody cbFile = new FileBody(file);
	      mpEntity.addPart("userfile", cbFile); // <input type="file" name="userfile" />  对应的
	   
	   
	      httppost.setEntity(mpEntity);
	      System.out.println("executing request " + httppost.getRequestLine());
	       
	      HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";
		}
	      HttpEntity resEntity = response.getEntity();
	   
	      System.out.println(response.getStatusLine());//通信Ok
	      String json="";
	      String path="";
	      if (resEntity != null) {
	        //System.out.println(EntityUtils.toString(resEntity,"utf-8"));
	        try {
				json=EntityUtils.toString(resEntity,"utf-8");
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        JSONObject p=null;
	        try{
	            p=new JSONObject(json);
	            path=(String) p.get("path");
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	      }
	      if (resEntity != null) {
	        try {
				resEntity.consumeContent();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }
	   
	      httpclient.getConnectionManager().shutdown();
	      return path;
	    }
	
	public static void PostFile(File file) {
			HttpURLConnection con;  
			URL url = null;
			try {
				url = new URL("http://115.28.178.225:3000/post");
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String boundary = java.util.UUID.randomUUID().toString(); 
		    try {  

		        con = (HttpURLConnection) url.openConnection();  

		        con.setConnectTimeout(5000);  

		        /* 允许Input、Output，不使用Cache */  

		        con.setDoInput(true);  

		        con.setDoOutput(true);  

		        con.setUseCaches(false);  

		        /* 设置传送的method=POST */  

		        con.setRequestMethod("POST");  

		        /* setRequestProperty */  

		        con.setRequestProperty("Connection", "Keep-Alive");  

		        con.setRequestProperty("Charset", "UTF-8");  

		        con.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);  

		      

		        /* 设置DataOutputStream */  

		         DataOutputStream ds = new DataOutputStream(con.getOutputStream());  

		         FileInputStream fStream = new FileInputStream(file);  

		  

		          /* 设置每次写入1024bytes */  

		          int bufferSize = 1024;  

		          byte[] buffer = new byte[bufferSize];  

		  

		          int length = -1;  

		          /* 从文件读取数据至缓冲区 */  

		          while((length = fStream.read(buffer)) != -1)  

		          {  

		            /* 将资料写入DataOutputStream中 */  

		            ds.write(buffer, 0, length);  

		          }   

		          fStream.close();   

		          ds.flush();  

		          ds.close();  
		} catch(Exception e) {
			e.printStackTrace();
		}
		}
	}
