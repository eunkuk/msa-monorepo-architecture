package com.kr.core.web.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 웹 로깅 관점(Aspect)
 * 컨트롤러와 핸들러 계층의 메서드 실행 시작과 종료 시점에 로그를 남깁니다.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class WebLoggingAspect {

    // 아래 추가된 특정 클래스 명과 메소드명은 로그를 남기지 않고 SKIP 합니다.
    private final Set<String> excludeControllerNames = Set.of("healthCheck");
    private final Set<String> excludeHandlerNames = Set.of();

    /**
     * 모든 컨트롤러 시작과 종료 부분에 로그를 남깁니다.
     * com.kr 패키지 하위의 모든 controller 패키지 내 메서드에 적용됩니다.
     * 
     * @param joinPoint 실행 중인 메서드 정보
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("execution(* com.kr..controller.*.*(..))")
    public Object controllerLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getCanonicalName();
        String methodName = joinPoint.getSignature().getName();

        if (excludeControllerNames.contains(methodName) || excludeControllerNames.contains(className)) {
            return joinPoint.proceed();
        }

        log.info("--- controller 컨트롤러 요청이 시작 되었습니다. START : {}.{}", className, methodName);
        Object obj = joinPoint.proceed();
        log.info("--- controller 컨트롤러 요청이 종료 되었습니다. END : {}.{}", className, methodName);
        return obj;
    }

    /**
     * 모든 Handler 시작과 종료 부분에 로그를 남깁니다.
     * com.kr 패키지 하위의 모든 handler 패키지 내 메서드에 적용됩니다.
     * 
     * @param joinPoint 실행 중인 메서드 정보
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("execution(* com.kr..handler.*.*(..))")
    public Object handlerLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getCanonicalName();
        String methodName = joinPoint.getSignature().getName();

        if (excludeHandlerNames.contains(methodName) || excludeHandlerNames.contains(className)) {
            return joinPoint.proceed();
        }

        log.info("--- handler 핸들러가 시작 되었습니다. START : {}.{}", className, methodName);
        Object obj = joinPoint.proceed();
        log.info("--- handler 핸들러가 종료 되었습니다. END : {}.{}", className, methodName);
        return obj;
    }
}