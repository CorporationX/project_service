package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacancyDto {
    private Long vacancyId;
    private String name;
    private String description;
    private Long projectId;
    private Long createdBy;

    @Builder.Default
    private VacancyStatus status = VacancyStatus.OPEN;
}
