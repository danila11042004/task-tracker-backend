package danila.backendservice.repository;

import danila.backendservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);

    boolean existsByEmail(String email);

    @Query("""
                SELECT DISTINCT u FROM User u
                JOIN FETCH u.taskList
            """)
    List<User> findAllUserWithTasks();

}

