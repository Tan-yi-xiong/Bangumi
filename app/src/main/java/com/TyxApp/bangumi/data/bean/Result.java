package com.TyxApp.bangumi.data.bean;

public class Result<T> {
    private boolean isNull;
    private T result;

    public Result(boolean isNull, T t) {
        this.isNull = isNull;
        result = t;
    }

    public boolean isNull() {
        return isNull;
    }

    public void setNull(boolean aNull) {
        this.isNull = aNull;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
