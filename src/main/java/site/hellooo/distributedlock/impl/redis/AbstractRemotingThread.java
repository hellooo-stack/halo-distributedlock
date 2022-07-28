package site.hellooo.distributedlock.impl.redis;

import site.hellooo.distributedlock.LockContext;

public abstract class AbstractRemotingThread extends Thread {

    final Object synchronizer = new Object();
    private boolean shutdown = false;

    protected final LockContext lockContext;

    public AbstractRemotingThread(LockContext lockContext) {
        setDaemon(true);
        this.lockContext = lockContext;
    }

    @Override
    public void run() {
        while (!shutdown) {
            synchronized (synchronizer) {
                try {
                    execute();
                    synchronizer.wait(getExecuteInterval());
                } catch (InterruptedException e) {
                    shutdown = true;
                }
            }
        }
    }

    protected abstract void execute() throws InterruptedException;

    protected abstract long getExecuteInterval();
}