package tax;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPooled;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description:LockAboutRedis
 * @author:pxf
 * @data:2023/10/19
 **/
@Service
public class LockAboutRedis {
    private JedisPooled jedis = new JedisPooled("192.168.149.131", 6380);
    @Autowired
    private StringRedisTemplate rt;
    @Autowired
    private Redisson redisson;
    String key_resource_id = "key_resource_id";
    String lock_value = "lock_value";
    int i = 0;
    public void distributeLock1() {
        synchronized (this) {
           /*long s = jedis.setnx("llk", "一个lock");
           if (s == 1L) {
               String cnt = jedis.get("cnt");
               Integer cntNum = Integer.valueOf(cnt);
               System.out.println("库存" + cnt);
               jedis.set("cnt", String.valueOf(--cntNum));
               jedis.del("llk");
               break;
           }*/
            String cnt = rt.opsForValue().get("cnt");
            Integer cntNum = Integer.valueOf(cnt);
            if (cntNum > 0) {
                System.out.println("库存" + cnt);
                rt.opsForValue().set("cnt", String.valueOf(--cntNum));
            }
        }
    }

    public String distributeLock2() {
        String  uuid = UUID.randomUUID().toString();
        try {
            //添加锁的同时设置过期时间，该操作具有原子性
            Boolean aBoolean = rt.opsForValue().setIfAbsent("llk", uuid,
                    5, TimeUnit.SECONDS);
            if (!aBoolean) {
                return "没有抢到锁";
            }
            String cnt = rt.opsForValue().get("cnt");
            int amount = cnt == null ? 0 : Integer.parseInt(cnt);
            if (amount > 0) {
                System.out.println("库存" + cnt);
                rt.opsForValue().set("cnt", String.valueOf(--amount));
                return "库存" + cnt;
            }
        }finally {
            JedisPool jedisPool = new JedisPool("192.168.149.131",6380);
            try(Jedis jedis1 = jedisPool.getResource()) {
                String script = " if redis.call ('get',kEYS[1]==avg[1] "+
                        "then return redis.call ('del',kEYS[1] ) "+
                        "end " +
                         "return  0 ";
                Object eval = jedis.eval(script, Collections.singletonList("llk"),Collections.singletonList(uuid));
                if ("1".equals(eval.toString())){
                    System.out.println("释放成功");
                }else {
                    System.out.println("释放锁时发生异常");
                }
            }

        }
        return " 没抢到";
    }

    public String distributeLock() {
        RLock rlock = redisson.getLock("ddl");
        try {
            //添加分布式锁
            Boolean aBoolean = rlock.tryLock(20,5,TimeUnit.SECONDS);
            if (!aBoolean) {
                return "没有抢到锁";
            }
            String cnt = rt.opsForValue().get("cnt");
            int amount = cnt == null ? 0 : Integer.parseInt(cnt);
            if (amount > 0) {
                System.out.println("库存" + cnt);
                rt.opsForValue().set("cnt", String.valueOf(--amount));
                return "库存" + cnt;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (rlock.isLocked() && rlock.isHeldByCurrentThread()) {
                rlock.unlock();
            }
        }
        return " 没抢到";
    }

}
