package site.hellooo.distributedlock.impl;

import org.junit.Test;

class ReentrantDistributedLockTest {
    @Test
    void testConstructor() {
//        case1: after constructor method called:
//          - the result of constructed object should not be null
//          - object field: lockOptions, lockTarget, lockHandler should be set to the argument ref
//          - object field: lockContext, lockCallback should not be null
//        case2: after case 1 executed, field of object.lockContext:
//          - .lockTarget(object.lockContext.lockTarget): should be equal to object.lockTarget
//          - .lockOptions(object.lockContext.lockOptions): should be equal to object.lockOptions
//          - .holdingThread(object.lockContext.holdingThread): should not be null, and has an empty value
//          - .holdingLockState(object.lockContext.holdingLockState): should not be null, and has an empty value
//          - .lockHandler(object.lockContext.lockHandler): should be equal to object.lockHandler
//          - .lockCallback(object.lockContext.lockCallback): should be equal to object.lockCallback
    }

    @Test
    void testAddWaiter() {

    }

    @Test
    void testEnqueue() {

    }

    @Test
    void testAcquireQueued() {

    }

    @Test
    void testUnparkQueueHead() {

    }

    @Test
    void testLock() {

    }

    @Test
    void testTryLock() {

    }

    @Test
    void testUnLock() {

    }

}
