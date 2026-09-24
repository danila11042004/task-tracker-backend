package danila.backendservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EndpointConstants {
    public final String CURRENT_USER_ENDPOINT = "/user";
    public final String LOGIN_ENDPOINT = "/auth/login";
    public final String CURRENT_USER_TASKS_ENDPOINT = "/tasks";
    public final String CURRENT_USER_TASK_ENDPOINT = "/tasks/{taskId}";
    public final String SCHEDULER_INTERNAL_ENDPOINT = "/internal/scheduler";
    public final String USERS_WITH_TASKS_ENDPOINT = SCHEDULER_INTERNAL_ENDPOINT + "/users-with-tasks";
    public final String COMPLETED_TASKS_ENDPOINT =
            SCHEDULER_INTERNAL_ENDPOINT + "/tasks/completed";

}
