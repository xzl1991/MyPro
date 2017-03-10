package com.huoli.bmall.exception;

public class CommonException extends RuntimeException {

	 private static final long serialVersionUID = 1L;

	    public CommonException(String msg) {

	        super(msg);
	    }

	    public CommonException(String msg, Throwable e) {

	        super(msg, e);
	    }
}
