package com.hjyd.enums;

import com.hjyd.util.StringUtils;

/**
 * @program：sfidworker
 * @description：返回的状态值
 * @author：黄细初
 * @create：2019-04-29 15:44
 */
public enum  HttpResponseStatus {

    //404错误
    STATUS_404("404","404错误"),
    //业务执行异常
    STATUS_500("500","业务执行异常"),
    //业务执行异常
    FAIL("600","执行失败"),
    //执行成功
    SUCCESS("200","执行成功")
    ;

    private String code;

    private String message;

    HttpResponseStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @Description：根据code获取对应的枚举
     * @Author：黄细初
     * @Date：2019/4/29
     */
    public static HttpResponseStatus getStatusByCode(String code){
        if(StringUtils.isEmpty(code)){
            return null;
        }
        for (HttpResponseStatus status : HttpResponseStatus.values()) {
            if(status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
