package com.mercury.botcreator.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class AgencyClientConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor requestLoggingInterceptor() {
        return template -> {
            log.info("[Feign Request] {} {}", template.method(), template.url());
            if (template.body() != null) {
                log.info("[Feign Request Body] {}", new String(template.body(), StandardCharsets.UTF_8));
            }
        };
    }

    @Bean
    public ErrorDecoder responseLoggingErrorDecoder() {
        return new ErrorDecoder() {
            private final ErrorDecoder defaultDecoder = new Default();
            @Override
            public Exception decode(String methodKey, Response response) {
                try {
                    String body = response.body() != null
                            ? new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8)
                            : "[No body]";
                    log.warn("[Feign Response] {} -> status: {}, body: {}", methodKey, response.status(), body);
                } catch (IOException e) {
                    log.warn("Error reading Feign response body", e);
                }
                return defaultDecoder.decode(methodKey, response);
            }
        };
    }


}
