package danila.backendservice.repository;

import danila.backendservice.entity.Task;
import danila.backendservice.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> getAllByUserId(Long userId);

    int deleteByIdAndUserId(Long id, Long userId);

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    void deleteByStatusAndId(TaskStatus taskStatus, Long userId);
}
