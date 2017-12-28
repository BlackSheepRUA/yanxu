package com.suning.search.analyzer.jni;

import java.io.File ;  
import java.io.FileInputStream ;  
import java.io.InputStream ;  
import java.util.zip.ZipEntry ;  
import java.util.zip.ZipOutputStream ;  
import java.io.FileOutputStream ; 

public class ZipHelp {
    public static void main(String args[]) throws Exception{    // 所有异常抛出  
        //File file = new File("/Users/liuxun/Downloads/liuxun") ;    // 定义要压缩的文件夹  
        //File zipFile = new File("/Users/liuxun/Downloads/liuxundir.zip") ;  // 定义压缩文件名称  
        zipMultiFile("/opt/search/jnisegment/idx", "/opt/search/jnisegemnt/idx.zip", true);  
    }

    /* 
    * 压缩整个文件夹中的所有文件，生成指定名称的zip压缩包 
    * @param filepath 文件所在目录 
    * @param zippath 压缩后zip文件名称 
    * @param dirFlag zip文件中第一层是否包含一级目录，true包含；false没有 
    * 2015年6月9日 
    */
    public static void zipMultiFile(String filepath ,String zippath, boolean dirFlag) {  
        try {  
            File file = new File(filepath);// 要被压缩的文件夹  
            File zipFile = new File(zippath);  
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));  
            if (file.isDirectory())
            {  
                File[] files = file.listFiles();  
                for (File fileSec:files)
                {  
                    if (dirFlag)
                    {
                        recursionZip(zipOut, fileSec, file.getName() + File.separator);
                    }
                    else
                    {  
                        recursionZip(zipOut, fileSec, "");
                    }
                }
            }
            zipOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void recursionZip(ZipOutputStream zipOut, File file, String baseDir) throws Exception{
        if (file.isDirectory())
        {
            File[] files = file.listFiles();
            for (File fileSec:files)
            {
                recursionZip(zipOut, fileSec, baseDir + file.getName() + File.separator);
            }
        }
        else
        {
            byte[] buf = new byte[1024];
            InputStream input = new FileInputStream(file);
            zipOut.putNextEntry(new ZipEntry(baseDir + file.getName()));
            int len;
            while ((len = input.read(buf)) != -1)
            {
                zipOut.write(buf, 0, len);
            }
            input.close();
        }
    }
}
