package site.hellooo.distributedlock.core.impl;

import site.hellooo.distributedlock.core.LockCallback;
import site.hellooo.distributedlock.core.LockContext;
import site.hellooo.distributedlock.core.enums.Coordinator;
import site.hellooo.distributedlock.core.impl.redis.RedisLockCallback;

public class LockCallbackFactory {
    public static LockCallback of(Coordinator coordinator, LockContext lockContext) {
        switch (coordinator) {
            case REDIS_SINGLETON:
                return new RedisLockCallback(lockContext);
        }

        throw new UnsupportedOperationException("Fatal: LockCallback with coordinator type [" + coordinator.getName() + "] is not implemented yet!!!");
    }
}
