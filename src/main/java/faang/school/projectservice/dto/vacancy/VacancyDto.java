package faang.school.projectservice.dto.vacancy;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VacancyDto {
    private Long id;
    private String name;
    private String position;
    private Long projectId;
    private LocalDateTime createdAt;
    private String createdBy;
    private String status;
    private Integer count;
    private List<Long> candidateIds;
}
