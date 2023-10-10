package site.hellooo.distributedlock.core;

public interface LockCallback {
    // execute immediately after the lock granted to current thread
    void afterLocked(LockContext lockContext);

    // execute before parking the current thread
    void beforeParking(LockContext lockContext);

    // execute immediately after the lock released
    void afterUnlocked(LockContext lockContext);
}
