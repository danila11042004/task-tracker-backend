package danila.backendservice.controller;

import danila.backendservice.dto.TaskCreateRequestDto;
import danila.backendservice.dto.TaskResponseDto;
import danila.backendservice.dto.TaskUpdateRequestDto;
import danila.backendservice.security.UserPrincipal;
import danila.backendservice.service.TaskService;
import danila.backendservice.util.EndpointConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CurrentUserTaskController {

    private final TaskService taskService;

    @GetMapping(EndpointConstants.CURRENT_USER_TASKS_ENDPOINT)
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponseDto> getTaskCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return taskService.getTasksUser(userPrincipal.getId());
    }

    @PostMapping(EndpointConstants.CURRENT_USER_TASKS_ENDPOINT)
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDto createTaskCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 @Valid @RequestBody TaskCreateRequestDto requestDto) {
        return taskService.createTaskUser(userPrincipal.getId(), requestDto);
    }

    @DeleteMapping(EndpointConstants.CURRENT_USER_TASK_ENDPOINT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long taskId) {
        taskService.deleteTaskUser(userPrincipal.getId(), taskId);
    }

    @PatchMapping(EndpointConstants.CURRENT_USER_TASK_ENDPOINT)
    @ResponseStatus(HttpStatus.OK)
    public TaskResponseDto updateTaskCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 @PathVariable Long taskId,
                                                 @Valid @RequestBody TaskUpdateRequestDto requestDto) {
        return taskService.updateTaskUser(userPrincipal.getId(), taskId, requestDto);
    }
}
