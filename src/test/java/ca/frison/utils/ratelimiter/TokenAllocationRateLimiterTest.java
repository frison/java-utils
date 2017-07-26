package ca.frison.utils.ratelimiter;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

public class TokenAllocationRateLimiterTest {
    @Test
    public void loopOverStatement() {
        RateLimitedObject obj = getTokenAllocatedTestObject();
        int i = 0;
        while(true) {
            obj.println("" + ++i);
        }
    }
    private RateLimitedObject getTokenAllocatedTestObject() {
        return RateLimiter.limitCallsOn(new TestObject(),
                new TokenBucketCallLimit(
                        new LazyTokenBucket(1, Duration.ofMillis(100),
                                new LazyTokenBucket(100000, Duration.ofSeconds(2)))),
                new RetryRateLimitViolation());
    }
}
