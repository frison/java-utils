package ca.frison.utils.ratelimiter;

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
