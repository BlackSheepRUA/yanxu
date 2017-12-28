package com.suning.search.analyzer.jni;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;

/**
 * 分词工具: 通过JNI调用C++分词模块
 * 
 * @author 张鉴石
 */

public class JniSegment {
	private static final Logger log = LoggerFactory.getLogger(JniSegment.class);
	
	private static final String m_const_version = "1.7.4";
	private static String m_home_path = "/opt/search/jnisegment";
	private static String m_so_name = m_home_path + "/lib/libjnisegment.so." + m_const_version;
	
    static {
        if (SegmentManager.GetIsJni()) {
        	String version = m_const_version;
        	try
        	{
        		Properties pu = PropertiesUtil.loadProperties("classpath:jni.properties");
        		m_home_path = pu.getProperty("jni_path", "/opt/search/jnisegment/");
        		version = pu.getProperty("jni_version", m_const_version);
        	} catch (Exception e) {}
        	if (m_home_path.isEmpty())
        	{
        		m_home_path = "/opt/search/jnisegment";
        	}
        	if (!version.isEmpty())
        	{
        		m_so_name = m_home_path + "/lib/libjnisegment.so." + version;
        	}
        	else
        	{
        		m_so_name = m_home_path + "/lib/libjnisegment.so";
        	}
        	System.load(m_so_name);
        	Init(m_home_path);
        }
    }

    private static native boolean Init(String home_path);

    private static native boolean Update(String home_path);
    
    private static native boolean Build(String home_path);

    public static native String[] SegmentMax(String word);

    public static native String[] SegmentOmni(String word);

    public static native String[] SegmentProb(String word);

    public static native String[] SegmentProbOrig(String word);
    
    public static native String SimpleToTraditional(String word);
    
    public static native String TraditionalToSimple(String word);

	public static native int Check();
    
    public static boolean InitDic()
    {
    	return Init(m_home_path);
    }

    public static boolean UpdateDic()
    {
    	return Update(m_home_path);
    }

    public static boolean BuildDic()
    {
    	boolean build_success = Build(m_home_path);
    	if (build_success)
    	{
    		try
    		{
    			ZipHelp.zipMultiFile(m_home_path + "/idx", m_home_path + "/idx.zip", false);
    			log.info("make zip: " + m_home_path + "/idx.zip");
	        }
    		catch (Exception e)
    		{
    			log.error("打包词典出现异常.",e);
	        }
    	}
    	else
    	{
    		log.info("no new dict exist.");
    	}
    	
    	build_success = Build("hk::" + m_home_path);
    	if (build_success)
    	{
    		try
    		{
    			ZipHelp.zipMultiFile(m_home_path + "/idx", m_home_path + "/idx.hk.zip", false);
    			log.info("make zip: " + m_home_path + "/idx.hk.zip");
	        }
    		catch (Exception e)
    		{
    			log.error("打包hk词典出现异常.",e);
	        }
    	}
    	else
    	{
    		log.info("no new hk dict exist.");
    	}
    	
        return build_success;
    }
}
