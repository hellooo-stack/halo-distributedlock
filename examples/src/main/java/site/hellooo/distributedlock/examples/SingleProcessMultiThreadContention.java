package site.hellooo.distributedlock.examples;

import redis.clients.jedis.JedisPool;
import site.hellooo.distributedlock.core.common.ProcessUtils;
import site.hellooo.distributedlock.core.config.LockOptions;
import site.hellooo.distributedlock.core.impl.ReentrantDistributedLockBuilder;

import java.util.concurrent.locks.Lock;

public class SingleProcessMultiThreadContention {
    public static void main(String[] args) {
        lockCompetition();
    }

    public static void lockCompetition() {
        long start = System.currentTimeMillis();

        ConfigReader.RedisConfig redisConfig = ConfigReader.redis();
        String host = redisConfig.getHost();
        int port = redisConfig.getPort();

        JedisPool jedisPool = new JedisPool(host, port);
        for (int i = 0; i < 10; i++) {
            final int threadNumber = i;
            Thread thread = new Thread(() -> {
                Thread.currentThread().setName("locking_thread_" + threadNumber);

                Lock lock = new ReentrantDistributedLockBuilder()
                        .lockOptions(LockOptions.ofDefault())
                        .jedisPool(jedisPool)
                        .lockTarget("my_lock")
                        .build();

                System.out.println("process [" + ProcessUtils.getProcessId() + "] thread [" + Thread.currentThread().getName() + "] is getting lock");
                lock.lock();
                System.out.println("process [" + ProcessUtils.getProcessId() + "] thread [" + Thread.currentThread().getName() + "] got lock");
                lock.unlock();
                System.out.println("process [" + ProcessUtils.getProcessId() + "] thread [" + Thread.currentThread().getName() + "] released lock");
            });
            thread.start();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            jedisPool.close();
            System.out.println("cause: " + (System.currentTimeMillis() - start) + "ms");
        }));
    }
}
