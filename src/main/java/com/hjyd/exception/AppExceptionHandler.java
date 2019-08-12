package com.hjyd.exception;

import com.hjyd.enums.HttpResponseStatus;
import com.hjyd.response.HjHttpResponse;
import com.hjyd.util.ExceptionUtils;
import com.hjyd.util.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @program：sfidworker
 * @description：统一异常处理类
 * @author：黄细初
 * @create：2019-04-29 15:37
 */
@Slf4j
@RestControllerAdvice
public class AppExceptionHandler {


    @ExceptionHandler(value = Exception.class)
    public HjHttpResponse defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        if (e instanceof ApplicationException) {
            String stack = ((ApplicationException) e).getStack();
            log.info("\n========> 发生业务异常：\n{}", stack);
            String message = e.getMessage();
            HjHttpResponse response = ResultUtils.failed(HttpResponseStatus.STATUS_500.getCode(), message);
//            response.setStack(stack);
            return response;
        } else if (e instanceof NullPointerException) {
            String stack = ((ApplicationException) e).getStack();
            log.info("\n========> 发生业务异常：\n{}", stack);
            HjHttpResponse response = ResultUtils.failed(HttpResponseStatus.STATUS_500.getCode(), "空指针");
//            response.setStack(stack);
            return response;
        } else {
            String stack = ExceptionUtils.getStackTrace(e);
            log.info("\n========> 发生业务异常：\n{}", stack);
            HjHttpResponse response = ResultUtils.failed(HttpResponseStatus.STATUS_500.getCode(), HttpResponseStatus.STATUS_500.getMessage());
//            response.setStack(stack);
            return response;
        }
    }


}
