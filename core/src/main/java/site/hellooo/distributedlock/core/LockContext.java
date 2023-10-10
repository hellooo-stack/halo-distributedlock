package site.hellooo.distributedlock.core;

import site.hellooo.distributedlock.core.config.LockOptions;

import java.util.concurrent.atomic.AtomicReference;

/**
 * context of the lock, holding the necessary message for handling the lock operation
 */
public interface LockContext {
    String target();
    // user options of the lock
    LockOptions options();

    // thread which is holding the lock in current process
    // return value should not be null
    AtomicReference<Thread> holdingThread();

    // lockState of the holding thread in current process
    AtomicReference<LockState<?>> holdingLockState();

    // operation handler for dealing with the coordinator
    LockHandler lockHandler();

    LockCallback lockCallback();

    DistributedLock currentLock();
}
