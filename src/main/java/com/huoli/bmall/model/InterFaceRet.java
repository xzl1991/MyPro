package com.huoli.bmall.model;

import com.alibaba.fastjson.JSONObject;

public class InterFaceRet {
	private String msg;
	private JSONObject param;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public JSONObject getParam() {
		return param;
	}
	public void setParam(JSONObject param) {
		this.param = param;
	}
}
