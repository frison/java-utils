package ca.frison.utils.ratelimiter;

import java.time.Instant;

class TestObject implements RateLimitedObject {
    @Override
    public int rateLimitedPrimativeCall() {
        return 50;
    }

    @Override
    public NestedTestObject rateLimitedObjectCall() {
        return new NestedTestObject();
    }

    @Override
    public void println(String text) {
        System.out.println(String.format("%s : %s", Instant.now().toString(), text));
        System.out.flush();
    }

    @Override
    public int noRateLimit() {
        return 100;
    }
}

class NestedTestObject {
}