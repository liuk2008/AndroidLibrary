package com.example.android.mvp.model;

public class ApiResponse<T> {

    private int resultCode;    // 返回码，200为成功
    private String message;      // 返回信息
    private T data;

    public ApiResponse(int resultCode, String message) {
        this.message = message;
        this.resultCode = resultCode;
    }

    public boolean isSuccess() {
        return 200 == resultCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ApiResponse{");
        sb.append("data=").append(data);
        sb.append(", resultCode=").append(resultCode);
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiResponse)) return false;

        ApiResponse<?> that = (ApiResponse<?>) o;

        if (getResultCode() != that.getResultCode()) return false;
        return getMessage() != null ? getMessage().equals(that.getMessage()) : that.getMessage() == null;
    }

    @Override
    public int hashCode() {
        int result = getResultCode();
        result = 31 * result + (getMessage() != null ? getMessage().hashCode() : 0);
        return result;
    }
}
