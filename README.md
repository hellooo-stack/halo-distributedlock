# DistributedLock

![Build](https://img.shields.io/github/actions/workflow/status/hellooo-stack/hellooo-distributedlock/maven.yml)
![Code Size](https://img.shields.io/github/languages/code-size/hellooo-stack/hellooo-distributedlock)
![Maven Central](https://img.shields.io/maven-central/v/site.hellooo/hellooo-distributedlock)
![GitHub license](https://img.shields.io/github/license/hellooo-stack/hellooo-distributedlock)

DistributedLock is a lightweight distributed lock framework that provides reliable consistency features. It can be used with only the Lock interface.


# Features
- Reentrant distributed locking
- Supports tryLock(), lock(), unlock() operations
- Supports lock leasing

# Quick Start
Step one: Add maven dependency
```xml
<dependency>
    <groupId>site.hellooo</groupId>
    <artifactId>hellooo-distributedlock</artifactId>
    <version>>${hellooo-distributedlock.version}</version>
</dependency>
```

Step two: lock your resources with Lock.lock()
```java
public class Main {
    public static void main(String[] args) {
        LockOptions lockOptions = LockOptions.options()
                .build();

//        define the redis source
        JedisPool pool = new JedisPool("localhost", 6379);
        for (int i = 0; i < 10; i++) {
            final int threadNumber = i;
            Thread thread = new Thread(() -> {
                Thread.currentThread().setName("Thread " + threadNumber);

                try (Jedis jedis = pool.getResource()) {
                    try {
                        Lock lock = new ReentrantDistributedLock(lockOptions, "my_lock", new RedisLockHandler(jedis));
//                        lock 
                        lock.lock();
                        System.out.println("thread" + Thread.currentThread().getName() + " locked!");
                        Thread.sleep(1000);
                        System.out.println("thread" + Thread.currentThread().getName() + " lock released!");
//                        unlock
                        lock.unlock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }
}

```
