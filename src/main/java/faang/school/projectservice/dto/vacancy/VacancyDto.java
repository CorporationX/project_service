package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacancyDto {
    private Long id;
    private Long projectId;
    private String name;
    private String description;
    private Integer count;
    private Long createdBy;
    private VacancyStatus status;
    private Double salary;
    private String workSchedule;
}
