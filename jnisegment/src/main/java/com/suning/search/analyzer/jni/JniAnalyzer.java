package com.suning.search.analyzer.jni;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents;
import org.apache.lucene.analysis.TokenStream;

/**
 * 分词工具: 通过JNI调用C++分词模块
 * 
 * @author 张鉴石
 */

public class JniAnalyzer extends Analyzer
{
	/**
	 * 最多切分 默认模式
	 */
	public static final String MOST_WORDS_MODE = "most-words";
	/**
	 * 按最大切分
	 */
	public static final String MAX_WORD_LENGTH_MODE = "max-word-length";
	/**
	 * 检索分词（不进行繁简体转换）
	 */
	public static final String MAX_WORD_TRADITIONAL = "max-word-traditional";
	/**
	 * 繁体转简体（不分词）
	 */
	public static final String TRADITIONAL_TO_SIMPLE = "traditional-to-simple";
	/**
	 * 简体转繁体（不分词）
	 */
	public static final String SIMPLE_TO_TRADITIONAL = "simple-to-traditional";
	/**
	 * 最多切分 默认模式.web版本
	 */
	public static final String MOST_WORDS_MODE_WEB = "most-words-web";
	/**
	 * 按最大切分.web版本
	 */
	public static final String MAX_WORD_LENGTH_MODE_WEB = "max-word-length-web";
	/**
	 * 检索分词（不进行繁简体转换）.web版本
	 */
	public static final String MAX_WORD_TRADITIONAL_WEB = "max-word-traditional-web";
	/**
	 * 繁体转简体（不分词）.web版本
	 */
	public static final String TRADITIONAL_TO_SIMPLE_WEB = "traditional-to-simple-web";
	/**
	 * 简体转繁体（不分词）.web版本
	 */
	public static final String SIMPLE_TO_TRADITIONAL_WEB = "simple-to-traditional-web";
	
	private int m_iMode = 0;
	
	public void setMode(String mode) {
		if (mode == null || MOST_WORDS_MODE.equalsIgnoreCase(mode)
				|| "default".equalsIgnoreCase(mode)) {
			m_iMode = 1;
		} else if (MAX_WORD_LENGTH_MODE.equalsIgnoreCase(mode)) {
			m_iMode = 0;
		} else if (MAX_WORD_TRADITIONAL.equalsIgnoreCase(mode)) {
			m_iMode = 2;
		} else if (TRADITIONAL_TO_SIMPLE.equalsIgnoreCase(mode)) {
			m_iMode = 3;
		} else if (SIMPLE_TO_TRADITIONAL.equalsIgnoreCase(mode)) {
			m_iMode = 4;
		} else if (MOST_WORDS_MODE_WEB.equalsIgnoreCase(mode)) {
			m_iMode = 5;
		} else if (MAX_WORD_LENGTH_MODE_WEB.equalsIgnoreCase(mode)) {
			m_iMode = 6;
		} else if (MAX_WORD_TRADITIONAL_WEB.equalsIgnoreCase(mode)) {
			m_iMode = 7;
		} else if (TRADITIONAL_TO_SIMPLE_WEB.equalsIgnoreCase(mode)) {
			m_iMode = 8;
		} else if (SIMPLE_TO_TRADITIONAL_WEB.equalsIgnoreCase(mode)) {
			m_iMode = 9;
		} else {
			throw new IllegalArgumentException("不合法的分析器Mode参数设置:" + mode);
		}
	}
	
	/* 3.6.2 *
	@Override
	public TokenStream tokenStream(String fieldName, Reader reader)
	{
		return new JniTokenizer(reader, m_iMode);
	}
	* 3.6.2 */
	
	/* 4.7.2 */
	protected TokenStreamComponents createComponents(String fieldName, Reader reader)
	{
		return new TokenStreamComponents(new JniTokenizer(reader, m_iMode));
	}
}
