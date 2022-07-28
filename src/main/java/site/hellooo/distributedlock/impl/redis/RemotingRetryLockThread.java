package site.hellooo.distributedlock.impl.redis;

import site.hellooo.distributedlock.LockContext;

public class RemotingRetryLockThread extends AbstractRemotingThread {

    public RemotingRetryLockThread(LockContext lockContext) {
        super(lockContext);
    }

    @Override
    protected void execute() throws InterruptedException {
        doRetry();
    }

    @Override
    protected long getExecuteInterval() {
        return lockContext.lockOptions().getRetryIntervalMilliseconds();
    }

    private void doRetry() {

        if (!(lockContext.lockHandler() instanceof RedisLockHandler)) {
            throw new IllegalArgumentException("Fatal: implementation of lockHandler is not redis, please check!");
        }

//        todo need some threading check

        RedisLockHandler lockHandler = (RedisLockHandler) lockContext.lockHandler();
        lockHandler.doRetry(lockContext);
    }
}
