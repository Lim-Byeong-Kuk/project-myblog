package com.myblog.api.exception;

public class ExpiredJwt extends MyblogException {

    public ExpiredJwt(String message) {
        super(message);
    }

    public ExpiredJwt(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
