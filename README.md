# ca.frison.java-utils
A variety of thread-safe java-utilities that seem to not exist, or have interfaces that are unpleasant to use. 


## Rate Limited Method Calls
If you've ever integrated with a 3rd-party API that has rate limiting, and don't want to worry about going over the rate limits they provide, a simple way to rate-limit calls may be required. Instead of worrying about how your application may handle rate limit violations, pushing the rate-limiting to the client-side may be desireable. Classes that have methods that require rate-limiting can easily have client side rate limiting.

Let's say you have a class that implements RateLimitedObject:
```java
interface RateLimitedObject {
    @RateLimited
    int rateLimitedPrimativeCall();

    @RateLimited
    NestedTestObject rateLimitedObjectCall();

    @RateLimited
    void println(String text);

    @RateLimited
    void increment();

    int getIncrements();

    int noRateLimit();
}

public class TestObject implements RateLimitedObject {
    ....
}
```

If you want to limit calls on the TestObject to 30 calls per-second, and retry on rate-limit violations, all you need to do is:
```java
public class App {
    public static void main(String[] args) {
        RateLimitedObject rlo = RateLimiter.limitCallsOn(new TestObject(),
            new TokenBucketCallLimit(new LazyTokenBucket(30, Duration.ofSeconds(1)),
            new RetryRateLimitViolation());
    }
}
```

If you want to limit calls on the TestObject to 30 calls per-second *and* 30,000 calls per hour, all you need to do is:
```java
public class App {
    public static void main(String[] args) {
        RateLimitedObject rlo = RateLimiter.limitCallsOn(new TestObject(),
            new TokenBucketCallLimit(new LazyTokenBucket(30, Duration.ofSeconds(1),new LazyTokenBucket(30000, Duration.ofHours(1))
            new RetryRateLimitViolation());
    }
}
```

You can have non-refreshing rate-limits, and throw exceptions on rate-limit violations as well:
```java
public class App {
    public static void main(String[] args) {
        RateLimitedObject rlo = RateLimiter.limitCallsOn(new TestObject(),
                new FixedCallLimit(100),
                new StrictRateLimitViolation());
    }
}
```

See the test-cases for more options on rate-limiting. Or create your own by implementing the RateLimitPolicy and the RateLimitViolationPolicy interfaces. I'll happily include them in the upstream.
