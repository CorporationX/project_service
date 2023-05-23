package faang.school.projectservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "performer_user_id", nullable = false)
    private Long performerUserId;

    @Column(name = "reporter_user_id", nullable = false)
    private Long reporterUserId;

    @Column(name = "minutes_tracked")
    private int minutesTracked;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @ManyToMany
    @JoinTable(
            name = "task_linked_tasks",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "linked_task_id")
    )
    private List<Task> linkedTasks;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}

