package faang.school.projectservice.model.dto;

import faang.school.projectservice.model.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private TaskStatus status;         // Статус задачи
    private Long performerUserId;      // ID исполнителя задачи
    private Long reporterUserId;       // ID автора задачи
    private Integer minutesTracked;    // Время, затраченное на задачу
    private LocalDateTime createdAt;   // Время создания задачи
    private LocalDateTime updatedAt;   // Время обновления задачи
}