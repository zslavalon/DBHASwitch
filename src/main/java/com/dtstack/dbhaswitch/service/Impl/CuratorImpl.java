package com.dtstack.dbhaswitch.service.Impl;

import com.dtstack.dbhaswitch.service.CuratorService;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;

import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;
import org.junit.Test;

/**
 * Author:Demon
 * Date:2019-09-04
 */
public class CuratorImpl implements CuratorService {

    //会话超时时间
    private final int SESSION_TIMEOUT = 30 * 1000;

    //连接超时时间
    private final int CONNECTION_TIMEOUT = 3 * 1000;

    //ZooKeeper服务地址
    private static final String SERVER = "192.168.1.159:2100,192.168.1.159:2101,192.168.1.159:2102";

    //创建连接实例
    private CuratorFramework client = null;

    @Override
    public void testCurator(){

    }

    /**
     * baseSleepTimeMs：初始的重试等待时间
     * maxRetries：最多重试次数
     */
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    @Before
    public void init(){
        //创建 CuratorFrameworkImpl实例
        client = CuratorFrameworkFactory.newClient(SERVER, SESSION_TIMEOUT, CONNECTION_TIMEOUT, retryPolicy);

        //启动
        client.start();
    }
}
