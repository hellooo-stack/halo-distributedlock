package site.hellooo.distributedlock.impl;

import site.hellooo.distributedlock.LockCallback;
import site.hellooo.distributedlock.LockContext;

public class RedisLockCallback implements LockCallback {

    @Override
    public void afterLocked(LockContext lockContext) {
//        shutdownretryThread();
//        startexpandlockleaseThead();
    }

    @Override
    public void afterQueued(LockContext lockContext) {

    }

    @Override
    public void beforeParking(LockContext lockContext) {
//        start startRetryThread();
    }

    @Override
    public void afterUnlocked(LockContext lockContext) {

    }
}
