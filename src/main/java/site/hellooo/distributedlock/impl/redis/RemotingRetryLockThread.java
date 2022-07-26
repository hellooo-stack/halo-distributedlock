package site.hellooo.distributedlock.impl.redis;

import site.hellooo.distributedlock.LockContext;

public class RemotingRetryLockThread extends Thread {
    private LockContext lockContext;

    public RemotingRetryLockThread(LockContext lockContext) {
        setDaemon(true);
        this.lockContext = lockContext;
    }

    @Override
    public void run() {

    }
}
