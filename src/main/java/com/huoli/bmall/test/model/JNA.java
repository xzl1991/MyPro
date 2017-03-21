package com.huoli.bmall.test.model;


import com.sun.jna.Library;  
import com.sun.jna.Native;  
import com.sun.jna.Platform;  
/**
 * 
 * @category 第二种调用dll 方法
 * @author Administrator
 *
 */
public class JNA {  

  // 定义接口CLibrary，继承自com.sun.jna.Library  
  public interface testdll extends Library {  
      // msvcrt为dll名称,msvcrt目录的位置为:C:\Windows\System32下面,  
//      testdll Instance = (testdll) Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"),  
      testdll Instance = (testdll) Native.loadLibrary((Platform.isWindows() ? "E:\\WorkSoft\\jdk\\jdk1.7.0_03\\bin/test/SkJQJK.dll" : "c"),  
              testdll.class);  
      // printf为msvcrt.dll中的一个方法.  
      void printf(String format, Object... args);
  }  
  public native static void connectserver(String string);  

  public static void main(String[] args) {  
      // 调用printf打印信息  
      testdll.Instance.toString();//printf("yyyyMMdd");  
      System.out.println("---"+testdll.Instance.toString());
      connectserver("017316229775");
      System.out.println("8****");
//	  ((JNA) testdll.Instance).dail("817316229775");
  }  

}  
