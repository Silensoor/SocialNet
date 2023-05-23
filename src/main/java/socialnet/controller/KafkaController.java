package socialnet.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.kafka.annotation.KafkaListener;
import socialnet.api.request.KafkaMessageRq;

import java.util.concurrent.CompletableFuture;

@RequestMapping("/api/v1/kafka")
@RestController
@RequiredArgsConstructor
@Slf4j
public class KafkaController {
    private final KafkaTemplate<String, KafkaMessageRq> kafkaTemplate;

    @PostMapping
    public void sendMessage(@RequestBody KafkaMessageRq message) {
        kafkaTemplate.send("transaction-1", message);
    }

    /*@KafkaListener(topics = "transaction-1", groupId = "group-1")
    public void listener(@Payload KafkaMessageRq message, ConsumerRecord<String, KafkaMessageRq> cr) {
        log.info("Topic [transaction-1] Received message: {}", message.getData());
        log.info(cr.toString());
    }*/

    @KafkaListener(
        topics = "transaction-1",
        containerFactory = "kafkaListenerContainerFactory")
    public void messageListener(KafkaMessageRq message) {
        log.info("Topic [transaction-1] Received message: {}", message.getData());
    }
}
