package com.suning.search.analyzer.jni;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FileUtils;


public class SegmentManager {
    /**
     * 操作模式：
     * 0: 从jni本地动态库分词
     * 1: 从web服务取得分词结果
     */
    private static int m_iMode = 0;
    
    /**
	 * 判断当前操作系统是windows还是linux
	 */
	static
	{
		String os = System.getProperties().getProperty("os.name");
		if(os!=null){
		    SetIsJni(!(os.startsWith("win") || os.startsWith("Win")));
		}
		
	}

	public static boolean GetIsJni()
	{
		return m_iMode == 0;
	}
	
	public static void SetIsJni(boolean bIsJni)
	{
		if (bIsJni)
		{
			m_iMode = 0;
		}
		else
		{
			m_iMode = 1;
		}
	}
	
	public String[] SegmentMax(String sWord)
	{
		if (GetIsJni())
		{
			return JniSegment.SegmentMax(sWord);
		}
		else
		{
			return new WebSegment().SegmentMax(sWord);
		}
	}
	
	public String[] SegmentOmni(String sWord)
	{
		if (GetIsJni())
		{
			return JniSegment.SegmentOmni(sWord);
		}
		else
		{
			return new WebSegment().SegmentOmni(sWord);
		}
	}
	
	public String[] SegmentProb(String sWord)
	{
		if (GetIsJni())
		{
			return JniSegment.SegmentProb(sWord);
		}
		else
		{
			return new WebSegment().SegmentProb(sWord);
		}
	}
	
	public String[] SegmentProbOrig(String sWord)
	{
		if (GetIsJni())
		{
			return JniSegment.SegmentProbOrig(sWord);
		}
		else
		{
			return new WebSegment().SegmentProbOrig(sWord);
		}
	}
	
	public String SimpleToTraditional(String word)
	{
		if (GetIsJni())
		{
			return JniSegment.SimpleToTraditional(word);
		}
		else
		{
			return new WebSegment().SimpleToTraditional(word);
		}
	}
	
	public String TraditionalToSimple(String word)
	{
		if (GetIsJni())
		{
			return JniSegment.TraditionalToSimple(word);
		}
		else
		{
			return new WebSegment().TraditionalToSimple(word);
		}
	}

	public boolean Init()
	{
		if (GetIsJni())
		{
			new JniSegment();
			return true;
		}
		else
		{
			return true;
		}
	}
	
	public boolean Update()
	{
		if (GetIsJni())
		{
			return JniSegment.UpdateDic();
		}
		else
		{
			return true;
		}
	}
	
	public boolean BuildDic()
	{
		if (GetIsJni())
		{
			return JniSegment.BuildDic();
		}
		else
		{
			return true;
		}
	}
	
	public int Check()
	{
		if (GetIsJni())
		{
			return JniSegment.Check();
		}
		else
		{
			return 0;
		}
	}
	
	public static void display(String[] strs){
	    if(strs!=null){
	        for(String s:strs){
	            System.out.print(s +" |");
	        }
	        System.out.println();
	    }
	}

	/**
	 * 第一个参数决定运行模式：
	 * 		1（或无参数）：速度测试，无输出文件
	 * 		2：分词测试，输出到segment-omni.txt和segment-prob.txt（/opt/search文件夹）
	 * 		3：无限循环压测
	 * 		?（或help）：输出帮助信息
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		CommandLineParser parser = new BasicParser( );
		Options options = new Options( );
		options.addOption("h", "help", false, "Print this usage information.");
		options.addOption("m", "mode", true, "set segment mode.");
		options.addOption("i", "input", true, "set segment intput file.");
		options.addOption("o", "output", true, "set segment output file.");
		// Parse the program arguments
		CommandLine command_line = null;
		try {
			command_line = parser.parse( options, args );
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		if (command_line.hasOption('h')) {
			System.out.println( "Help Message");
			System.exit(0);
		}
		
		SegmentManager m = new SegmentManager();
		String seg_mode = "prob";
		if (command_line.hasOption('m'))
		{
			seg_mode = command_line.getOptionValue('m');
		}
		if (seg_mode.equalsIgnoreCase("update") || seg_mode.equalsIgnoreCase("up"))
		{
			System.out.println( "mode: update");
			m.Update();
			System.exit(0);
		}
		else if (seg_mode.equalsIgnoreCase("build") || seg_mode.equalsIgnoreCase("bd"))
		{
			System.out.println( "mode: build dict");
			m.BuildDic();
			System.exit(0);
		}
		else
		{
			System.out.println( "mode: segment");
			String input_file = "/opt/search/jnisegment/words.txt";
			if (command_line.hasOption('i'))
			{
				input_file = command_line.getOptionValue('i');
			}
			String output_file = "";
			if (command_line.hasOption('o'))
			{
				output_file = command_line.getOptionValue('o');
			}

			if (!input_file.isEmpty())
			{
				List<String> lines = FileUtils.readLines(new File(input_file), "utf-8");
		        long start = System.currentTimeMillis();
		        if(lines!=null && lines.size()>0){
		            for(String s:lines){
		                String [] omni = m.SegmentOmni(s);
		                String [] prob = m.SegmentProb(s);
		                String [] orig = m.SegmentProbOrig(s);
		                
		                display(omni);
		                display(prob);
		                display(orig);
		            }
			    } 
			    System.out.println("seg over!\n\r wrods count:"+lines.size()+" | time used:"+(System.currentTimeMillis()-start));
			}
		}
    }
}
