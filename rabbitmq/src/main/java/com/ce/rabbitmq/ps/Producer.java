package com.ce.rabbitmq.ps;

import com.ce.rabbitmq.util.ConnectionUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 发布与订阅模式：发送消息
 */
public class Producer {
    /**
     * 交换机名称
     */
    static final String FANOUT_EXCHANGE = "fanout_exchange";
    /**
     * 队列名称
     */
    static final String FANOUT_QUEUE_1 = "fanout_queue_1";
    static final String FANOUT_QUEUE_2 = "fanout_queue_2";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 1.  创建连接
        Connection connection = ConnectionUtil.getConnection();
        // 2.  创建频道
        Channel channel = connection.createChannel();
        // 3.  声明交换机  参数1：交换机名称  参数2：交换机类型 （fanout direct topic）
        channel.exchangeDeclare(FANOUT_EXCHANGE, BuiltinExchangeType.FANOUT);
        // 4.  声明队列
        /*
         *  参数1： 队列名称
         *  参数2：是否定义持久化队列（消息会持久化保存在服务器上）
         *  参数3：是否独占本连接
         *  参数4：是否在不使用的时候队列自动删除
         *  参数5：其他参数
         */
        channel.queueDeclare(FANOUT_QUEUE_1, true, false, false, null);
        channel.queueDeclare(FANOUT_QUEUE_2, true, false, false, null);

        // 5.队列绑定到交换机
        // 参数1：对列名称   参数2：交换机名称 参数3：路由key
        channel.queueBind(FANOUT_QUEUE_1, FANOUT_EXCHANGE, "");
        channel.queueBind(FANOUT_QUEUE_2, FANOUT_EXCHANGE, "");

        // 6.发送消息
        for (int i = 0; i < 10; i++) {

            String message = "你好，RabbitMQ！发布订阅模式_" + i;

            /*
             * 参数1：交换机名称，如果没有则指定空字符串，表示使用默认的交换机
             * 参数2：路由key，简单模式中可以使用队列名称
             * 参数3：消息其他属性
             * 参数4：消息内容
             */
            channel.basicPublish(FANOUT_EXCHANGE, "", null, message.getBytes());
            System.out.println("已发送消息：" + message);
        }
        // 6.  关闭资源
        channel.close();
        connection.close();
    }
}
