package danila.backendservice.service;

import danila.backendservice.entity.OutboxEvent;
import danila.backendservice.exception.OutboxEventSerializationException;
import danila.backendservice.kafka.message.EmailMessage;
import danila.backendservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OutboxEventService {
    private static final String JSON_SERIALIZATION_ERROR_MESSAGE = "Failed to serialize outbox message";
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;


    public void delete(OutboxEvent outboxEvent) {
        outboxEventRepository.delete(outboxEvent);
    }

    public void create(String topic, EmailMessage message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            outboxEventRepository.save(new OutboxEvent(topic, payload));
        } catch (JacksonException e) {
            throw new OutboxEventSerializationException(JSON_SERIALIZATION_ERROR_MESSAGE, e);
        }
    }
}
