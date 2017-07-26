package ca.frison.utils.ratelimiter;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class TokenBucketCallLimit implements RateLimitPolicy {
    private final TokenBucket tokenBucket;

    public TokenBucketCallLimit(TokenBucket bucket) {
        tokenBucket = bucket;
    }

    @Override
    public boolean canExecute(Method method) {
        return !tokenBucket.reachedLimit();
    }

    @Override
    public void registerExecution(Method method) {
        tokenBucket.useTokens(1);
    }
}

