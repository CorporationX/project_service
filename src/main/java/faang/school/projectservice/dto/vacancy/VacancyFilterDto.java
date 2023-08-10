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
public class VacancyFilterDto {
    private String name;
    private String descriptionPattern;
    private VacancyStatus vacancyStatus;
    private Long requiredSkillId;
}
