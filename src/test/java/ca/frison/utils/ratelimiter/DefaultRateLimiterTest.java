package ca.frison.utils.ratelimiter;

import org.junit.Assert;
import org.junit.Test;

public class DefaultRateLimiterTest {
    @Test
    public void fixedRateLimiterShouldUseDefaultOnRateLimitViolations() throws Exception {
        RateLimitedObject to = getDefaultTestObject(0);
        Assert.assertEquals(0, to.rateLimitedPrimativeCall());
        Assert.assertEquals(null, to.rateLimitedObjectCall());
    }

    @Test
    public void fixedRateLimiterShouldNotUseDefault() throws Exception {
        int callLimit = 10;
        RateLimitedObject to = getDefaultTestObject(2*callLimit);
        for (int i = 0; i < callLimit; i++) {
            Assert.assertEquals(50, to.rateLimitedPrimativeCall());
            Assert.assertNotNull(to.rateLimitedObjectCall());
        }
    }

    @Test
    public void fixedRateLimiterShouldUseDefaultOnRateViolations() throws Exception {
        int callLimit = 10;
        RateLimitedObject to = getDefaultTestObject(2*callLimit);
        for (int i = 0; i < callLimit; i++) {
            Assert.assertEquals(50, to.rateLimitedPrimativeCall());
            Assert.assertNotNull(to.rateLimitedObjectCall());
        }

        Assert.assertEquals(0, to.rateLimitedPrimativeCall());
        Assert.assertEquals(null, to.rateLimitedObjectCall());
    }

    private RateLimitedObject getDefaultTestObject(int fixedCallLimit) {
        return RateLimiter.limitCallsOn(new TestObject(),
                new FixedCallLimit(fixedCallLimit),
                new DefaultValueRateLimitViolation());
    }
}
