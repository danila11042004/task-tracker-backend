package danila.backendservice.util;

import danila.backendservice.dto.TaskResponseDto;
import danila.backendservice.entity.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    List<TaskResponseDto> toDtoList(List<Task> taskList);

    TaskResponseDto toDto(Task task);
}
