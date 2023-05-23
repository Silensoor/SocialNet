package socialnet.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import socialnet.api.request.KafkaMessageRq;

@RequestMapping("/api/v1/kafka")
@RestController
@RequiredArgsConstructor
@Slf4j
public class KafkaController {
    private final KafkaTemplate<String, KafkaMessageRq> kafkaTemplate;

    @Value(value = "${spring.kafka.consumer.topic-name}")
    private String topicName;

    @PostMapping
    public void sendMessage(@RequestBody KafkaMessageRq message) {
        kafkaTemplate.send(topicName, message);
    }

    @KafkaListener(
        topics = "${spring.kafka.consumer.topic-name}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory")
    public void messageListener(KafkaMessageRq message) {
        log.info("Topic [${spring.kafka.consumer.topic-name}] Received message: {}", message.getData());
    }
}
