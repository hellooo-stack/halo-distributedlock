package site.hellooo.distributedlock.core.impl.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;
import site.hellooo.distributedlock.core.LockContext;
import site.hellooo.distributedlock.core.LockHandler;
import site.hellooo.distributedlock.core.LockState;
import site.hellooo.distributedlock.core.common.ArgChecker;
import site.hellooo.distributedlock.core.common.StringUtils;
import site.hellooo.distributedlock.core.enums.Coordinator;
import site.hellooo.distributedlock.core.exception.LockStateNotRemovedException;
import site.hellooo.distributedlock.core.exception.LockStateNotSetException;
import site.hellooo.distributedlock.core.exception.LockStateRemoveExpiredException;
import site.hellooo.distributedlock.core.exception.LockStateSetExpiredException;

import java.util.Arrays;
import java.util.Collections;

public class RedisLockHandler implements LockHandler {

    private final JedisPool jedisPool;

    public RedisLockHandler(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void setState(LockState<?> lockState, LockContext lockContext) throws LockStateNotSetException {
        ArgChecker.checkNotNull(lockState, "lockState is expected to be not null");
        ArgChecker.checkNotNull(lockState.getIdentifier(), "lockState.getIdentifier() is expected to be not null");
        ArgChecker.checkNotNull(lockState.getValue(), "lockState.getValue() is expected to be not null");
        ArgChecker.checkNotNull(lockContext, "lockContext is expected to be not null");
        ArgChecker.checkNotNull(lockContext.options(), "lockContext.lockOptions() is expected to be not null");
        ArgChecker.check(lockContext.options().getLeaseMilliseconds() > 0, "lockContext.lockOptions().getLeaseMilliseconds() is expected to be > 0");

        String lockResult;
        try (Jedis jedis = jedisPool.getResource()) {
            // SET key value [EX seconds] [PX milliseconds] [NX|XX]
            // set abc 2 nx ex 10
            // set abc 2 nx px 10000
            SetParams setParams = new SetParams().nx().px(lockContext.options().getLeaseMilliseconds());
            lockResult = jedis.set(lockState.getIdentifier(), (String) lockState.getValue(), setParams);
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
        ArgChecker.checkNotNull(lockContext.options(), "lockContext.lockOptions() is expected to be not null");

        String removeStateScript = "if (redis.call('get', KEYS[1]) == ARGV[1]) then return redis.call('del', KEYS[1]); else return nil; end;";
        Object unlockResult;
        try (Jedis jedis = jedisPool.getResource()) {
            unlockResult = jedis.eval(removeStateScript, Collections.singletonList(lockState.getIdentifier()), Collections.singletonList((String) lockState.getValue()));
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
        ArgChecker.checkNotNull(lockContext.options(), "lockContext.lockOptions() is expected to be not null");
        ArgChecker.check(lockContext.options().getLeaseMilliseconds() > 0, "leaseMilliseconds is " + lockContext.options().getLeaseMilliseconds() + " (expected > 0).");

        String leaseScript = "if (redis.call('get', KEYS[1]) == ARGV[1]) then return redis.call('pexpire', KEYS[1], ARGV[2]); else return nil; end;";

        Object leaseResult = null;
        try (Jedis jedis = jedisPool.getResource()) {
            LockState<?> lockState = lockContext.holdingLockState().get();
            // if success, value will be "1"
            leaseResult = jedis.eval(leaseScript, Collections.singletonList(lockState.getIdentifier()), Arrays.asList((String) lockState.getValue(), lockContext.options().getLeaseMilliseconds() + ""));
        } catch (Exception ignored) {
        }

        return "1".equals(leaseResult);
    }

    public boolean checkStateExists(String identifier) {

        boolean isStateExists = false;
        try (Jedis jedis = jedisPool.getResource()) {
            String lockState = jedis.get(identifier);
            if (StringUtils.isNotEmpty(lockState)) {
                isStateExists = true;
            }
        } catch (Exception ignored) {
        }

        return isStateExists;
    }
}
