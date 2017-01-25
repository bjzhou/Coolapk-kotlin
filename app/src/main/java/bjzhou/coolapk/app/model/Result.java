package bjzhou.coolapk.app.model;

import android.text.TextUtils;

import bjzhou.coolapk.app.exceptions.ClientException;

public class Result<T> {
    private T data;
    private String message;
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Result(int i, String str) {
        status = i;
        message = str;
    }

    public Result(int i, String str, T t) {
        this(i, str);
        data = t;
    }

    public boolean isSuccess() {
        return status == null || status == 1;
    }

    public int getStatusCode() {
        return status != null ? status : 1;
    }

    public ClientException checkResult() {
        if (isSuccess()) {
            return null;
        }
        if (TextUtils.isEmpty(message)) {
            message = "Empty error message";
        }
        return new ClientException(status, message);
    }
}
