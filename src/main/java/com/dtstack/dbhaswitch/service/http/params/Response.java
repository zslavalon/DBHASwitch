package com.dtstack.dbhaswitch.service.http.params;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class Response {
    /**
     * Response的一个json拼接工具
     */
    private String message = "";
    private Integer code = 1;
    private Object data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isTrue() {
        if (code == null || code != 0) {
            return false;
        }
        return true;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String dataString() {
        return (String) data;
    }

    public String dataMapString(String key) {
        if (data == null || !(data instanceof Map)) {
            return "";
        }

        Map d = (Map) data;
        if (!d.containsKey(key)) {
            return "";
        }

        return (String) d.get(key);
    }

    public JSONObject dataJSONObject() {
        return JSON.parseObject((JSON.toJSONString(data)));
    }


    @Override
    public String toString() {
        return "Response [message=" + message + ", code=" + code + ", data=" + data + "]";
    }

}
