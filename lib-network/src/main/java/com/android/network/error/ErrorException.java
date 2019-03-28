package com.android.network.error;

import java.io.IOException;

/**
 * 自定义异常
 * 1、可抛出业务异常
 * 2、可抛出网络层异常
 */
public class ErrorException extends IOException {

    private int code;
    private String message;

    public ErrorException(int resultCode, String message) {
        super(message);
        this.code = resultCode;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

