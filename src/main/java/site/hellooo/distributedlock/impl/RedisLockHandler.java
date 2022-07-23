package site.hellooo.distributedlock.impl;

import redis.clients.jedis.Jedis;
import site.hellooo.distributedlock.LockContext;
import site.hellooo.distributedlock.LockHandler;
import site.hellooo.distributedlock.LockState;
import site.hellooo.distributedlock.enums.Coordinator;

public class RedisLockHandler implements LockHandler {

    private Jedis jedis;

    public RedisLockHandler(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public void setState(LockState<?> lockState, LockContext lockContext) {

    }

    @Override
    public void setStateExpires(LockState<?> lockState, LockContext lockContext, long expireMilliseconds) {

    }

    @Override
    public void removeState(LockState<?> lockState, LockContext lockContext) {

    }

    @Override
    public void removeStateExpires(LockState<?> lockState, LockContext lockContext, long expireMilliseconds) {

    }

    @Override
    public Coordinator coordinatorType() {
        return null;
    }
}
