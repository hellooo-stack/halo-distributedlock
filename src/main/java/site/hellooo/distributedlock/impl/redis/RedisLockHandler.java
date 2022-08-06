package site.hellooo.distributedlock.impl.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;
import site.hellooo.distributedlock.LockContext;
import site.hellooo.distributedlock.LockHandler;
import site.hellooo.distributedlock.LockState;
import site.hellooo.distributedlock.common.ArgChecker;
import site.hellooo.distributedlock.common.StringUtils;
import site.hellooo.distributedlock.enums.Coordinator;
import site.hellooo.distributedlock.exception.LockStateNotRemovedException;
import site.hellooo.distributedlock.exception.LockStateNotSetException;
import site.hellooo.distributedlock.exception.LockStateRemoveExpiredException;
import site.hellooo.distributedlock.exception.LockStateSetExpiredException;

import java.util.Arrays;
import java.util.Collections;

public class RedisLockHandler implements LockHandler {

    private final Jedis jedis;

    public RedisLockHandler(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public void setState(LockState<?> lockState, LockContext lockContext) throws LockStateNotSetException {
        ArgChecker.checkNotNull(lockState, "lockState is expected to be not null");
        ArgChecker.checkNotNull(lockState.getIdentifier(), "lockState.getIdentifier() is expected to be not null");
        ArgChecker.checkNotNull(lockState.getValue(), "lockState.getValue() is expected to be not null");
        ArgChecker.checkNotNull(lockContext, "lockContext is expected to be not null");
        ArgChecker.checkNotNull(lockContext.lockOptions(), "lockContext.lockOptions() is expected to be not null");
        ArgChecker.check(lockContext.lockOptions().getLeaseMilliseconds() > 0, "lockContext.lockOptions().getLeaseMilliseconds() is expected to be > 0");

        String lockResult;
        try {
//            SET key value [EX seconds] [PX milliseconds] [NX|XX]
//            set abc 2 nx ex 10
//            set abc 2 nx px 10000
//            lockResult = this.jedis.set(lockState.getIdentifier(), (String) lockState.getValue(), "NX", "PX", lockContext.lockOptions().getLeaseMilliseconds());
            SetParams setParams = new SetParams().nx().px(lockContext.lockOptions().getLeaseMilliseconds());
            lockResult = this.jedis.set(lockState.getIdentifier(), (String) lockState.getValue(), setParams);
        } catch (Exception e) {
            throw new LockStateNotSetException("Fatal: executing redis command to set lock state for identifier [" + lockState.getIdentifier() + "] failed!!!", e);
        }

        if (!"OK".equals(lockResult)) {
            throw new LockStateNotSetException("Fatal: executing redis command to set lock state for identifier [" + lockState.getIdentifier() + "] failed!!!");
        }
    }

    @Override
    public void setStateExpires(LockState<?> lockState, LockContext lockContext, long expireMilliseconds) throws LockStateSetExpiredException {
        throw new UnsupportedOperationException("operation not supported yet!");
    }

    @Override
    public void removeState(LockState<?> lockState, LockContext lockContext) throws LockStateNotRemovedException {
        ArgChecker.checkNotNull(lockState, "lockState is expected to be not null");
        ArgChecker.checkNotNull(lockState.getIdentifier(), "lockState.getIdentifier() is expected to be not null");
        ArgChecker.checkNotNull(lockState.getValue(), "lockState.getValue() is expected to be not null");
        ArgChecker.checkNotNull(lockContext, "lockContext is expected to be not null");
        ArgChecker.checkNotNull(lockContext.lockOptions(), "lockContext.lockOptions() is expected to be not null");

        String removeStateScript = "if (redis.call('get', KEYS[1]) == ARGV[1]) then return redis.call('del', KEYS[1]); else return nil; end;";
        Object unlockResult;
        try {
            unlockResult = this.jedis.eval(removeStateScript, Collections.singletonList(lockState.getIdentifier()), Collections.singletonList((String) lockState.getValue()));
        } catch (Exception e) {
            throw new LockStateNotRemovedException("Fatal: executing redis command to remove lock state for identifier [" + lockState.getIdentifier() + "] failed!!!", e);
        }

        if (unlockResult == null) {
            throw new LockStateNotRemovedException("Fatal: executing redis command to remove lock state for identifier [" + lockState.getIdentifier() + "] failed!!!");
        }
    }

    @Override
    public void removeStateExpires(LockState<?> lockState, LockContext lockContext, long expireMilliseconds) throws LockStateRemoveExpiredException {
        throw new UnsupportedOperationException("operation not supported yet!");
    }

    @Override
    public Coordinator coordinatorType() {
        return Coordinator.REDIS_SINGLETON;
    }

    public boolean doLease(LockContext lockContext) {
        ArgChecker.checkNotNull(lockContext, "lockContext is expected to be not null");
        ArgChecker.checkNotNull(lockContext.lockOptions(), "lockContext.lockOptions() is expected to be not null");
        ArgChecker.check(lockContext.lockOptions().getLeaseMilliseconds() > 0, "leaseMilliseconds is " + lockContext.lockOptions().getLeaseMilliseconds() + " (expected > 0).");

        String leaseScript = "if (redis.call('get', KEYS[1]) == ARGV[1]) then return redis.call('pexpire', KEYS[1], ARGV[2]); else return nil; end;";

        Object leaseResult = null;
        try {
            LockState<?> lockState = lockContext.holdingLockState().get();
//            if success, value will be "1"
            leaseResult = jedis.eval(leaseScript, Collections.singletonList(lockState.getIdentifier()), Arrays.asList((String) lockState.getValue(), lockContext.lockOptions().getLeaseMilliseconds() + ""));
        } catch (Exception ignored) {

        }

        return "1".equals(leaseResult);
    }

    public boolean checkStateExists(String identifier) {

        boolean isStateExists = false;
        try {
            String lockState = jedis.get(identifier);
            if (StringUtils.isNotEmpty(lockState)) {
                isStateExists = true;
            }
        } catch (Exception e) {

        }

        return isStateExists;
    }
}
