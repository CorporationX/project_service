package faang.school.projectservice.dto;

import faang.school.projectservice.model.Project;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Project project;
}
