package site.hellooo.distributedlock.impl.redis;

import site.hellooo.distributedlock.LockCallback;
import site.hellooo.distributedlock.LockContext;
import site.hellooo.distributedlock.enums.Coordinator;

public class LockCallbackFactory {
    public static LockCallback of(Coordinator coordinator, LockContext lockContext) {
        switch (coordinator) {
            case REDIS_SINGLETON:
                return new RedisLockCallback(lockContext);
        }

        throw new UnsupportedOperationException("Fatal: coordinator with type '" + coordinator.getName() + "' is not implemented yet!");
    }
}
