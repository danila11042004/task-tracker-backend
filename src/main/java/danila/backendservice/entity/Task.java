package danila.backendservice.entity;

import danila.backendservice.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@DynamicUpdate
@Table(name = "tasks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String headline;

    @Column(name = "text_content", nullable = false)
    private String textContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Task(String headline, String textContent, User user) {
        this.headline = headline;
        this.textContent = textContent == null ? "" : textContent;
        this.status = TaskStatus.TODO;
        this.user = user;
    }

    @PrePersist
    @PreUpdate
    private void normalize() {
        headline = headline.strip();
        textContent = textContent.strip();
    }

    public void update(String headline, String textContent, TaskStatus status) {
        if (headline != null) {
            this.headline = headline;
        }
        if (textContent != null) {
            this.textContent = textContent;
        }
        if (status != null) {
            if (status == TaskStatus.COMPLETED && this.status != TaskStatus.COMPLETED) {
                this.completedAt = LocalDateTime.now();
            } else if (status != TaskStatus.COMPLETED) {
                this.completedAt = null;
            }
            this.status = status;
        }

    }
}
