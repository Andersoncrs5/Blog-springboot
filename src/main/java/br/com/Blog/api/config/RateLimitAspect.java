package br.com.Blog.api.config;

import br.com.Blog.api.config.GlobalExceptionHandler;
import br.com.Blog.api.config.annotation.RateLimit;
import io.github.bucket4j.*;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.concurrent.*;

@Aspect
@Component
public class RateLimitAspect {

    private final ConcurrentHashMap<String, Bucket> cache = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object applyRateLimit(ProceedingJoinPoint pjp, RateLimit rateLimit) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String key = request.getRemoteAddr() + ":" + pjp.getSignature();

        Bucket bucket = cache.computeIfAbsent(key, k ->
                Bucket4j.builder()
                        .addLimit(Bandwidth.classic(
                                rateLimit.capacity(),
                                Refill.greedy(rateLimit.refillTokens(), Duration.ofSeconds(rateLimit.refillSeconds()))
                        ))
                        .build()
        );

        if (bucket.tryConsume(1)) {
            return pjp.proceed();
        } else {
            throw new GlobalExceptionHandler.RateLimitExceededException();
        }
    }
}
