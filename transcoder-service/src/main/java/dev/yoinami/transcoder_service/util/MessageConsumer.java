package dev.yoinami.transcoder_service.util;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
        // Process your message here
    }
}
