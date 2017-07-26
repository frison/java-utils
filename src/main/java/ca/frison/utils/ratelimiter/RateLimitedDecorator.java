package ca.frison.utils.ratelimiter;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class RateLimitedDecorator<T> implements InvocationHandler {
    private final RateLimitPolicy rateLimitPolicy;
    private final RateLimitViolationPolicy rateLimitViolationPolicy;
    private volatile T rateLimitedObject;

    RateLimitedDecorator(T rateLimited, RateLimitPolicy rlp, RateLimitViolationPolicy rlvp) {
        rateLimitedObject = rateLimited;
        rateLimitPolicy = rlp;
        rateLimitViolationPolicy = rlvp;
    }

    private synchronized boolean preallocateUsage(Object proxy, Method method, Object[] args) {
        // By pre-allocating usage we don't pay the cost of the method calls in a
        // synchronized method.
        if(rateLimitPolicy.canExecute(method)) {
            rateLimitPolicy.registerExecution(method);
            return true;
        }
        return false;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getAnnotation(RateLimited.class) == null) {
            return method.invoke(rateLimitedObject, args);
        }

        do {
            if (preallocateUsage(proxy, method, args)) {
                return method.invoke(rateLimitedObject, args);
            }
        } while(rateLimitViolationPolicy.retry());

        rateLimitViolationPolicy.rateLimitExceeded(method, args);

        // https://stackoverflow.com/questions/2891970/getting-default-value-for-java-primitive-types
        return Array.get(Array.newInstance(method.getReturnType(), 1), 0);
    }
}
