package com.huoli.bmall.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
@author :zhouwenbin
@time   :2016-9-5
@comment:
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NormalResp {

	private int status;
	private String msg;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
