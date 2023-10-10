package site.hellooo.distributedlock.core.impl;

import site.hellooo.distributedlock.core.config.LockOptions;
import site.hellooo.distributedlock.core.*;
import site.hellooo.distributedlock.core.enums.Coordinator;
import site.hellooo.distributedlock.core.enums.LockType;
import site.hellooo.distributedlock.core.exception.GenericRuntimeLockException;
import site.hellooo.distributedlock.core.exception.LockStateNotRemovedException;
import site.hellooo.distributedlock.core.exception.LockStateNotSetException;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class ReentrantDistributedLock extends AbstractDistributedLock {

    private final LockContext lockContext;
    private final LockOptions lockOptions;
    private final String lockTarget;
    private final LockHandler lockHandler;
    private final LockCallback lockCallback;
    private final AtomicInteger holdingCount = new AtomicInteger(0);
    private final AtomicReference<Node> head = new AtomicReference<>();
    private final AtomicReference<Node> tail = new AtomicReference<>();


    public ReentrantDistributedLock(LockOptions lockOptions, String lockTarget, LockHandler lockHandler) {
        this.lockOptions = lockOptions;
        this.lockTarget = lockTarget;
        this.lockHandler = lockHandler;

        lockContext = new LockContext() {
            private final AtomicReference<Thread> holdingThread = new AtomicReference<>();
            private final AtomicReference<LockState<?>> holdingLockState = new AtomicReference<>();

            @Override
            public String target() {
                return lockTarget;
            }

            @Override
            public LockOptions options() {
                return lockOptions;
            }

            @Override
            public AtomicReference<Thread> holdingThread() {
                return holdingThread;
            }

            @Override
            public AtomicReference<LockState<?>> holdingLockState() {
                return holdingLockState;
            }

            @Override
            public LockHandler lockHandler() {
                return lockHandler;
            }

            @Override
            public LockCallback lockCallback() {
                return lockCallback;
            }

            @Override
            public DistributedLock currentLock() {
                return ReentrantDistributedLock.this;
            }
        };
        lockCallback = LockCallbackFactory.of(lockOptions.getCoordinator(), lockContext);
    }

    private Node addWaiter() {

        final Node currentNode = new Node(Thread.currentThread());

        final Node prev = tail.get();
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

    // enqueue node and return the prev node
    private Node enqueue(final Node node) {

        while (true) {
            final Node prev = tail.get();
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
            // only if prev node is head, and then we try to get the lock
            if (prev == head.get() && tryLock()) {
                // if tryLock success, it means that the prev node has release the lock,
                // so we need to remove it from the queue
                head.set(node);
                // set to null, help with gc
                prev.next.set(null);
                break;
            }

            lockCallback.beforeParking(lockContext);

            LockSupport.park(this);
        }
    }

    public void unparkQueueHead() {
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
                .build();
        boolean locked = false;
        try {
            lockHandler.setState(lockState, lockContext);
            locked = true;
        } catch (LockStateNotSetException ignored) {

        }

        if (locked) {
            Thread holdingThread = lockContext.holdingThread().get();
            lockContext.holdingThread().compareAndSet(holdingThread, Thread.currentThread());

            LockState<?> holdingLockState = lockContext.holdingLockState().get();
            lockContext.holdingLockState().compareAndSet(holdingLockState, lockState);

            this.holdingCount.set(1);

            lockCallback.afterLocked(lockContext);
        }

        return locked;
    }

    @Override
    public void unlock() {

        AtomicReference<Thread> holdingThreadReference = lockContext.holdingThread();
        Thread holdingThread = holdingThreadReference.get();
        if (holdingThread == null || holdingThread != Thread.currentThread()) {
            String causeMessage = holdingThread == null ?
                    "Fatal: holdingThread is null, when does lock released? PLEASE CHECK!!!"
                    :
                    "Fatal: different thread between lock and unlock, PLEASE CHECK!!!";
            throw new GenericRuntimeLockException(causeMessage);
        }

        if (this.holdingCount.decrementAndGet() > 0) {
            return;
        }

        AtomicReference<LockState<?>> holdingLockStateReference = lockContext.holdingLockState();
        LockState<?> holdingLockState = holdingLockStateReference.get();
        if (holdingLockState == null) {
            throw new GenericRuntimeLockException("Fatal: holdingLockState is null, when does it removed? PLEASE CHECK!!!");
        }
        try {
            lockHandler.removeState(holdingLockState, lockContext);
        } catch (LockStateNotRemovedException ignored) {
        } finally {
            lockCallback.afterUnlocked(lockContext);

            holdingThreadReference.compareAndSet(holdingThread, null);
            holdingLockStateReference.compareAndSet(holdingLockState, null);
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

    private static class Node {
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
