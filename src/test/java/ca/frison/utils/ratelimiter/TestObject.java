package ca.frison.utils.ratelimiter;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

class TestObject implements RateLimitedObject {
    private final AtomicInteger increment;

    public TestObject() {
        this.increment = new AtomicInteger(0);
    }

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
    public void increment() {
        this.increment.addAndGet(1);
    }

    @Override
    public int getIncrements() {
        return this.increment.get();
    }

    @Override
    public int noRateLimit() {
        return 100;
    }
}

class NestedTestObject {
}