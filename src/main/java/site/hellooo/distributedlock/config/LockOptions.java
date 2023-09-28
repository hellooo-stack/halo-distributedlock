package site.hellooo.distributedlock.config;

import site.hellooo.distributedlock.Reusable;
import site.hellooo.distributedlock.common.ArgChecker;
import site.hellooo.distributedlock.enums.Coordinator;

import java.io.Serializable;
import java.util.StringJoiner;

// Maybe a little hardcode for redis, It still needs to do some work if you want to use other coordinators,
// such as zookeeper, etcd, etc.
public class LockOptions implements Reusable<LockOptions>, Serializable {

    // prefix of lock identifier
    private final String identifierPrefix;
    // suffix of lock identifier
    private final String identifierSuffix;
    // milliseconds of retry thread execute interval
    private final long retryIntervalMilliseconds;
    //  milliseconds of lease thread execute interval, should be smaller than leaseMilliseconds
    private final long leaseIntervalMilliseconds;
    // milliseconds to lease per time
    private final long leaseMilliseconds;
    private final Coordinator coordinator;

    private LockOptions(final String identifierPrefix,
                        final String identifierSuffix,
                        final long retryIntervalMilliseconds,
                        final long leaseIntervalMilliseconds,
                        final long leaseMilliseconds,
                        final Coordinator coordinator) {
        ArgChecker.check(identifierPrefix != null, "identifierPrefix is null (expected not null).");
        ArgChecker.check(identifierSuffix != null, "identifierSuffix is null (expected not null).");
        ArgChecker.check(retryIntervalMilliseconds > 0, "retryIntervalMilliseconds is " + retryIntervalMilliseconds + " (expected > 0).");
        ArgChecker.check(leaseIntervalMilliseconds > 0, "leaseIntervalMilliseconds is " + leaseIntervalMilliseconds + " (expected > 0).");
        ArgChecker.check(leaseMilliseconds > 0, "leaseMilliseconds is " + leaseMilliseconds + " (expected > 0).");
        ArgChecker.check(leaseMilliseconds > leaseIntervalMilliseconds, "leaseMilliseconds less than leaseIntervalMilliseconds (expected greater than leaseIntervalMilliseconds).");
        ArgChecker.check(coordinator != null, "coordinator is null (expected not null).");

        this.identifierPrefix = identifierPrefix;
        this.identifierSuffix = identifierSuffix;
        this.retryIntervalMilliseconds = retryIntervalMilliseconds;
        this.leaseIntervalMilliseconds = leaseIntervalMilliseconds;
        this.leaseMilliseconds = leaseMilliseconds;
        this.coordinator = coordinator;
    }

    public static LockOptions ofDefault() {
        return new LockOptionsBuilder().build();
    }

    public static LockOptionsBuilder options() {
        return new LockOptionsBuilder();
    }

    public String getIdentifierPrefix() {
        return this.identifierPrefix;
    }

    public String getIdentifierSuffix() {
        return this.identifierSuffix;
    }

    public long getRetryIntervalMilliseconds() {
        return this.retryIntervalMilliseconds;
    }

    public long getLeaseIntervalMilliseconds() {
        return this.leaseIntervalMilliseconds;
    }

    public long getLeaseMilliseconds() {
        return this.leaseMilliseconds;
    }

    public Coordinator getCoordinator() {
        return this.coordinator;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LockOptions.class.getSimpleName() + "[", "]")
                .add("identifierPrefix=" + identifierPrefix)
                .add("identifierSuffix=" + identifierSuffix)
                .add("retryIntervalMilliseconds=" + retryIntervalMilliseconds)
                .add("leaseIntervalMilliseconds=" + leaseIntervalMilliseconds)
                .add("leaseMilliseconds=" + leaseMilliseconds)
                .add("coordinator=" + coordinator.getName())
                .toString();
    }

    @Override
    public LockOptions copy() {
        return new LockOptions(identifierPrefix, identifierSuffix, retryIntervalMilliseconds, leaseIntervalMilliseconds, leaseMilliseconds, coordinator);
    }

    public static class LockOptionsBuilder {
        private static final String DEFAULT_IDENTIFIER_PREFIX = "";
        private static final String DEFAULT_IDENTIFIER_SUFFIX = "";

        // 20 milliseconds by default
        private static final long DEFAULT_RETRY_INTERVAL_MILLISECONDS = 10L;
        // 1 second a lease by default
        private static final long DEFAULT_LEASE_INTERVAL_MILLISECONDS = 1000L;
        // default lease time is 5 seconds
        private static final long DEFAULT_LEASE_MILLISECONDS = 5000L;
        // default coordinator is redis(singleton)
        private static final Coordinator DEFAULT_COORDINATOR = Coordinator.REDIS_SINGLETON;

        private String identifierPrefix = DEFAULT_IDENTIFIER_PREFIX;

        private String identifierSuffix = DEFAULT_IDENTIFIER_SUFFIX;

        private long retryIntervalMilliseconds = DEFAULT_RETRY_INTERVAL_MILLISECONDS;

        private long leaseIntervalMilliseconds = DEFAULT_LEASE_INTERVAL_MILLISECONDS;

        private long leaseMilliseconds = DEFAULT_LEASE_MILLISECONDS;

        private Coordinator coordinator = DEFAULT_COORDINATOR;

        LockOptionsBuilder() {

        }

        public LockOptionsBuilder identifierPrefix(String identifierPrefix) {
            this.identifierPrefix = identifierPrefix;
            return this;
        }

        public LockOptionsBuilder identifierSuffix(String identifierSuffix) {
            this.identifierSuffix = identifierSuffix;
            return this;
        }

        public LockOptionsBuilder retryIntervalMilliseconds(long retryIntervalMilliseconds) {
            this.retryIntervalMilliseconds = retryIntervalMilliseconds;
            return this;
        }

        public LockOptionsBuilder identifierPrefix(long leaseIntervalMilliseconds) {
            this.leaseIntervalMilliseconds = leaseIntervalMilliseconds;
            return this;
        }

        public LockOptionsBuilder leaseMilliseconds(long leaseMilliseconds) {
            this.leaseMilliseconds = leaseMilliseconds;
            return this;
        }

        public LockOptionsBuilder coordinator(Coordinator coordinator) {
            this.coordinator = coordinator;
            return this;
        }

        public LockOptions build() {
            return new LockOptions(identifierPrefix, identifierSuffix, retryIntervalMilliseconds, leaseIntervalMilliseconds, leaseMilliseconds, coordinator);
        }
    }
}
