package danila.backendservice.kafka.outboxEvent;

import danila.backendservice.entity.OutboxEvent;
import danila.backendservice.kafka.message.EmailMessage;
import danila.backendservice.kafka.producer.EmailSenderProducer;
import danila.backendservice.repository.OutboxEventRepository;
import danila.backendservice.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "outbox-event-publisher.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class OutboxEventPublisher {
    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventService outboxEventService;
    private final EmailSenderProducer emailSenderProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 2000)
    public void publishEvents() {
        List<OutboxEvent> outboxEventsList = outboxEventRepository.findAllByOrderByCreatedAt();
        for (OutboxEvent outboxEvent : outboxEventsList) {
            try {
                EmailMessage message = objectMapper.readValue(outboxEvent.getPayload(), EmailMessage.class);
                emailSenderProducer.send(outboxEvent.getTopic(), message);
                outboxEventService.delete(outboxEvent);
            } catch (Exception e) {
                log.error("Failed publish event № {}", outboxEvent.getId(), e);
            }
        }
    }

}
