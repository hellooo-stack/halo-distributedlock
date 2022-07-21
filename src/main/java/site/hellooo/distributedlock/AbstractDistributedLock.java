package site.hellooo.distributedlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public abstract class AbstractDistributedLock implements Lock {
    @Override
    public void lock() {
        throw new UnsupportedOperationException("operation not supported yet!");
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException("operation not supported yet!");
    }

    @Override
    public boolean tryLock() {
        throw new UnsupportedOperationException("operation not supported yet!");
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("operation not supported yet!");
    }

    @Override
    public void unlock() {
        throw new UnsupportedOperationException("operation not supported yet!");
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("operation not supported yet!");
    }
}
