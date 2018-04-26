package entity;

import java.io.Serializable;

/**
 * @author 小郑
 * @version 1.0
 * @description com.pinyougou.entity
 * @date 2018/4/23/0023
 */
public class Result implements Serializable {
    private boolean success;
    private String msg;

    public Result(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
