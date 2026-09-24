package danila.backendservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import danila.backendservice.enums.TaskStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskResponseDto(
        Long id,
        String headline,
        String textContent,
        TaskStatus status,
        LocalDateTime completedAt) {
}
