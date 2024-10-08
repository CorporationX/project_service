package faang.school.projectservice.dto;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Name must not be blank")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Description must not be blank")
    private String description;

    private TaskStatus status;

    @NotNull(message = "Performer User ID must not be null")// Статус задачи
    private Long performerUserId;

    @NotNull(message = "Reporter User ID must not be null")// ID исполнителя задачи
    private Long reporterUserId;       // ID автора задачи

    private Integer minutesTracked;    // Время, затраченное на задачу
    private LocalDateTime createdAt;   // Время создания задачи
    private LocalDateTime updatedAt;   // Время обновления задачи
}