package ca.frison.utils.ratelimiter;

import java.lang.reflect.Method;

public class StrictRateLimitViolation implements RateLimitViolationPolicy {
    @Override
    public boolean retry() {
        return false;
    }

    @Override
    public void rateLimitExceeded(Method method, Object[] args) throws RuntimeException {
        throw new RateLimitViolationException();
    }
}

