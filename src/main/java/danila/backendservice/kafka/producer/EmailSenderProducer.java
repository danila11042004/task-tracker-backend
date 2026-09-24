package danila.backendservice.kafka.producer;

import danila.backendservice.kafka.message.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSenderProducer {
    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    public void send(String topic, EmailMessage message) {
        try {
            kafkaTemplate.send(topic, message).get();
        } catch (Exception e) {
            log.error("Failed to send registration message №{}", message.uuid(), e);
            throw new KafkaException("Failed to send registration message", e);
        }
    }
}
