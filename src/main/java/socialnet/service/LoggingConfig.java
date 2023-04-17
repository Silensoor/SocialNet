package socialnet.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingConfig {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* socialnet.controller.*.*(..))")
    public void methodExecutingDebug() {
    }

    @Pointcut("execution(* socialnet.service.*.*(..))")
    public void methodExecutingInfo() {
    }

    @AfterReturning(pointcut = "methodExecutingInfo()", returning = "returningValue")
    public void recordSuccessfulExecutionInfo(JoinPoint joinPoint, Object returningValue) {

        if (returningValue != null) {

            log.info("Старт метода :" + joinPoint.getSignature().getName() +
                    " в классе: " + joinPoint.getSourceLocation().getWithinType().getName() +
                    " результат: " + returningValue);
        }
        else {

            log.info("Старт метода :" + joinPoint.getSignature().getName() +
                    " в классе: " + joinPoint.getSourceLocation().getWithinType().getName());
        }
    }

    @AfterReturning(pointcut = "methodExecutingDebug()", returning = "returningValue")
    public void recordSuccessfulExecutionDebug(JoinPoint joinPoint, Object returningValue) {
        if (returningValue != null) {

            log.debug("Старт метода :" + joinPoint.getSignature().getName() +
                    " в классе : " + joinPoint.getSourceLocation().getWithinType().getName() +
                    " результат : " + returningValue);

        }
        else {

            log.debug("Старт метода :" + joinPoint.getSignature().getName() +
                    " в классе: " + joinPoint.getSourceLocation().getWithinType().getName());
        }
    }

    @AfterThrowing(pointcut = "methodExecutingInfo() || methodExecutingDebug()", throwing = "exception")
    public void recordFailedExecutionInfo(JoinPoint joinPoint, Exception exception) {

        log.error("Ошибка метода: " + joinPoint.getSignature().getName() +
                " в классе" + joinPoint.getSourceLocation().getWithinType().getName() +
                " результат : " + exception);
    }
}
