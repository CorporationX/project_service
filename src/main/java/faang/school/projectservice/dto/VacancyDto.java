package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VacancyDto {
    private Long id;
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private TeamRole position;

    @Enumerated(EnumType.STRING)
    private VacancyStatus status;
}
