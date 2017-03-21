package com.huoli.bmall.test.model;

import org.xvolks.jnative.JNative;
import org.xvolks.jnative.exceptions.NativeException;
//import org.xvolks.jnative.pointers.Pointer;
//import org.xvolks.jnative.pointers.memory.MemoryBlockFactory;
import org.xvolks.jnative.Type;
/***
 * @category 第一种调用dll 方法
 * @author Administrator
 *
 */
public class KGDLLTest {
	static final String DLL_FILE = "C:/Program Files (x86)/OpenEye/libeay32.dll";
	public String connectServer() throws NativeException,
			IllegalAccessException {
		JNative jn_handler = null;
		jn_handler = new JNative(DLL_FILE, "BN_GENCB_call");
		jn_handler.setRetVal(Type.STRING); // 指定返回参数的类型
		jn_handler.invoke(); // 调用方法
		return jn_handler.getRetVal();
	}

	public String sendStructToJson(int alldetail) throws NativeException,
			IllegalAccessException {
		JNative jn_handler = null;
		jn_handler = new JNative(DLL_FILE, "senddata");
		jn_handler.setRetVal(Type.STRING); // 指定返回参数的类型
		jn_handler.setParameter(0, "817316229775");
		System.out.println("-----");
		jn_handler.invoke(); // 调用方法
		return jn_handler.getRetVal();
	}

	public String disconnectServer() throws NativeException,
			IllegalAccessException {
		JNative jn_handler = null;
		jn_handler = new JNative(DLL_FILE, "disconnectserver");
		jn_handler.setRetVal(Type.STRING); // 指定返回参数的类型
		jn_handler.invoke(); // 调用方法
		return jn_handler.getRetVal();
	}

	public static void main(String[] args) throws NativeException, IllegalAccessException{
		//System.out.println(System.getProperty("user.dir"));
		KGDLLTest kt = new KGDLLTest();
		String result = kt.connectServer();
		System.out.println("连接服务器：" + result);
		boolean is_connected_server = result.indexOf("100") == 0 ? true: false;
		if(! is_connected_server) {
			return;
		}
		result = kt.sendStructToJson(0);//是否打印数量、单位及单价['0':不打印;'1':打印]
		System.out.println("服务器返回消息："+result);
		result = kt.sendStructToJson(1);//是否打印数量、单位及单价['0':不打印;'1':打印]
		System.out.println("服务器返回消息："+result);
		result = kt.disconnectServer();
		System.out.println("断开服务器：" + result);
		
	}
}
