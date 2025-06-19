package com.mercury.botcreator.aspect;

import com.mercury.botcreator.aspect.stereotype.ValidateApiResponse;
import com.mercury.botcreator.client.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ApiResponseValidationAspect {

    @Around("@annotation(validateApiResponse)")
    public Object validateFeignResponse(ProceedingJoinPoint pjp, ValidateApiResponse validateApiResponse) throws Throwable {
        Object result = pjp.proceed();

        if (result instanceof ApiResponse<?> response) {
            String op = validateApiResponse.operation();

            if (response.getCode() != 200 || !"OK".equalsIgnoreCase(response.getStatus())) {
                throw new IllegalStateException("[" + op + "] Failed with code=" + response.getCode() +
                        ", status=" + response.getStatus() + ", message=" + response.getMessage());
            }

            if (response.getData() == null) {
                throw new IllegalStateException("[" + op + "] Data is null");
            }
        }

        return result;
    }
}
