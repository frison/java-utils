package ca.frison.utils.ratelimiter;

import java.lang.reflect.Method;

public interface RateLimitViolationPolicy {
    boolean retry();
    void rateLimitExceeded(Method method, Object[] args) throws RuntimeException;
}
