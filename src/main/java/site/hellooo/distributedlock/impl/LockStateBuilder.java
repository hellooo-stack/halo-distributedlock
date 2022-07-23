package site.hellooo.distributedlock.impl;


import site.hellooo.distributedlock.LockState;
import site.hellooo.distributedlock.common.ArgChecker;
import site.hellooo.distributedlock.common.StringUtils;
import site.hellooo.distributedlock.config.LockOptions;
import site.hellooo.distributedlock.enums.Coordinator;

public class LockStateBuilder {

    private LockOptions DEFAULT_LOCK_OPTIONS = LockOptions.ofDefault();
    private final Coordinator DEFAULT_COORDINATOR = DEFAULT_LOCK_OPTIONS.getCoordinator();
    private LockOptions lockOptions = DEFAULT_LOCK_OPTIONS;
    private Coordinator coordinator = DEFAULT_COORDINATOR;
    private String identifier;

    private Object value;


    public LockStateBuilder() {

    }

    public LockStateBuilder(LockOptions lockOptions) {
        this.lockOptions = lockOptions;
        this.coordinator = lockOptions.getCoordinator();
    }

    public LockStateBuilder coordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
        return this;
    }

    public LockStateBuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public LockStateBuilder value(Object value) {
        this.value = value;
        return this;
    }

    public LockState<?> build() {
        ArgChecker.check(StringUtils.isEmpty(identifier), "identifier is empty (expected not empty).");
//        ArgChecker.check(value != null, "value is null (expected not null).");

        String generatedIdentifier = lockOptions.getIdentifierPrefix() + this.identifier;
        generatedIdentifier = generatedIdentifier + lockOptions.getIdentifierSuffix();

        switch (coordinator) {
            case REDIS_SINGLETON:
                if (!(this.value instanceof String)) {
                    throw new IllegalArgumentException("Fatal: invalid type of 'value', type is " + value.getClass().getSimpleName());
                }

                return new RedisLockState(generatedIdentifier);
            case REDIS_CLUSTER:
            case ZOOKEEPER:
        }

        throw new UnsupportedOperationException("Fatal: coordinator with type '" + coordinator.getName() + "' is not implemented yet!");
    }
}
