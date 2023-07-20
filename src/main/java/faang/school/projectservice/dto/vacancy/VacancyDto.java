package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VacancyDto {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private VacancyStatus status;
}
