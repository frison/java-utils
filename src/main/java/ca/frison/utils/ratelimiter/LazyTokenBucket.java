package ca.frison.utils.ratelimiter;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class LazyTokenBucket implements TokenBucket {
    private final Duration growthInterval;
    private final int growthIncrement;
    private final TokenBucket sourceBucket;
    private final AtomicInteger tokensLeft;

    private volatile Instant nextBucketGrowthTime;

    LazyTokenBucket(int growthIncrement, Duration growthInterval) {
        this(growthIncrement, growthInterval, null);
    }

    LazyTokenBucket(int growthIncrement, Duration growthInterval, TokenBucket sourceBucket) {
        this.growthInterval = growthInterval;
        this.growthIncrement = growthIncrement;

        this.sourceBucket = sourceBucket;
        this.tokensLeft = new AtomicInteger(this.growthIncrement);
        this.nextBucketGrowthTime = getNextGrowthTime();
    }

    Instant getNextGrowthTime() {
        return Instant.now().plus(this.growthInterval);
    }

    @Override
    public int getTokensLeft(boolean recalculate) {
        if(recalculate) {
            updateTokens();
        }
        return tokensLeft.get();
    }

    @Override
    public void useTokens(int count) {
        tokensLeft.addAndGet(-1*count);
    }

    @Override
    public boolean reachedLimit() {
        if(getTokensLeft(false) != 0) {
            return false;
        }

        return getTokensLeft(true) == 0;
    }

    public synchronized void updateTokens() {
        if(Instant.now().isBefore(this.nextBucketGrowthTime)) {
            return;
        }

        int growthAmount;
        if(sourceBucket == null) {
            growthAmount = this.growthIncrement;
        } else {
            if(sourceBucket.reachedLimit()) {
                return;
            }
            growthAmount = Math.min(sourceBucket.getTokensLeft(true), this.growthIncrement);
            sourceBucket.useTokens(growthAmount);
        }

        this.nextBucketGrowthTime = getNextGrowthTime();
        this.tokensLeft.getAndAdd(growthAmount);
    }


}
