package com.myblog.api.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class MyblogException extends RuntimeException{

    public final Map<String, String> validation = new HashMap<>();

    public MyblogException(String message) {
        super(message);
    }

    public MyblogException(String message, Throwable cause) {
        super(message, cause);
    }

    // 반드시 구현해야 하는 메소드
    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }
}
