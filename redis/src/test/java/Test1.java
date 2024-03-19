import org.junit.Test;
import redis.clients.jedis.JedisPooled;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:Test1
 * @author:pxf
 * @data:2023/09/04
 **/
public class Test1 {
    private JedisPooled jedis = new JedisPooled("192.168.149.131",6380);
    /**
     * String 类型
     */
    @Test
    public void testString(){
        jedis.mset("name","pangxiaofu","age","25");
        System.out.println(jedis.get("name")+jedis.get("age"));
        jedis.close();
    }
    /**
     * hash 类型
     */
    @Test
    public void testHash(){
      Map m =  new HashMap<>();
      m.put("type","2");
      m.put("typeName","phone");
      m.put("system","ios");
      m.put("core","4");
      jedis.hset("iphone12",m);
      System.out.println(jedis.hgetAll("iphone12"));
      System.out.println(jedis.hmget("iphone12","type","typeName","system"));
      jedis.close();
    }
    /**
     * list 类型
     */
    @Test
    public void testList(){
        jedis.rpush("fruit","banana", "apple", "watermelon", "orange");
        System.out.println(jedis.lrange("fruit",0,-1));
        jedis.close();
    }

    /**
     * set 类型
     */
    @Test
    public void testSet(){
        jedis.sadd("car","bmw", "tesla", "byd", "ferrari");
        System.out.println(jedis.smembers("car"));
        jedis.close();
    }

    /**
     * zset 类型
     */
    @Test
    public void testZSet(){
        jedis.zadd("week",30,"Monthday");
        jedis.zadd("week",40,"Tuesday");
        jedis.zadd("week",55,"Wednesday");
        jedis.zadd("week",60,"Thursday");
        jedis.zadd("week",80,"Friday");
        jedis.zadd("week",90,"Saturday");
        jedis.zadd("week",20,"Sunday");
        System.out.println(jedis.zrevrangeWithScores("week",0,2));
        System.out.println(jedis.zrangeWithScores("week",0,5));
        jedis.close();
    }

    /**
     * zset 类型
     */
    @Test
    public void testZSet2(){
        String deptNum = "13011000060135";
        String orgNum  = deptNum.substring(0,12);
        System.out.println(orgNum);
    }




}
