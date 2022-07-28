package site.hellooo.distributedlock.impl.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;
import site.hellooo.distributedlock.LockContext;
import site.hellooo.distributedlock.LockHandler;
import site.hellooo.distributedlock.LockState;
import site.hellooo.distributedlock.common.StringUtils;
import site.hellooo.distributedlock.enums.Coordinator;
import site.hellooo.distributedlock.exception.LockStateNotRemovedException;
import site.hellooo.distributedlock.exception.LockStateNotSetException;
import site.hellooo.distributedlock.exception.LockStateRemoveExpiredException;
import site.hellooo.distributedlock.exception.LockStateSetExpiredException;

import java.util.Collections;

public class RedisLockHandler implements LockHandler {

    private final Jedis jedis;

    public RedisLockHandler(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public void setState(LockState<?> lockState, LockContext lockContext) throws LockStateNotSetException {
        String lockRes;
        try {
//            SET key value [EX seconds] [PX milliseconds] [NX|XX]
//            set abc 2 nx ex 10
//            set abc 2 nx px 10000
//            lockRes = this.jedis.set(lockState.getIdentifier(), (String) lockState.getValue(), "NX", "PX", lockContext.lockOptions().getLeaseMilliseconds());
            SetParams setParams = new SetParams().nx().px(lockContext.lockOptions().getLeaseMilliseconds());
            lockRes = this.jedis.set(lockState.getIdentifier(), (String) lockState.getValue(), setParams);
        } catch (Exception e) {
            throw new LockStateNotSetException("executing redis command 'set(nx, px)' error for identifier [" + lockState.getIdentifier() + "]", e);
        }

        if (!"OK".equals(lockRes)) {
            throw new LockStateNotSetException("fail to get lock for identifier [" + lockState.getIdentifier() + "]" + ", value is [" + lockState.getValue() + "]");
        }
    }

    @Override
    public void setStateExpires(LockState<?> lockState, LockContext lockContext, long expireMilliseconds) throws LockStateSetExpiredException {
        throw new UnsupportedOperationException("operation not supported yet!");
    }

    @Override
    public void removeState(LockState<?> lockState, LockContext lockContext) throws LockStateNotRemovedException {
        String unlockScript = "if (redis.call('get', KEYS[1]) == ARGV[1]) then "
                + "return redis.call('del', KEYS[1]); "
                + "else "
                + " return nil;"
                + "end ;";

        Object unlockRes;
        try {
            unlockRes = this.jedis.eval(unlockScript, Collections.singletonList(lockState.getIdentifier()), Collections.singletonList((String) lockState.getValue()));
        } catch (Exception e) {
            throw new LockStateNotRemovedException("executing redis command del failed.");
        }

        if (unlockRes == null) {
            throw new LockStateNotRemovedException("executing redis command del failed.");
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

        if (Thread.currentThread() != lockContext.holdingThread().get()) {
            return false;
        }

        String leaseScript = "if (redis.call('get', KEYS[1]) == ARGV[1]) then return redis.call('pexpire', KEYS[1], ARGV[2]); else return nil; end;";

        Object leaseResult = null;
        try {
            long leaseMilliseconds = lockContext.lockOptions().getLeaseMilliseconds();
            long leaseIntervalMilliseconds = lockContext.lockOptions().getLeaseIntervalMilliseconds();
//            should add log here, lease value should not be greater than interval value
            if (leaseMilliseconds >= leaseIntervalMilliseconds) {
                leaseMilliseconds = leaseIntervalMilliseconds;
            }

            LockState<?> lockState = lockContext.holdingLockState().get();
//            if success, value will be "1"
            leaseResult = jedis.eval(leaseScript, Collections.singletonList(lockState.getIdentifier()), Collections.singletonList(leaseMilliseconds + ""));
        } catch (Exception e) {

        }

        return "1".equals(leaseResult);
    }

    public boolean doRetry(LockContext lockContext) {

        boolean needRetry = false;
        try {
            String lockState = jedis.get(lockContext.lockTarget());
            if (StringUtils.isNotEmpty(lockState)) {
                needRetry = true;
            }
        } catch (Exception e) {

        }

        return needRetry;
    }
}
