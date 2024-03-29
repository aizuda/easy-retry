package com.aizuda.easy.retry.server.common.allocate.client;

import com.aizuda.easy.retry.server.common.ClientLoadBalance;
import com.aizuda.easy.retry.server.common.allocate.client.ClientLoadBalanceManager.AllocationAlgorithmEnum;

import java.util.Random;
import java.util.TreeSet;

/**
 * @author: www.byteblogs.com
 * @date : 2022-03-11 22:00
 */
public class ClientLoadBalanceRandom implements ClientLoadBalance {

    private Random random = new Random();

    @Override
    public String route(String allocKey, TreeSet<String> clientAllAddressSet) {
        String[] addressArr = clientAllAddressSet.toArray(new String[clientAllAddressSet.size()]);
        return addressArr[random.nextInt(clientAllAddressSet.size())];
    }

    @Override
    public int routeType() {
        return AllocationAlgorithmEnum.RANDOM.getType();
    }
}
