package ca.frison.utils.ratelimiter;

public interface TokenBucket {
    boolean reachedLimit();
    void useTokens(int tokenCount);
    int getTokensLeft(boolean recalculate);
}
