package com.suning.search.analyzer.jni;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);
    
    /**
     * url请求方法
     * @param urlStr url字符串
     * @param timeout 超时时间
     * @param sign 检测字符串
     * @return 如果url返回结果包含sign且不超时，则返回true,否则返回false
     */
    public static boolean webgetForCheck(String urlStr,int timeout,String sign){
        try{
            String content=webget(urlStr,timeout);
            if(StringUtils.isNotBlank(content)){
                return StringUtils.containsIgnoreCase(content, sign);
            }else if(StringUtils.isBlank(sign) && StringUtils.isBlank(content)){
                return true;
            }
        }catch(Exception e){
            log.error("webgetForCheck方法执行出现异常!",e);
        }
        return false;
    }
    
    /**
     * url请求
     * @param urlStr
     * @param timeout
     * @return
     * @throws IOException
     */
    public static String webget(String urlStr, int timeout) throws IOException {
        HttpURLConnection conn = null;
        InputStream is = null;
        StringBuffer sb = new StringBuffer();
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            is = conn.getInputStream();
            int ch = is.read();
            while (ch != -1) {
                sb.append((char) ch);
                ch = is.read();
            }
        } catch (IOException e) {
            log.error("在访问链接"+urlStr+"时，出现异常！");
            e = new IOException("1");
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            try {
                if (null != is)
                    is.close();
            } catch (IOException e) {
                e = new IOException("2");
                throw e;
            }
        }
        return new String(sb.toString().getBytes(),"utf-8");
    }
    
    
    
    public static boolean asyncWebgetCheck(String urlStr, int timeout,String sign) throws IOException {
    	boolean isSuccess = true;
        HttpURLConnection conn = null;
        InputStream is = null;
        StringBuffer sb = new StringBuffer();
        try {
        	long time = System.currentTimeMillis();
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            is = conn.getInputStream();
            int ch = is.read();
            while (ch != -1) {
                sb.append((char) ch);
                ch = is.read();
                if (sb.toString().contains(sign)){
                	conn.disconnect();
                	isSuccess = true;
                	break;
                }
                if (System.currentTimeMillis()-time > timeout){
                	isSuccess = false;
					break;
				}
            }
        } catch (IOException e) {
            log.error("在访问链接"+urlStr+"时，出现异常！");
            e = new IOException("1");
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            try {
                if (null != is)
                    is.close();
            } catch (IOException e) {
                e = new IOException("2");
                isSuccess = false;
                throw e;
            }
        }
        
        return isSuccess;
    }
    /**
     * url请求
     * @param urlStr
     * @param timeout
     * @return
     * @throws IOException
     */
    public static String webgetForUTF8(String urlStr, int timeout) throws IOException {
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream bis=new ByteArrayOutputStream();
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            is = conn.getInputStream();
            int ch = is.read();
            while (ch != -1) {
                bis.write(ch);
                ch = is.read();
            }
        } catch (IOException e) {
            log.error("在访问链接"+urlStr+"时，出现异常！");
            e = new IOException("1");
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            try {
                if (null != is)
                    is.close();
            } catch (IOException e) {
                e = new IOException("2");
                throw e;
            }
        }
        return bis.toString("utf-8").trim();
    }
}
