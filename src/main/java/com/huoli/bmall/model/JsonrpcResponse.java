package com.huoli.bmall.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.huoli.bmall.exception.MyException;
import com.huoli.bmall.model.jsonresp.ErrorJson;
import com.huoli.bmall.points.model.UserInfo;

public class JsonrpcResponse
{
	private String name;
	private Object value;

	public Object getResult()
	{
		return value;
	}

	public boolean isError()
	{
		return name.equals("error");
	}

	public void setResult(Object result)
	{
		this.name = "result";
		this.value = result;
	}

	public void setError(String detail,int code)
	{
		ErrorJson result = new ErrorJson(code);
		result.setDetail(detail);
		this.name = "error";
		this.value = result;
	}

	public void setError(MyException e)
	{
		ErrorJson result = new ErrorJson(e);
		this.name = "error";
		this.value = result;
	}

	public void setError(String detail,int code, String msg)
	{
		ErrorJson result = new ErrorJson(detail,code, msg);
		this.name = "error";
		this.value = result;
	}
	
	public Map<String, Object> GenMapResp(String token)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jsonrpc", "2.0");
		map.put("id", "0");
		map.put(name, value);
		if(StringUtils.isNotBlank(token)){
			map.put("token", token);
		}
		return map;
	}
	
	public Map<String, Object> GenMapResp(String token,UserInfo user)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jsonrpc", "2.0");
		map.put("id", "0");
		map.put(name, value);
		if(StringUtils.isNotBlank(token)){
			map.put("token", token);
		}
//		if(this.name.equals("error")&&StringUtils.isNotBlank(user.getPhone())&&orderService.isStaff(user.getPhone())){
//			ErrorJson result=(ErrorJson)this.value;
//			String message=result.getMessage();
//			String detail=result.getDetail();
//			result.setMessage(detail);
//			result.setDetail(message);
//			this.value=result;
//		}
		return map;
	}
}
