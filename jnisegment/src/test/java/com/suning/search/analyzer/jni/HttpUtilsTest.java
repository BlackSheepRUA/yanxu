package com.suning.search.analyzer.jni;

import java.io.IOException;

import org.junit.*;

public class HttpUtilsTest {
	@Test
	public void test(){
		try {
			HttpUtils.webget("www.baidu.com", 5);
		} catch (IOException e) {
			System.err.println("Error:" + e.getMessage());
		}
	}
	
	@Test
	public void test1(){
		try {
			HttpUtils.asyncWebgetCheck("www.baidu.com", 5, "123");
		} catch (IOException e) {
			System.err.println("Error:" + e.getMessage());
		}
	}
}
