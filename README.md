# learning-rabbitmq

## AMQP 0-9-1 Model

当消息发送到消费者时，消费者会自动通知broker或应用程序开发人员选择立即通知broker。使用消息确认时，代理只有在收到该消息通知时（或消息组），才会从队列中移除消息。 在某些情况下，例如，消息不能路由时，消息可能会返回给发布者，或者被丢弃，或如果代理实现了扩展，会将消息放置在"死信队列"中。发布者通过使用某些参数来选择如何处理这种情况。队列的交换和绑定统称为AMQP实体。

## Exchange and exchange type

|Exchange type|Default pre-declared names|
|----|----|
|Direct exchange|(Empty string) and amq.direct|
|Fanout exchange|amq.fanout|
|Topic exchange|amq.topic|
|Headers exchange|amq.match(and amq.headers in RabbitMQ)|

Exchange attributes:Name,Durability,Auto-delete,Argument

Direct exchange often used to distribute tasks between multiple workers in a round robbin manner.

Fanout exchanges are ideal for the broadcast routing of messages.

The topic exchange type is often used to implement various publish/subscribe pattern variations. Topic exchanges are commonly used for the multicast routing of messages.

A headers exchange is designed for routing on multiple attributes that are more easily expressed as message headers than a routing key.

## Queue

Exchange绑定在队列上，如果多个应用程序需要连接到broker，建立多个connection会消耗系统资源，并且对于防火墙配置不友好。Channel采用多路复用机制，共享一个TCP连接。 