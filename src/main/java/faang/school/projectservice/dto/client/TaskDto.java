package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDto {
    private Long id;                // для идентификации обновляемой задачи
    private String description;     // описание
    private TaskStatus status;      // статус
    private Long performerUserId;   // исполнитель
    private LocalDateTime deadline; // deadline
}
