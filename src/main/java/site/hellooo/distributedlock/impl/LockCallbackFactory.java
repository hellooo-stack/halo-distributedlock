package site.hellooo.distributedlock.impl;

import site.hellooo.distributedlock.LockCallback;
import site.hellooo.distributedlock.LockContext;
import site.hellooo.distributedlock.enums.Coordinator;
import site.hellooo.distributedlock.impl.redis.RedisLockCallback;

public class LockCallbackFactory {
    public static LockCallback of(Coordinator coordinator, LockContext lockContext) {
        switch (coordinator) {
            case REDIS_SINGLETON:
                return new RedisLockCallback(lockContext);
        }

        throw new UnsupportedOperationException("Fatal: LockCallback with coordinator type [" + coordinator.getName() + "] is not implemented yet!!!");
    }
}
