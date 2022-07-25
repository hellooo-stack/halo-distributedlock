package site.hellooo.distributedlock.impl;

import site.hellooo.distributedlock.LockCallback;
import site.hellooo.distributedlock.LockContext;
import site.hellooo.distributedlock.LockHandler;
import site.hellooo.distributedlock.LockState;
import site.hellooo.distributedlock.config.LockOptions;
import site.hellooo.distributedlock.enums.Coordinator;
import site.hellooo.distributedlock.enums.LockType;
import site.hellooo.distributedlock.exception.GenericRuntimeLockException;
import site.hellooo.distributedlock.exception.LockStateNotRemovedException;
import site.hellooo.distributedlock.exception.LockStateNotSetException;
import site.hellooo.distributedlock.impl.redis.LockCallbackFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class ReentrantDistributedLock extends AbstractDistributedLock {

    private LockContext lockContext;
    private LockOptions lockOptions;
    private String lockTarget;
    private LockHandler lockHandler;
    private LockCallback lockCallback;
    private AtomicInteger holdingCount = new AtomicInteger(0);

    private AtomicReference<Node> head = new AtomicReference<>();
    private AtomicReference<Node> tail = new AtomicReference<>();


    public ReentrantDistributedLock(LockOptions lockOptions, String lockTarget, LockHandler lockHandler) {
        this.lockOptions = lockOptions;
        this.lockTarget = lockTarget;
        this.lockHandler = lockHandler;

        lockContext = new LockContext() {
            @Override
            public LockOptions lockOptions() {
                return lockOptions;
            }

            @Override
            public AtomicReference<Thread> holdingThread() {
                return new AtomicReference<>(null);
            }

            @Override
            public AtomicReference<LockState<?>> holdingLockState() {
                return new AtomicReference<>(null);
            }

            @Override
            public LockHandler lockHandler() {
                return lockHandler;
            }

            @Override
            public LockCallback lockCallback() {
                if (lockCallback == null) {
                    lockCallback = LockCallbackFactory.of(lockOptions.getCoordinator(), this);
                }
                return lockCallback;
            }
        };
        lockCallback = LockCallbackFactory.of(lockOptions.getCoordinator(), lockContext);
    }

    private Node addWaiter() {

        Node currentNode = new Node(Thread.currentThread());

        Node prev = tail.get();
        if (prev != null) {
            if (tail.compareAndSet(prev, currentNode)) {
                prev.next.set(currentNode);
                currentNode.prev.set(prev);
                return currentNode;
            }
        }

        enqueue(currentNode);
        return currentNode;
    }

    //    enqueue node and return the prev node
    private Node enqueue(final Node node) {

        while (true) {
            Node prev = tail.get();
            if (prev == null) {
                Node newHead = new Node(null);
                newHead.next.set(node);
                node.prev.set(newHead);
                if (head.compareAndSet(null, newHead)) {
                    tail.set(node);
                    return newHead;
                }
            } else {
                if (tail.compareAndSet(prev, node)) {
                    prev.next.set(node);
                    node.prev.set(prev);
                    return prev;
                }
            }
        }
    }

    private void acquireQueued(final Node node) {

        while (true) {
            final Node prev = node.prev.get();
//            only if prev node is head, ant then we try to get lock
            if (prev == head.get() && tryLock()) {
//                if tryLock success, it means that the prev node has release the lock,
//                so we need to remove it from the queue
                head.set(node);
//                set to null, help with gc
                prev.next.set(null);
                break;
            }

            lockCallback.beforeParking(lockContext);

            LockSupport.park(this);
        }
    }

    private void unparkQueueHead() {
        Node headNode = head.get();
        if (headNode != null && headNode.next.get() != null) {
            Thread thread = headNode.next.get().thread;
            if (thread.isAlive()) {
                LockSupport.unpark(thread);
            }
        }
    }

    @Override
    public void lock() {
        if (!tryLock()) {
            acquireQueued(addWaiter());
        }
    }

    @Override
    public boolean tryLock() {
        if (Thread.currentThread() == lockContext.holdingThread().get()) {
            this.holdingCount.incrementAndGet();
            return true;
        }


        LockState<?> lockState = new LockStateBuilder(lockOptions)
                .identifier(lockTarget)
//                todo: need a value builder for difference state type
//                .value()
                .build();
        boolean locked = false;
        try {
            lockHandler.setState(lockState, lockContext);
            locked = true;
        } catch (LockStateNotSetException ignored) {

        }

        if (locked) {
            AtomicReference<Thread> holdingThread = lockContext.holdingThread();
            holdingThread.set(Thread.currentThread());
            this.holdingCount.set(1);

            lockCallback.afterLocked(lockContext);
        }

        return locked;
    }

    @Override
    public void unlock() {
        AtomicReference<Thread> holdThreadReference = lockContext.holdingThread();
        Thread holdingThread = holdThreadReference.get();
        if (holdingThread != null && Thread.currentThread() != holdingThread) {
            throw new GenericRuntimeLockException("Fatal: different thread between lock and unlock, PLEASE CHECK!!!");
        }

        if (this.holdingCount.decrementAndGet() > 0) {
            return;
        }

        AtomicReference<LockState<?>> lockStateReference = lockContext.holdingLockState();
        if (lockStateReference.get() == null) {
            throw new GenericRuntimeLockException("Fatal: context not holding a lockState, PLEASE CHECK!!!");
        }
        try {
            lockHandler.removeState(lockStateReference.get(), lockContext);
            lockCallback.afterUnlocked(lockContext);
        } catch (LockStateNotRemovedException ignored) {

        } finally {
            holdThreadReference.compareAndSet(holdingThread, null);
            unparkQueueHead();
        }
    }

    @Override
    public LockType lockType() {
        return LockType.REENTRANT;
    }

    @Override
    public Coordinator coordinatorType() {
        return lockHandler.coordinatorType();
    }

    static class Node {
        final AtomicReference<Node> prev = new AtomicReference<>();
        final AtomicReference<Node> next = new AtomicReference<>();
        final Thread thread;

        Node() {
            this(null);
        }

        Node(Thread thread) {
            this.thread = thread;
        }
    }
}
