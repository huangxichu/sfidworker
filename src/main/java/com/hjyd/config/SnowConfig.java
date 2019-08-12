package com.hjyd.config;

import com.hjyd.core.SnowflakeIDWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program：sfidworker
 * @description：
 * @author：黄细初
 * @create：2019-07-03 09:47
 */
@Configuration
public class SnowConfig {


    @Value("${hjyd.workerId}")
    private Long workerId;

    @Value("${hjyd.dataCenterId}")
    private Long dataCenterId;

    @Value("${hjyd.workerIdBits}")
    private Integer workerIdBits;

    @Value("${hjyd.dataCenterIdBits}")
    private Integer dataCenterIdBits;

    @Bean("snowflakeIDWorker")
    public SnowflakeIDWorker getSnowflakeIDWorker() {
        SnowflakeIDWorker.Factory factory = new SnowflakeIDWorker.Factory(workerIdBits,dataCenterIdBits);
        return factory.create(workerId,dataCenterId);
    }


}
