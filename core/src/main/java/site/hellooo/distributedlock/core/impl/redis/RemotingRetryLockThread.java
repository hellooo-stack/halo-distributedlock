package site.hellooo.distributedlock.core.impl.redis;

import site.hellooo.distributedlock.core.LockContext;
import site.hellooo.distributedlock.core.common.ClassUtils;
import site.hellooo.distributedlock.core.impl.ReentrantDistributedLock;

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
        return lockContext.options().getRetryIntervalMilliseconds();
    }

    private void doRetry() throws InterruptedException {

        if (lockContext.holdingThread().get() != null) {
            throw new InterruptedException("MSG: lock state holding by current process, no need to fetch lock state in coordinator!");
        }

        if (!(lockContext.lockHandler() instanceof RedisLockHandler)) {
            throw new IllegalArgumentException("Fatal: implementation of lockHandler is not for redis, real type=[" + ClassUtils.getObjClassName(lockContext.lockHandler()) + "], PLEASE CHECK!!!");
        }

        RedisLockHandler lockHandler = (RedisLockHandler) lockContext.lockHandler();
        boolean isStateExists = lockHandler.checkStateExists(lockContext.target());
        if (!isStateExists) {
//             lock hold by other process has been released,
//             then we unpark queued head thread in current process
            ReentrantDistributedLock lock = (ReentrantDistributedLock) lockContext.currentLock();
            lock.unparkQueueHead();
        }
    }
}
