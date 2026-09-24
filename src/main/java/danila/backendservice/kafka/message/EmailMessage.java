package danila.backendservice.kafka.message;

import java.util.UUID;

public record EmailMessage(
        UUID uuid,
        String email,
        String headline,
        String textContent) {
}
