package site.hellooo.distributedlock.examples.multiprocess;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import site.hellooo.distributedlock.examples.ConfigReader;
import site.hellooo.distributedlock.examples.SingleProcessMultiThreadContention;

public class Step1 {
    public static void main(String[] args) {
        ConfigReader.RedisConfig redisConfig = ConfigReader.redis();
        String host = redisConfig.getHost();
        int port = redisConfig.getPort();

        long start = System.currentTimeMillis();
        JedisPool jedisPool = new JedisPool(host, port);
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set("multiprocess_step1_ready", "true");

            boolean step0_ready = false;
            while (System.currentTimeMillis() - start < 5000) {
                String multiprocessValue = jedis.get("multiprocess_step0_ready");
                if ("true".equals(multiprocessValue)) {
                    step0_ready = true;
                    break;
                }
                Thread.sleep(2);
            }

            if (!step0_ready) {
                return;
            }

            SingleProcessMultiThreadContention.lockCompetition();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            jedis.del("multiprocess_step1_ready");
            jedis.close();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            jedisPool.close();
            System.out.println("cause: " + (System.currentTimeMillis() - start) + "ms");
        }));
    }
}
