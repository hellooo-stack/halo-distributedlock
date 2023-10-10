# Halo-DistributedLock

![Build](https://img.shields.io/github/actions/workflow/status/hellooo-stack/halo-distributedlock/maven.yml)
![Code Size](https://img.shields.io/github/languages/code-size/hellooo-stack/halo-distributedlock)
![Maven Central](https://img.shields.io/maven-central/v/site.hellooo/halo-distributedlock)
![GitHub license](https://img.shields.io/github/license/hellooo-stack/halo-distributedlock)

halo-distributedlock is a simple and reliable distributed lock implementation. 
It is designed to help you learn the principles of distributed locking 
and provides a lightweight solution for your production environment (assuming you are using single instance Redis). 
Whether you're a beginner or an experienced developer, It's worth to take a look at.

# Features
- Supports distributed lock, distributed unlock operations with a singleton Redis.
- Supports lock leasing, lock blocking and lock reentrant.

# Quick Start
Step one: Add maven dependency
```xml
<dependency>
    <groupId>site.hellooo</groupId>
    <artifactId>halo-distributedlock-core</artifactId>
    <version>>${halo-distributedlock.version}</version>
</dependency>
```

Step two: lock your resources with Lock.lock()
```java
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
        for (int i = 0; i < 500; i++) {
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
```

# Examples
You can find some examples in the [examples](https://github.com/hellooo-stack/halo-distributedlock/tree/master/examples) module. 
If you want to simulate multiple-process, multiple-thread competition, you should run multiprocess/Step0, 
and then run multiprocess/Step1 within a 5-second window. You will see the result like this:
```
# process1:
# process [99991] thread [locking_thread_0] is getting lock
# process [99991] thread [locking_thread_8] is getting lock
# process [99991] thread [locking_thread_7] is getting lock
# process [99991] thread [locking_thread_6] is getting lock
# process [99991] thread [locking_thread_2] is getting lock
# process [99991] thread [locking_thread_5] is getting lock
# process [99991] thread [locking_thread_4] is getting lock
# process [99991] thread [locking_thread_3] is getting lock
# process [99991] thread [locking_thread_9] is getting lock
# process [99991] thread [locking_thread_1] is getting lock
# process [99991] thread [locking_thread_8] got lock
# process [99991] thread [locking_thread_8] released lock
# process [99991] thread [locking_thread_7] got lock
# process [99991] thread [locking_thread_7] released lock
# process [99991] thread [locking_thread_3] got lock
# process [99991] thread [locking_thread_3] released lock
# process [99991] thread [locking_thread_0] got lock
# process [99991] thread [locking_thread_0] released lock
# process [99991] thread [locking_thread_9] got lock
# process [99991] thread [locking_thread_9] released lock
# process [99991] thread [locking_thread_2] got lock
# process [99991] thread [locking_thread_2] released lock
# ...


# process2:
# process [96469] thread [locking_thread_498] is getting lock
# process [96469] thread [locking_thread_499] is getting lock
# process [96469] thread [locking_thread_152] got lock
# process [96469] thread [locking_thread_152] released lock
# process [96469] thread [locking_thread_139] got lock
# process [96469] thread [locking_thread_139] released lock
# process [96469] thread [locking_thread_417] got lock
# process [96469] thread [locking_thread_417] released lock
# process [96469] thread [locking_thread_213] got lock
# process [96469] thread [locking_thread_213] released lock
# process [96469] thread [locking_thread_458] got lock
# process [96469] thread [locking_thread_458] released lock
# process [96469] thread [locking_thread_124] got lock
# process [96469] thread [locking_thread_124] released lock
# process [96469] thread [locking_thread_204] got lock
# process [96469] thread [locking_thread_204] released lock
# ...
```


