package com.suning.search.analyzer.jni;

import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Iterator;  
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class WebSegment {
	private static final Logger logger = LoggerFactory.getLogger(WebSegment.class);
	private static String WEB_SEGMENT_DOMAIN = null;
	static{
	    init();
	}
	public static void init(){
	    WEB_SEGMENT_DOMAIN = PropertiesUtil.loadProperties("file:/opt/search/jnisegment/websegment.properties").getProperty("WEB_SEGMENT_DOMAIN", "http://10.27.103.100:5201/");
		//WEB_SEGMENT_DOMAIN = "http://10.27.103.100:5201/";
	}
	public String[] SegmentMax(String sWord)
	{
		return RunWebSegment("ST_MAX", sWord);
	}
	
	public String[] SegmentOmni(String sWord)
	{
		return RunWebSegment("ST_OMNI", sWord);
	}
	
	public String[] SegmentProb(String sWord)
	{
		return RunWebSegment("ST_PROB", sWord);
	}
	
	public String[] SegmentProbOrig(String sWord)
	{
		return RunWebSegment("4", sWord);
	}
	
	public String[] SegmentChar(String sWord)
	{
		return RunWebSegment("ST_CHAR", sWord);
	}
	
	public String SimpleToTraditional(String word)
	{
		return RunWebSegmentSimpleTraditional(word);
	}
	
	public String TraditionalToSimple(String word)
	{
		return RunWebSegmentTraditionalSimple(word);
	}
	
	private static String RequestWebSegment(String sUrl)
	{
		try
		{
			return HttpUtils.webgetForUTF8(sUrl, 10000);
	    }catch(Exception e){
	    	logger.error("wegsegment.wegGet方法执行出现异常!",e);
	    }
		return "";
	}
	
	private String[] GetResultVector(String sResultString)
	{
		JSONObject jsonObject;
		String[] vResultVector = null;
		try
		{
			jsonObject = JSONObject.parseObject(sResultString);
			if (jsonObject == null)
			{
				return new String[0];
			}
	        JSONArray query_seg = JSONArray.parseArray(jsonObject.getJSONArray("trans_res").toString());//JSON.parseArray(jsonStr);
	        vResultVector = new String[query_seg.size()];
	        int count = 0;
	        for (Iterator it = query_seg.iterator(); it.hasNext();) {  
	            JSONObject term = (JSONObject)it.next();  
	            String word = term.get("t").toString();
	            String start = term.get("o").toString();
	            String end = term.get("e").toString();
	            vResultVector[count] = "1:" + start + ":" + end + ":" + word; 
	            count++;
	        }
		}catch(Exception e){
	    	logger.error("wegsegment.GetResultVector方法执行出现异常!",e);
	    	return new String[0];
	    }

		return vResultVector;
	}
	
	private String GetResultTS(String sResultString)
	{
		JSONObject jsonObject;
		String result = "";
		try
		{
			jsonObject = JSONObject.parseObject(sResultString);
			if (jsonObject == null)
			{
				return result;
			}
			result = jsonObject.getString("trans_res");
		}catch(Exception e){
	    	logger.error("wegsegment.GetResultVector方法执行出现异常!",e);
	    }

		return result;
	}
	
	private String GetUrl(String sType, String sWord)
	{
		String url_word = sWord;
    	try {
    		url_word = URLEncoder.encode(url_word, "utf-8");
    		String cur_url = WEB_SEGMENT_DOMAIN + "?en=utf8&call_type=" + sType + "&q=" + url_word;
    		logger.info("call web-segment: " + cur_url);
    		return cur_url;
        } catch (Exception e) {
            return "";
        }
	}
	
	private String GetUrlTraditionalSimple(String sWord)
	{
		String url_word = sWord;
    	try {
    		url_word = URLEncoder.encode(url_word, "utf-8");
    		String cur_url = WEB_SEGMENT_DOMAIN + "?lang=tc&en=utf8&trans=1&q=" + url_word;
    		logger.info("call web-segment: " + cur_url);
    		return cur_url;
        } catch (Exception e) {
            return "";
        }
	}
	
	private String GetUrlSimpleTraditional(String sWord)
	{
		String url_word = sWord;
    	try {
    		url_word = URLEncoder.encode(url_word, "utf-8");
    		String cur_url = WEB_SEGMENT_DOMAIN + "?lang=sc&en=utf8&trans=1&q=" + url_word;
    		logger.info("call web-segment: " + cur_url);
    		return cur_url;
        } catch (Exception e) {
            return "";
        }
	}

	private String[] RunWebSegment(String sType, String sWord)
	{
		String sUrl = GetUrl(sType, sWord);
		String sResult = RequestWebSegment(sUrl);
		return GetResultVector(sResult);
	}
	
	private String RunWebSegmentTraditionalSimple(String sWord)
	{
		String sUrl = GetUrlTraditionalSimple(sWord);
		String sResult = RequestWebSegment(sUrl);
		return GetResultTS(sResult);
	}
	
	private String RunWebSegmentSimpleTraditional(String sWord)
	{
		String sUrl = GetUrlSimpleTraditional(sWord);
		String sResult = RequestWebSegment(sUrl);
		return GetResultTS(sResult);
	}
	
	public static void main(String[] args) {
	    WebSegment ws = new WebSegment();
	    ws.SegmentChar("三星手机");
	    
    }
}
