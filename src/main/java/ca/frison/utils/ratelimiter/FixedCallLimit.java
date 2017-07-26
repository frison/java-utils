package ca.frison.utils.ratelimiter;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.*;
import java.util.PrimitiveIterator;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedCallLimit implements RateLimitPolicy {
    private final int callLimit;
    AtomicInteger callCount = new AtomicInteger(0);

    public FixedCallLimit(int callLimit) {
        this.callLimit = callLimit;
    }

    @Override
    public boolean canExecute(Method method) {
        return callCount.get() < callLimit;

    }

    @Override
    public void registerExecution(Method method) {
        callCount.getAndIncrement();
    }
}

