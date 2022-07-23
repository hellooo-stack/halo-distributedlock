package site.hellooo.distributedlock;

public interface LockCallback {
//    execute immediately after the lock granted to current thread
    void afterLocked(LockContext lockContext);

//    execute immediately after the current thread queued, or say: after added to the 'tail.next'
    void afterQueued(LockContext lockContext);

//    execute before parking the current thread
    void beforeParking(LockContext lockContext);

//    execute immediately after the lock released
    void afterUnlocked(LockContext lockContext);
}
