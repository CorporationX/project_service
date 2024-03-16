package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacancyDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private List<Long> candidatesIds;
    private Long curatorId;
    private VacancyStatus status;
    private Integer workersRequired;
}
