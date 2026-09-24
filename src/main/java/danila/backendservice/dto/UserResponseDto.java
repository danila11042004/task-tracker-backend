package danila.backendservice.dto;

import java.util.List;

public record UserResponseDto(
        Long id,
        String email,
        List<TaskResponseDto> taskList) {
}
