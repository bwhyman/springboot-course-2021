package org.example.redisexamples.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.redisexamples.component.ULID;
import org.example.redisexamples.dox.Item;
import org.example.redisexamples.dox.Order;
import org.redisson.api.*;
import org.redisson.api.stream.StreamAddArgs;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {
    private final RedissonClient redissonClient;
    private final ULID ulid;

    // 将指定商品批量加入redis抢购数据库
    public void addItems(List<Item> items) {
        // 获取操作管道的批处理对象
        RBatch batch = redissonClient.createBatch();
        for (Item item : items) {
            // 获取操作redis hash对象。以整数序列化
            RMapAsync<String, Integer> map =
                    batch.getMap(Item.PRE_KEY + item.getId(), IntegerCodec.INSTANCE);
            // 有则覆盖
            map.putAsync("total", item.getTotal());
        }
        batch.execute();
    }

    // 传入抢购的商品，抢购用户
    public long rushBuy(Item item, String userId) {
        var key = Item.PRE_KEY + item.getId();
        // 调用redis抢购函数
        long quantity = redissonClient
                .getFunction()
                .call(FunctionMode.WRITE, "rushBuy_0", FunctionResult.LONG, List.of(key));
        if (quantity == -1) {
            log.debug("抢光啦");
            return quantity;
        }
        // 抢购成功，则基于用户ID执行扣款等操作
        log.debug("{}，抢购成功", userId);
        // 创建订单
        Order order = Order.builder()
                .id(ulid.nextULID())
                .itemId(item.getId())
                .userId(userId)
                .build();
        // 默认基于Kryo5Codec序列化对象，非json可读。
        RBucket<Order> bucket = redissonClient.getBucket(Order.PRE_KEY + order.getId());
        // 持久化订单
        bucket.set(order);
        // 发送到订单处理消息队列，异步延迟处理订单，减轻服务器压力
        pushOrderQueue(order);
        return quantity;
    }

    private void pushOrderQueue(Order order) {
        // 消息ID类型；消息体类型
        RStream<String, String> stream = redissonClient.getStream(Order.STREAM_KEY, StringCodec.INSTANCE);
        // 仅需在消息体添加订单ID
        stream.add(StreamAddArgs.entry("orderid", order.getId()));

    }
}
