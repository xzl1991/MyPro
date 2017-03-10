package com.huoli.bmall.exception;

import java.util.HashMap;
import java.util.Map;

public class BeforePayException extends Exception {
	private static final long serialVersionUID = 1L;
	private int code;

	public BeforePayException(int code) {
		super(PayErrorMsg.getMessage(code));
		this.code = code;
	}

	public BeforePayException(int code, String msg) {
		super(msg);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	private static class PayErrorMsg {
		public static Map<String, String> errors;

		public static String errorcode[][] = {
				// 系统错误
				{ "-1", "不好意思，您访问的页面走丢了" },
				{ "-2", "不好意思，您的网络不太好" },
				{ "-9", "不好意思,订单超时,请重下订单" },
				
				//通过token获取信息
				{ "-101", "未找到相应订单" },
				{ "-102", "token已失效" },
				{ "-103", "订单与token不匹配" },

				// 优惠券相关错误
				{ "-600", "对不起，此订单不能使用该商品类型优惠券。" },
				{ "-601", "对不起，此订单不能使用该频道类型优惠券。" },
				{ "-602", "对不起，该类型优惠券已过期。" }, { "-603", "对不起，该订单已失效。" },
				{ "-604", "对不起，该订单未到满减金额。" },

		};

		public static String getMessage(int code) {
			if (errors == null) {
				errors = new HashMap<String, String>();
				for (int i = 0; i < errorcode.length; i++) {
					errors.put(errorcode[i][0], errorcode[i][1]);
				}
			}
			return errors.get("" + code);
		}
		
//		private int code;
//		private String message;
//		private String data;

//		public PayErrorMsg(BeforePayException e) {
//			this.code = e.getCode();
//			if (code < -900 && code > -3000)
//				this.message = e.getMessage();
//			else
//				this.message = getMessage(code);
//			this.data = null;
//		}
//
//		public PayErrorMsg(int code) {
//			this.code = code;
//			this.message = getMessage(code);
//			this.data = null;
//		}
//
//		public PayErrorMsg(int code, String message) {
//			this.code = code;
//			this.message = message;
//			this.data = null;
//		}
//
//		public int getCode() {
//			return code;
//		}
//
//		public void setCode(int code) {
//			this.code = code;
//		}
//
//		public String getMessage() {
//			return message;
//		}
//
//		public void setMessage(String message) {
//			this.message = message;
//		}
//
//		public String getData() {
//			return data;
//		}
//
//		public void setData(String data) {
//			this.data = data;
//		}
	}
}