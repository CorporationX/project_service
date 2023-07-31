package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class VacancyDto {
    private Long id;
    private String name;
    private Long projectId;
    private Long createdBy;
    private Long updatedBy;
    private VacancyStatus status;
}
