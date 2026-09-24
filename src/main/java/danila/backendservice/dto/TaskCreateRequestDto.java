package danila.backendservice.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskCreateRequestDto(
        @NotBlank(message = "Headline is required")
        String headline,
        String textContent) {
}
