package com.suning.search.analyzer.jni;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分词工具: 通过JNI调用C++分词模块
 * 
 * @author 张鉴石
 */

public class JniTokenizer extends Tokenizer
{
    private static final Logger logger = LoggerFactory.getLogger(JniTokenizer.class);
	private String[] m_vSegment;
	private String[] m_vSegmentWord;
	private int[] m_vSegmentStart;
	private int[] m_vSegmentEnd;
	private String[] m_vSegmentType;
	private int m_iIndex = 0;
	private CharTermAttribute m_CharAtt;
	private OffsetAttribute m_OffsetAtt;
	private TypeAttribute m_TypeAtt;
	private int m_iMode = 0;

	public JniTokenizer(Reader input)
	{
	   
		/* 4.7.2 */
		super(input);
		 //System.out..println("!!!!!!!!!!!!!construct with out mode!!!!!!!!!!");
	    m_CharAtt = addAttribute(CharTermAttribute.class);
	    m_OffsetAtt = addAttribute(OffsetAttribute.class);
	    m_TypeAtt = addAttribute(TypeAttribute.class);
		m_iMode = 0;
		m_vSegment = new String[0];
	}
	
	public JniTokenizer(Reader input, int mode)
	{
	    
		/* 4.7.2 */
		super(input);
		//System.out..println("!!!!!!!!!!!!!construct with mode!!!!!!!!!!");
		m_CharAtt = addAttribute(CharTermAttribute.class);
		m_OffsetAtt = addAttribute(OffsetAttribute.class);
		m_TypeAtt = addAttribute(TypeAttribute.class);
		m_iMode = mode;
		m_vSegment = new String[0];
	}
	
	private String GetReaderString()
	{
		char[] sTmp = new char[64];
		String sResult = "";
		int len = -1;
		try
		{
			while ((len = this.input.read(sTmp)) != -1)
			{
				String strTmp = new String(sTmp, 0, len);
				sResult += strTmp;
			}
		} catch (IOException e) {  
		    logger.error("jni分词异常"+e.getMessage());
	    }
		return sResult;
	}
	
