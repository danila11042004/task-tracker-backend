package danila.backendservice.dto;

import danila.backendservice.enums.TaskStatus;
import jakarta.validation.constraints.Pattern;

public record TaskUpdateRequestDto(
        @Pattern(regexp = ".*\\S+.*", message = "Task headline not be empty")
        String headline,
        String textContent,
        TaskStatus status
) {
}
