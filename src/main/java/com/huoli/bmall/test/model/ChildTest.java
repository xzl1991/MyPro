package com.huoli.bmall.test.model;

public class ChildTest extends Father{
	int age = 20;
	public static void main(String[] args) {
		Father father = new Father();
//		new ChildTest("呵呵呵");
//		ChildTest ch = (ChildTest) father;
		ChildTest child = new ChildTest();
		System.out.println("年龄:"+child.age);
		Father f = child;
		System.out.println("年龄:"+f.age);
	}
	public ChildTest(){
		System.out.println("	子类无参构造器---");
	}
	public ChildTest(String name){
		System.out.println("	子类无参构造器***---"+name);
	}
	static{
		System.out.println("   子类静态方法---");
	}
	{
		System.out.println("   子类非静态输出---");
	}
}
class Father{
	int age = 40;
	public Father(){
		System.out.println("父类无参构造器---");
	}
	public Father(String name){
		System.out.println("父类构造器****---"+name);
	}
	static{
		System.out.println("父类静态方法---");
	}
	{
		System.out.println("父类非静态输出---");
	}
}