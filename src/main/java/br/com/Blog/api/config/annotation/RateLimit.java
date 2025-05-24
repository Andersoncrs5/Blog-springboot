package br.com.Blog.api.config.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int capacity();
    int refillTokens();
    int refillSeconds();
}