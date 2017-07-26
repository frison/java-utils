package ca.frison.utils.ratelimiter;

import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    private static ConcurrentHashMap<Object, RateLimitedDecorator> references = new ConcurrentHashMap<>();
    public static <T> T limitCallsOn(T rateLimited, RateLimitPolicy rlp, RateLimitViolationPolicy rlvp) {
        references.put(rateLimited, new RateLimitedDecorator(rateLimited, rlp, rlvp));
        return (T) Proxy.newProxyInstance(
                rateLimited.getClass().getClassLoader(),
                rateLimited.getClass().getInterfaces(),
                references.get(rateLimited));
    }
}