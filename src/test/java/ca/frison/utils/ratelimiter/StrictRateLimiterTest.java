package ca.frison.utils.ratelimiter;
import org.junit.Test;

public class StrictRateLimiterTest {

    @Test(expected = RateLimitViolationException.class)
    public void strictRateLimiterShouldThrowExceptionOnRateLimitViolations() throws Exception {
        RateLimitedObject to = getStrictTestObject(0);
        to.rateLimitedPrimativeCall();
    }

    @Test
    public void strictRateLimiterShouldNotThrowExceptionOnFunctionThatIsNotRateLimited() throws Exception {
        RateLimitedObject to = getStrictTestObject(0);
        to.noRateLimit();
    }

    @Test
    public void strictRateLimiterShouldNotThrowExceptionIfRateLimitNotReached() throws Exception {
        int callLimit = 10;
        RateLimitedObject to = getStrictTestObject(callLimit);
        for (int i = 0; i < callLimit; i++) {
            to.rateLimitedPrimativeCall();
        }
    }

    @Test(expected = RateLimitViolationException.class)
    public void strictRateLimiterShouldThrowExceptionIfRateLimitExceededAfterSuccessfulCalls() throws Exception {
        int callLimit = 10;
        RateLimitedObject to = getStrictTestObject(callLimit);
        for (int i = 0; i < callLimit; i++) {
            to.rateLimitedPrimativeCall();
        }
        to.rateLimitedPrimativeCall();
    }

    private RateLimitedObject getStrictTestObject(int fixedCallLimit) {
        return RateLimiter.limitCallsOn(new TestObject(),
                new FixedCallLimit(fixedCallLimit),
                new StrictRateLimitViolation());
    }
}

