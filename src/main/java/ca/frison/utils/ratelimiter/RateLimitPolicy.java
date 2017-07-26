package ca.frison.utils.ratelimiter;

import java.lang.reflect.Method;

public interface RateLimitPolicy {
    boolean canExecute(Method method);
    void registerExecution(Method method);
}

