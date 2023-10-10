package site.hellooo.distributedlock.core.impl;

import redis.clients.jedis.JedisPool;
import site.hellooo.distributedlock.core.LockHandler;
import site.hellooo.distributedlock.core.common.StringUtils;
import site.hellooo.distributedlock.core.config.LockOptions;
import site.hellooo.distributedlock.core.enums.Coordinator;
import site.hellooo.distributedlock.core.exception.BuilderEssentialFieldNotSetException;
import site.hellooo.distributedlock.core.impl.redis.RedisLockHandler;

public class ReentrantDistributedLockBuilder {

    private static final LockOptions DEFAULT_LOCK_OPTIONS = LockOptions.ofDefault();

    private LockOptions lockOptions = DEFAULT_LOCK_OPTIONS;

    private String lockTarget = null;

    private JedisPool jedisPool;

    public ReentrantDistributedLockBuilder lockOptions(LockOptions lockOptions) {
        this.lockOptions = lockOptions;
        return this;
    }

    public ReentrantDistributedLockBuilder lockTarget(String lockTarget) {
        this.lockTarget = lockTarget;
        return this;
    }

    public ReentrantDistributedLockBuilder jedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        return this;
    }

    public ReentrantDistributedLock build() {

        if (StringUtils.isEmpty(lockTarget)) {
            throw new BuilderEssentialFieldNotSetException("Fatal: miss essential component 'lockTarget'!");
        }

        LockHandler lockHandler = null;
        Coordinator coordinator = lockOptions.getCoordinator();
        switch (coordinator) {
            case REDIS_SINGLETON:
                if (this.jedisPool == null) {
                    throw new BuilderEssentialFieldNotSetException("Fatal: miss essential component 'jedis'!");
                }

                lockHandler = new RedisLockHandler(this.jedisPool);
                break;
            case REDIS_CLUSTER:
            case ZOOKEEPER:
                throw new UnsupportedOperationException("Fatal: coordinator with type '" + coordinator.getName() + "' is not implemented yet!");
        }

        return new ReentrantDistributedLock(lockOptions, lockTarget, lockHandler);
    }
}
