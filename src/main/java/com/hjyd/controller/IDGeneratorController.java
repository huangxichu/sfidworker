package com.hjyd.controller;

import com.hjyd.core.SnowflakeIDWorker;
import com.hjyd.response.HjHttpResponse;
import com.hjyd.util.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program：sfidworker
 * @description：获取ID接口
 * @author：黄细初
 * @create：2019-07-03 10:33
 */
@RestController
@RequestMapping("/idGenerator")
@Api(value = "IDGenerator", tags = "ID相关接口")
public class IDGeneratorController {

    @Autowired
    private SnowflakeIDWorker snowflakeIDWorker;

    @ApiOperation(value = "获取ID接口", notes = "获取ID接口")
    @GetMapping("/get")
    public HjHttpResponse<Long> getOne() {
        return ResultUtils.ok(snowflakeIDWorker.nextId(), "查询成功");
    }
}
