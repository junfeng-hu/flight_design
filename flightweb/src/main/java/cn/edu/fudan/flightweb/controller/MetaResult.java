package cn.edu.fudan.flightweb.controller;

/**
 * Created by junfeng on 8/7/15.
 */

/**
 * utility class for results of write rest api
 */
public class MetaResult {
    private int error = 0;
    private Status status = Status.SUCCESS;
    private String message = "";

    /**
     * for some reason
     */
    public MetaResult() {}

    /* generated */

    public MetaResult(int error, Status status, String message) {
        this.error = error;
        this.status = status;
        this.message = message;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.error = status.ordinal();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MetaResult{" +
                "error=" + error +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }

    /**
     * enum class for error status
     */
    public enum Status {
        SUCCESS,
        PENDING,
        ERROR,
        FAILED
    }



}
