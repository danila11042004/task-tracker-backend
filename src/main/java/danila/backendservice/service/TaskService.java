package danila.backendservice.service;

import danila.backendservice.dto.TaskCreateRequestDto;
import danila.backendservice.dto.TaskResponseDto;
import danila.backendservice.dto.TaskUpdateRequestDto;
import danila.backendservice.entity.Task;
import danila.backendservice.entity.User;
import danila.backendservice.enums.TaskStatus;
import danila.backendservice.exception.TaskNotFoundException;
import danila.backendservice.repository.TaskRepository;
import danila.backendservice.repository.UserRepository;
import danila.backendservice.util.TaskMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private static final String TASK_NOT_FOUND_BY_ID_AND_USER_ID = "Task with this id not found for the current user";
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public List<TaskResponseDto> getTasksUser(Long userId) {
        return taskMapper.toDtoList(taskRepository.getAllByUserId(userId));
    }

    @Transactional
    public TaskResponseDto createTaskUser(Long userId, TaskCreateRequestDto requestDto) {
        User user = userRepository.getReferenceById(userId);
        Task task = new Task(requestDto.headline(), requestDto.textContent(), user);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Transactional
    public void deleteTaskUser(Long userId, Long taskId) {
        int numberOfDeleted = taskRepository.deleteByIdAndUserId(taskId, userId);
        if (numberOfDeleted == 0) {
            throw new TaskNotFoundException(TASK_NOT_FOUND_BY_ID_AND_USER_ID);
        }
    }

    @Transactional
    public TaskResponseDto updateTaskUser(Long userId, Long taskId, TaskUpdateRequestDto requestDto) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId).orElseThrow(() ->
                new TaskNotFoundException(TASK_NOT_FOUND_BY_ID_AND_USER_ID));
        task.update(requestDto.headline(), requestDto.textContent(), requestDto.status());
        return taskMapper.toDto(task);
    }

    @Transactional
    public void deleteSelectedCompletedTasks(List<Long> taskIdList) {
        for (Long taskId : taskIdList) {
            taskRepository.deleteByStatusAndId(TaskStatus.COMPLETED, taskId);
        }
    }
}
