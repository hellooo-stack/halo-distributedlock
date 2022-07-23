package site.hellooo.distributedlock;

import site.hellooo.distributedlock.impl.ReentrantDistributedLock;
import site.hellooo.distributedlock.impl.ReentrantDistributedLockBuilder;

public class Main {
    public static void main(String[] args) {
//        DistributedLock lock = ReentrantDistributedLockBuilder
//        .lockHandler(LockHandler lockHandler)
//        .lockCallback(LockCallback lockCallback
//        .build();
//
        DistributedLock lock = new ReentrantDistributedLockBuilder()
                .build();
        lock.lock();
        lock.unlock();
    }
}
