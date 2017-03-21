package com.huoli.bmall.test.model;

/***
 * 
 * @category 第三种方法
 * @author Administrator
 *
 */
public class HelloWorld {
    public static void main(String[] args) {
    	connectserver();
    	System.out.println("---end---");
    }
    public void say(){
    	System.out.println("say - hello - Test....");
    }
    static{
    	System.loadLibrary("test/SkJQJK");
    }
    public native static void connectserver();  
}