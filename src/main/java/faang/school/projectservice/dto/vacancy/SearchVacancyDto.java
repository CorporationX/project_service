package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import lombok.Builder;

@Builder
public record SearchVacancyDto(
        TeamRole position,
        String vacancyName
){

}