	private void Segment()
	{
		String strRead = GetReaderString();
        if (strRead != "") {  
        	//System.out.println("分词原始字符串:"+strRead);
        	//logger.info("分词原始字符串:"+strRead);
        	switch (m_iMode)
        	{
        	case 0 :
        		m_vSegment = new SegmentManager().SegmentProb(strRead);
        		break;
        	case 1 :
        		m_vSegment = new SegmentManager().SegmentOmni(strRead);
        		break;
        	case 2 :
        		m_vSegment = new SegmentManager().SegmentProbOrig(strRead);
        		break;
        	case 3 :
        		m_vSegment = new String[1];
        		m_vSegment[0] = new SegmentManager().TraditionalToSimple(strRead);
        		break;
        	case 4 :
        		m_vSegment = new String[1];
        		m_vSegment[0] = new SegmentManager().SimpleToTraditional(strRead);
        		break;
        	case 5 :
        		m_vSegment = new WebSegment().SegmentOmni(strRead);
        		break;
        	case 6 :
        		m_vSegment = new WebSegment().SegmentProb(strRead);
        		break;
        	case 7 :
        		m_vSegment = new WebSegment().SegmentProbOrig(strRead);
        		break;
        	case 8 :
        		m_vSegment = new String[1];
        		m_vSegment[0] = new WebSegment().TraditionalToSimple(strRead);
        		break;
        	case 9 :
        		m_vSegment = new String[1];
        		m_vSegment[0] = new WebSegment().SimpleToTraditional(strRead);
        		break;
        	default:
        		m_vSegment = new SegmentManager().SegmentProb(strRead);
        	}
        }
        else
        {
        	m_vSegment = new String[0];
        }
        m_iIndex = 0;
        //System.out.println("分词结果:"+Arrays.asList(m_vSegment));
        
    	m_vSegmentWord = new String[m_vSegment.length];
    	m_vSegmentStart = new int[m_vSegment.length];
    	m_vSegmentEnd = new int[m_vSegment.length];
    	m_vSegmentType = new String[m_vSegment.length];
    	for (int i = 0; i < m_vSegment.length; i++)
    	{
    		//logger.info("m_vSegment[i]:"+m_vSegment[i]);
    		String sTmp[] = m_vSegment[i].split(":");
    		if (sTmp.length == 1)
    		{
    			//logger.info("(sTmp.length == 1)");
    			m_vSegmentWord[i] = m_vSegment[i];
    			m_vSegmentStart[i] = 0;
    			m_vSegmentEnd[i] = 0;
    			m_vSegmentType[i] = "1";
    		}
    		else if (sTmp.length == 3)
    		{
    			//logger.info("(sTmp.length == 3)");
    			m_vSegmentWord[i] = sTmp[2];
    			m_vSegmentStart[i] = Integer.valueOf(sTmp[0]).intValue();
    			m_vSegmentEnd[i] = Integer.valueOf(sTmp[1]).intValue() + 1;
    			m_vSegmentType[i] = "1";
    		}
    		else if (sTmp.length == 4)
    		{
    			//logger.info("(sTmp.length == 4)");
    			m_vSegmentWord[i] = sTmp[3];
    			m_vSegmentType[i] = sTmp[0];
    			m_vSegmentStart[i] = Integer.valueOf(sTmp[1]).intValue();
    			m_vSegmentEnd[i] = Integer.valueOf(sTmp[2]).intValue() + 1;
    		}
    		else
    		{
    			//logger.info("(sTmp.length == else)");
    			m_vSegmentWord[i] = "";
    			m_vSegmentStart[i] = 0;
    			m_vSegmentEnd[i] = 0;
    			m_vSegmentType[i] = "1";
    		}
    	}
	}
	
	@Override
	public boolean incrementToken() throws IOException
	{
	    //System.out..println("!!!!!!!!!!incrementToken called!!!!!!!!!");
	    clearAttributes();
		//如果m_vSegment为空则去读取input中的字符，并分词，并置m_iIndex = 0
		if (m_vSegment.length == 0)
		{
			this.Segment();
		}
		
		//判断m_iIndex是否合法
		if ((m_iIndex < 0) || (m_iIndex >= m_vSegment.length))
		{
			return false;
		}

		//将m_vSegment中的字符第m_iIndex个读出
		m_CharAtt.setEmpty();
		m_CharAtt.append(m_vSegmentWord[m_iIndex]);
		m_OffsetAtt.setOffset(m_vSegmentStart[m_iIndex], m_vSegmentEnd[m_iIndex]);
		m_TypeAtt.setType(m_vSegmentType[m_iIndex]);
		//System.out.println("分词取得tokenizer结果:"+m_vSegment[m_iIndex]);

		m_iIndex = m_iIndex + 1;
		return true;
	}

	/** Expert: Reset the tokenizer to a new reader.  Typically, an
	 *  analyzer (in its reusableTokenStream method) will use
	 *  this to re-use a previously created tokenizer. */
	//@Override
	/*public void reset(Reader input) throws IOException {
	    super.reset(input);
		super.reset();
		reset();
	}*/
	
	@Override
	public void reset() throws IOException {
	    //System.out..println("!!!!!!!!!! reset called!!!!!!!!");
		super.reset();
		m_vSegment = new String[0];
		m_CharAtt.setEmpty();
		m_OffsetAtt.setOffset(0, 0);
		m_TypeAtt.setType("0");
		m_iIndex = 0;
	}
	
	@Override
	public void close() throws IOException {
	    //System.out..println("!!!!!!!!!! close called!!!!!!!!");
	    super.close();
	}
	
	@Override
	public void end() throws IOException {
	    //System.out..println("!!!!!!!!!! end called!!!!!!!!");
	    super.end();
	}
}
