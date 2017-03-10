package com.huoli.bmall.exception;

import com.huoli.bmall.model.jsonresp.ErrorJson;

public class MyException extends Exception
{
	private static final long serialVersionUID = 1L;
	private int code;
	private String detail;

	
	public MyException(String detail,int code)
	{
		super(ErrorJson.getMessage(code));
		this.code = code;
		this.detail=detail;
	}

	public MyException(String detail,int code, String msg)
	{
		super(msg);
		this.code = code;
		this.detail=detail;
	}

	public int getCode()
	{
		return code;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
	
}
