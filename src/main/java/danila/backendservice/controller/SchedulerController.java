package danila.backendservice.controller;

import danila.backendservice.dto.UserResponseDto;
import danila.backendservice.service.TaskService;
import danila.backendservice.service.UserService;
import danila.backendservice.util.EndpointConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SchedulerController {
    private final UserService userService;
    private final TaskService taskService;

    @GetMapping(EndpointConstants.USERS_WITH_TASKS_ENDPOINT)
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getUserWithTasksList() {
        return userService.getUserWithTasksList();
    }

    @DeleteMapping(EndpointConstants.COMPLETED_TASKS_ENDPOINT)
    @ResponseStatus(HttpStatus.OK)
    public void deleteSelectedCompletedTasks(@RequestBody List<Long> userTaskIdList) {
        taskService.deleteSelectedCompletedTasks(userTaskIdList);
    }
}
