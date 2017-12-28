package com.suning.search.analyzer.jni;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropertiesUtil {
    
    private static final Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

    public static final String CLASSPATH_LOAD_WAY="classpath";
    public static final String FILE_LOAD_WAY="file";
    
    /**
     * 
     * @param path
     * @return
     * example:
     * PropertiesUtil.loadProperties("classpath:solrhome.properties")<br>
     * 加载classpath下的solrhome.properties<br>
     * OR<br>
     * PropertiesUtil.loadProperties("file:/opt/search/jnisegment/websegment.properties")<br>
     * 加载文件系统绝对路径/opt/search/jnisegment/websegment.properties<br>
     * @throws FileNotFoundException 
     */
    public static Properties loadProperties(String path){
        Properties p = new Properties();
        InputStream inputStream = getFileStream(path);
        if(inputStream == null){
            return p;
        }
        try {
            p.load(inputStream);
        } catch (IOException e1) {
            log.error("加载配置文件异常",e1);
            return null;
        } finally {
            try {
                IOUtils.closeQuietly(inputStream);
            } catch (Exception ex) {
                log.error("加载配置文件异常",ex);
            }
        }
        return p;
    }
    
    private static InputStream getFileStream(String partten) {
        InputStream result = null;
        if(partten.indexOf(":")==-1){
            PropertiesUtil.class.getClassLoader()
            .getResourceAsStream(partten);
        }
        if(CLASSPATH_LOAD_WAY.equals(partten.split(":")[0])){//classpath:
            result = PropertiesUtil.class.getClassLoader()
            .getResourceAsStream(partten.split(":")[1]);
        }else if(FILE_LOAD_WAY.equals(partten.split(":")[0])){//file:
            try{
                result = new FileInputStream(new File(partten.split(":")[1]));
            }catch(Exception e){
                log.info("",e);
            }
        }
        return result;
    }
    
    
    
    public synchronized static void store(Properties p ,String path){
        if(StringUtils.isNotEmpty(path)){
            File f = new File(path);
            OutputStream os = null;
            try{
                os=new FileOutputStream(f);
                p.store(os, "");
            }catch(Exception e){
                log.error("保存配置文件发生异常！",e);
            }finally{
                IOUtils.closeQuietly(os);
            }
        }
    }
    
    /**
     * 
     * 功能描述: <br>
     * 从配置文件读取配置，如果配置文件不存在，或配置项不存在，则返回默认值<br>
     *
     * @param p 配置文件<br>
     * @param key <br>
     * @param defaultValue 默认值<br>
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public  static String getProperty(Properties p ,String key,String defaultValue){
        if(p==null){
            return defaultValue;
        }else{
            return p.getProperty(key, defaultValue);
        }
    }
    
    public static String getProperty(Properties p ,String key){
        return getProperty(p,key,null);
    }
    
    
    public static void main(String[] args) {
        Properties p = new Properties();
        p.setProperty("mod_a", "true");
        store(p,"/opt/search/test.properties");
        Properties p1 = loadProperties("file:/opt/search/test.properties");
        
        System.out.println(p1.getProperty("mod_a"));
    }
}

