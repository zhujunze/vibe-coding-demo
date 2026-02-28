package com.easyaccounting.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 混沌工程切面
 *
 * <p>用于模拟系统故障，验证系统的容错能力和异常处理机制。</p>
 */
@Slf4j
@Aspect
@Component
public class ChaosAspect {

    @Value("${chaos.enabled:false}")
    private boolean enabled;

    @Value("${chaos.rate:0.1}")
    private double rate;

    private final Random random = new Random();

    @Before("execution(* com.easyaccounting.service.impl.*.*(..))")
    public void maybeFail() {
        if (enabled) {
            if (random.nextDouble() < rate) {
                log.warn("Chaos Monkey: 触发模拟故障!");
                throw new RuntimeException("Chaos Monkey: Simulated Failure");
            }
        }
    }
}
