package com.hjyd.response;

import lombok.Data;

/**
 * @program：sfidworker
 * @description：统一的业务返回信息
 * @author：黄细初
 * @create：2019-04-28 18:01
 */
@Data
public class HjHttpResponse<T> {

    private String status;

    private String message;

    private String stack;

    private T record;

}
