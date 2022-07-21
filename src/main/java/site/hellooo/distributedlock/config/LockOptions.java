package site.hellooo.distributedlock.config;

import site.hellooo.distributedlock.Reusable;
import site.hellooo.distributedlock.common.ArgChecker;

import java.io.Serializable;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class LockOptions implements Reusable<LockOptions>, Serializable {

    private final String lockNamePrefix;
    private final long lease;
    private final TimeUnit leaseTimeUnit;

    private LockOptions(String lockNamePrefix, long lease, TimeUnit leaseTimeUnit) {
        ArgChecker.check(lockNamePrefix != null, "lockNamePrefix is null (expected not null).");
        ArgChecker.check(lease > 0, "lease is " + lease + " (expected > 0).");
        ArgChecker.check(leaseTimeUnit != null, "leaseTimeUnit is null (expected not null).");

        this.lockNamePrefix = lockNamePrefix;
        this.lease = lease;
        this.leaseTimeUnit = leaseTimeUnit;
    }

    public static LockOptions ofDefault() {
        return new LockOptionsBuilder().build();
    }

    public static LockOptionsBuilder options() {
        return new LockOptionsBuilder();
    }

    public String getLockNamePrefix() {
        return this.lockNamePrefix;
    }

    public long getLease() {
        return this.lease;
    }

    public TimeUnit getLeaseTimeUnit() {
        return this.leaseTimeUnit;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LockOptions.class.getSimpleName() + "[", "]")
                .add("lockNamePrefix=" + lockNamePrefix)
                .add("lease=" + lease)
                .add("leaseTimeUnit=" + leaseTimeUnit)
                .toString();
    }

    @Override
    public LockOptions copy() {
        return new LockOptions(this.lockNamePrefix, lease, leaseTimeUnit);
    }

    public static class LockOptionsBuilder {
        private static final String DEFAULT_LOCK_NAME_PREFIX = "";
        private static final long DEFAULT_LEASE = 1L;
        private static final TimeUnit DEFAULT_LEASE_TIME_UNIT = TimeUnit.SECONDS;

        private String lockNamePrefix = DEFAULT_LOCK_NAME_PREFIX;
        private long lease = DEFAULT_LEASE;
        private TimeUnit leaseTimeUnit = DEFAULT_LEASE_TIME_UNIT;

        LockOptionsBuilder() {

        }

        public LockOptionsBuilder lockNamePrefix(String lockNamePrefix) {
            this.lockNamePrefix = lockNamePrefix;
            return this;
        }

        public LockOptionsBuilder lease(long lease) {
            this.lease = lease;
            return this;
        }

        public LockOptionsBuilder leaseTimeUnit(TimeUnit leaseTimeUnit) {
            this.leaseTimeUnit = leaseTimeUnit;
            return this;
        }

        public LockOptions build() {
            return new LockOptions(lockNamePrefix, lease, leaseTimeUnit);
        }
    }

}
