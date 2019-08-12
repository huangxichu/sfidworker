package com.hjyd.exception;

import com.hjyd.util.ExceptionUtils;

/**
 * @program：sfidworker
 * @description：自定义异常类
 * @author：黄细初
 * @create：2019-04-29 15:29
 */
public class ApplicationException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private String code;

    private String stack;

    public ApplicationException(String message) {
        super(message);
        this.stack = ExceptionUtils.getStackTrace(this);
    }

    public ApplicationException(String code, String message) {
        super(message);
        this.code = code;
        this.stack = ExceptionUtils.getStackTrace(this);
    }

    public ApplicationException(String code, Throwable cause, String message) {
        super(message,cause);
        this.code = code;
        this.stack = ExceptionUtils.getStackTrace(cause);
    }

    public ApplicationException(Throwable cause, String message) {
        super(message,cause);
        this.stack = ExceptionUtils.getStackTrace(cause);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }
}
