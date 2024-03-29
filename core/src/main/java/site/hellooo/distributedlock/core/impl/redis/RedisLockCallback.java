package site.hellooo.distributedlock.core.impl.redis;

import site.hellooo.distributedlock.core.LockCallback;
import site.hellooo.distributedlock.core.LockContext;

import java.util.concurrent.atomic.AtomicReference;

public class RedisLockCallback implements LockCallback {

    /**
     * this thread is responsible for lease the redis key expire time
     */
    private final AtomicReference<Thread> leaseThreadReference = new AtomicReference<>();

    /**
     * this thread is responsible for communicating with the coordinator to
     * tryLock endlessly when no thread of current process hold the lock
     */
    private final AtomicReference<Thread> retryLockThreadReference = new AtomicReference<>();

    private LockContext lockContext;

    public RedisLockCallback() {

    }

    public RedisLockCallback(LockContext lockContext) {
        this.lockContext = lockContext;
    }

    private void startRetryLockThread() {
        Thread retryLockThread = retryLockThreadReference.get();
        while (retryLockThread == null || retryLockThread.getState() == Thread.State.TERMINATED) {
            if (retryLockThreadReference.compareAndSet(retryLockThread, new RemotingRetryLockThread(lockContext))) {
                retryLockThread = retryLockThreadReference.get();
                // one process have only one running retryLockThread
                retryLockThread.start();
            }
        }
    }

    private void shutdownRetryLockThread() {
        Thread retryLockThread = retryLockThreadReference.get();
        if (retryLockThread != null && retryLockThread.isAlive()) {
            retryLockThread.interrupt();
        }
    }

    private void startLeaseThread() {
        Thread leaseThread = leaseThreadReference.get();
        while (leaseThread == null || leaseThread.getState() == Thread.State.TERMINATED) {
            if (leaseThreadReference.compareAndSet(null, new RemotingLeaseThread(lockContext))) {
                leaseThread = leaseThreadReference.get();
            }
        }

        leaseThread.start();
    }

    private void shutdownLeaseThread() {
        Thread leaseThread = leaseThreadReference.get();
        if (leaseThread != null && leaseThread.isAlive()) {
            leaseThread.interrupt();
        }
    }

    @Override
    public void afterLocked(LockContext lockContext) {
        shutdownRetryLockThread();
        startLeaseThread();
    }

    @Override
    public void beforeParking(LockContext lockContext) {
        // if current process not holding the lock, then we should start the retry lock thread
        if (lockContext.holdingThread().get() == null) {
            startRetryLockThread();
        }
    }

    @Override
    public void afterUnlocked(LockContext lockContext) {
        shutdownLeaseThread();
    }
}
