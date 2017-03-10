package com.huoli.bmall.model;

import java.util.HashMap;

public class JsonrpcRequest
{
	private String jsonrpc;
	private String method;
	private String id;
	private HashMap<String, String> params;

	public String getJsonrpc()
	{
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc)
	{
		this.jsonrpc = jsonrpc;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public HashMap<String, String> getParams()
	{
		return params;
	}

	public void setParams(HashMap<String, String> params)
	{
		this.params = params;
	}

}
