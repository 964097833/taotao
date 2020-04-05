package com.taotao.rest.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;

public class JedisTest {

    @Test
    public void jedisTestSingle() {
        //创建一个jedis对象。
        Jedis jedis = new Jedis("192.168.220.128", 6379);
        //调用jedis对象的方法，方法名称和redis的命令一致
        jedis.set("key1", "jedis test");
        System.out.println(jedis.get("key1"));
        //关闭jedis。
        jedis.close();
    }

    /**
     * 使用连接池
     */
    @Test
    public void testJedisPool() {
        //创建jedis连接池
        JedisPool pool = new JedisPool("192.168.220.128", 6379);
        //从连接池中获得jedis对象
        Jedis jedis = pool.getResource();
        String s = jedis.get("key1");
        System.out.println(s);
        //关闭jedis
        jedis.close();
        pool.close();
    }

    /**
     * 集群测试
     */
    @Test
    public void testJedisClouster() {
        HashSet<HostAndPort> node = new HashSet<>();
        node.add(new HostAndPort("192.168.220.128", 7001));
        node.add(new HostAndPort("192.168.220.128", 7002));
        node.add(new HostAndPort("192.168.220.128", 7003));
        node.add(new HostAndPort("192.168.220.128", 7004));
        node.add(new HostAndPort("192.168.220.128", 7005));
        node.add(new HostAndPort("192.168.220.128", 7006));

        JedisCluster cluster = new JedisCluster(node);

        cluster.set("key1", "cluster test");
        String key1 = cluster.get("key1");
        System.out.println(key1);

        cluster.close();
    }

    /**
     * 单机版测试
     */
    @Test
    public void testSpringJedisSingle() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        JedisPool pool = (JedisPool) applicationContext.getBean("redisClient");
        Jedis jedis = pool.getResource();
        String key1 = jedis.get("key1");
        System.out.println(key1);
        jedis.close();
        pool.close();
    }

    /**
     * 集群版测试
     */
    @Test
    public void testSpringJedisCluster() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        JedisCluster cluster = (JedisCluster) applicationContext.getBean("redisClient");
        String key1 = cluster.get("key1");
        System.out.println(key1);
        cluster.close();
    }
}
