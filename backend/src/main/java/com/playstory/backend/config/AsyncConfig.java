package com.playstory.backend.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 엑셀 생성 전용 스레드풀. 컨테이너 CPU 0.5 제한을 고려해 작게 유지하고,
     * 큐가 가득 차면 호출 스레드에서 실행(CallerRunsPolicy)해 과부하를 막는다.
     *
     * @return 엑셀 생성 작업 executor
     */
    @Bean("excelExecutor")
    public Executor excelExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("excel-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        return executor;
    }
}
