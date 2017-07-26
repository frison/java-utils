package ca.frison.utils.ratelimiter;

import java.lang.reflect.Method;

public class DefaultValueRateLimitViolation implements RateLimitViolationPolicy {
    @Override
    public boolean retry() {
        return false;
    }

    @Override
    public void rateLimitExceeded(Method method, Object[] args) throws RuntimeException {
        // Do nothing, so the default value of the method type is returned
    }
}
