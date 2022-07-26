package site.hellooo.distributedlock.impl.redis;

import site.hellooo.distributedlock.LockContext;

public class RemotingLeaseThread extends Thread {

    final Object synchronizer = new Object();
    private final LockContext lockContext;
    private boolean shutdown = false;

    public RemotingLeaseThread(LockContext lockContext) {
        setDaemon(true);
        this.lockContext = lockContext;
    }

    @Override
    public void run() {
        while (!shutdown) {
            synchronized (synchronizer) {
                try {
                    doLease();
                    synchronizer.wait(lockContext.lockOptions().getLeaseIntervalMilliseconds());
                } catch (InterruptedException e) {
                    shutdown = true;
                }
            }
        }
    }

    private void doLease() throws InterruptedException {
        System.out.println("i am leasing...");
    }
}
