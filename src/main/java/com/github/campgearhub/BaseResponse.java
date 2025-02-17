package com.github.campgearhub;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ResponseStatus;

import static jakarta.security.auth.message.AuthStatus.SUCCESS;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseResponse<T> {
    private String message;
    private T data;
    private int statusCode;

    // 성공 응답을 위한 생성자
    public static <T> BaseResponse<T> onSuccess(T data) {
        return new BaseResponse<>("Success", data, 200);
    }

    // 실패 응답을 위한 생성자
    public static <T> BaseResponse<T> onFailure(String message) {
        return new BaseResponse<>(message, null, 500);
    }

    public BaseResponse(String message, T data, int statusCode) {
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
