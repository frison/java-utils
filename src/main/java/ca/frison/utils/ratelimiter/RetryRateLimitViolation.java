package ca.frison.utils.ratelimiter;

import java.lang.reflect.Method;

public class RetryRateLimitViolation implements RateLimitViolationPolicy {

    @Override
    public boolean retry() {
        return true;
    }

    @Override
    public void rateLimitExceeded(Method method, Object[] args) throws RuntimeException {
    }
}
