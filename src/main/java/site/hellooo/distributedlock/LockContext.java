package site.hellooo.distributedlock;

import site.hellooo.distributedlock.config.LockOptions;

import java.util.concurrent.atomic.AtomicReference;

/**
 * context of the lock, holding the necessary message for handling the lock operation
 */
public interface LockContext {
//    the user config of this lock
    LockOptions lockOptions();

//    thread which is holding the lock in current process
//    return value should not be null
//    todo add annotation
    AtomicReference<Thread> holdingThread();

//    lockState of the holding thread in current process
    AtomicReference<LockState<?>> holdingLockState();

//    operation handler for dealing with the coordinator
    LockHandler lockHandler();

    LockCallback lockCallback();
}
