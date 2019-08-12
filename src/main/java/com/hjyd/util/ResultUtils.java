package com.hjyd.util;

import com.hjyd.enums.HttpResponseStatus;
import com.hjyd.response.HjHttpResponse;

/**
 * @program：sfidworker
 * @description：返回值处理工具类
 * @author：黄细初
 * @create：2019-04-29 15:55
 */
public class ResultUtils {

    public static HjHttpResponse success(Object object,String message){
        HjHttpResponse result = new HjHttpResponse();
        result.setStatus(HttpResponseStatus.SUCCESS.getCode());
        result.setMessage(message);
        result.setRecord(object);
        return result;
    }

    public static HjHttpResponse success(){
        return success(null,null);
    }


    public static HjHttpResponse error(String status,String message){
        HjHttpResponse result = new HjHttpResponse();
        result.setStatus(status);
        result.setMessage(message);
        return result;
    }

    public static <T> HjHttpResponse<T> ok(T object,String message){
        HjHttpResponse<T> result = new HjHttpResponse();
        result.setStatus(HttpResponseStatus.SUCCESS.getCode());
        result.setMessage(message);
        if(object != null){
            result.setRecord(object);
        }
        return result;
    }

    public static <T> HjHttpResponse<T> ok(){
        return ok(null,null);
    }

    public static <T> HjHttpResponse<T> failed(String status,String message){
        HjHttpResponse<T> result = new HjHttpResponse();
        result.setStatus(status);
        result.setMessage(message);
        return result;
    }

}
