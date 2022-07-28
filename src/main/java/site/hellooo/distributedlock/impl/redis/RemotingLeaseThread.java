package site.hellooo.distributedlock.impl.redis;

import site.hellooo.distributedlock.LockContext;

public class RemotingLeaseThread extends AbstractRemotingThread {

    public RemotingLeaseThread(LockContext lockContext) {
        super(lockContext);
    }

    @Override
    protected void execute() throws InterruptedException {
        doLease();
    }

    @Override
    protected long getExecuteInterval() {
        return lockContext.lockOptions().getLeaseIntervalMilliseconds();
    }

    private void doLease() throws InterruptedException {

        if (!(lockContext.lockHandler() instanceof RedisLockHandler)) {
            throw new IllegalArgumentException("Fatal: implementation of lockHandler is not redis, please check!");
        }

        RedisLockHandler lockHandler = (RedisLockHandler) lockContext.lockHandler();
        lockHandler.doLease(lockContext);
    }
}
