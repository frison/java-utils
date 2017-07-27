package ca.frison.utils.ratelimiter;

import net.jodah.concurrentunit.ConcurrentTestCase;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class TokenAllocationRateLimiterTest extends ConcurrentTestCase {

    @Test
    public void shouldBlockOnSecondBucketsLimit() throws Throwable {
        BucketTest bt = new BucketTest(10, Duration.ofMillis(1000), 30, Duration.ofSeconds(10));
        runTest(bt, 5, 30, 5);
    }

    @Test(expected = TimeoutException.class)
    public void shouldTimeoutIfWaitTimeIsReduced() throws Throwable {
        BucketTest bt = new BucketTest(10, Duration.ofSeconds(1), 30, Duration.ofSeconds(10));
        runTest(bt, 5, 30, -1000);
    }

    @Test
    public void shouldWorkOnCallLimitBeingNotAMultipleOfBucketRefillSizes() throws Throwable {
        BucketTest bt = new BucketTest(10, Duration.ofSeconds(1), 30, Duration.ofSeconds(10));
        runTest(bt, 5, 35, 5);
    }

    @Test
    public void shouldBePerformantOnLargeBuckets() throws Throwable {
        BucketTest bt = new BucketTest(250000, Duration.ofMillis(100), 500000, Duration.ofMillis(500));
        runTest(bt, 2, 1000000, 50);
    }

    @Test
    public void shouldWorkOnCoprimeBuckets() throws Throwable {
        BucketTest bt = new BucketTest(25741, Duration.ofMillis(100), 104281, Duration.ofMillis(400));
        System.out.println(bt.durationForCallCount(150000));
        runTest(bt, 2, 150000, 50);
    }

    private void runTest(BucketTest bt, int threadCount, int callLimit, int waitPadding) throws Throwable{
        RateLimitedObject obj = bt.getRateLimitedObject();
        for(int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                while(obj.getIncrements() < callLimit) {
                    obj.increment();
                }
                resume();
            }).start();
        }

        await(bt.durationForCallCount(callLimit) + waitPadding);

        assertThat("Expected number of calls not made",
                obj.getIncrements(),
                greaterThanOrEqualTo(callLimit));
    }

    class BucketTest {
        private Duration firstBucketRefillInterval;
        private int firstBucketRefillSize;

        private Duration secondBucketRefillInterval;
        private int secondBucketRefillSize;

        public BucketTest(
                int firstBucketRefillSize,
                Duration firstBucketRefillInterval,
                int secondBucketRefillSize,
                Duration secondBucketRefillInterval) {
            this.firstBucketRefillInterval = firstBucketRefillInterval;
            this.firstBucketRefillSize = firstBucketRefillSize;
            this.secondBucketRefillInterval = secondBucketRefillInterval;
            this.secondBucketRefillSize = secondBucketRefillSize;
        }

        public RateLimitedObject getRateLimitedObject() {
            return RateLimiter.limitCallsOn(new TestObject(),
                    new TokenBucketCallLimit(
                            new LazyTokenBucket(firstBucketRefillSize, firstBucketRefillInterval,
                                    new LazyTokenBucket(secondBucketRefillSize, secondBucketRefillInterval))),
                    new RetryRateLimitViolation());
        }

        public long durationForCallCount(int callCount) {
            long rval = 0;

            int firstBucket = firstBucketRefillSize;
            int secondBucket = secondBucketRefillSize;
            while(callCount > 0) {
                callCount -= firstBucket;
                // Still calls left to be made?
                // We have to refill the first bucket from the second bucket
                // and incur the cost of the first bucket's refill time
                if(callCount > 0) {
                    rval += firstBucketRefillInterval.toMillis();
                    // If the second bucket is empty, we need to incur the
                    // cost of the second bucket's refill time, and fill it back up
                    if(secondBucket == 0) {
                        rval += secondBucketRefillInterval.toMillis();
                        secondBucket = secondBucketRefillSize;
                    }
                    // We don't want to take too much from the second bucket
                    firstBucket = Math.min(firstBucketRefillSize, secondBucket);
                    secondBucket -= firstBucket;
                }

            }

            return rval;
        }
    }
}
