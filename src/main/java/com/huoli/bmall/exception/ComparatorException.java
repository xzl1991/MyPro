package com.huoli.bmall.exception;
/**
@author :zhouwenbin
@time   :2016-3-23
@comment:
 **/
public class ComparatorException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public ComparatorException(String msg) {

        super(msg);
    }

    public ComparatorException(String msg, Throwable e) {

        super(msg, e);
    }

}