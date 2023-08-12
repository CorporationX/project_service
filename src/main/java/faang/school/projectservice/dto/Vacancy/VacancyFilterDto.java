package faang.school.projectservice.dto.Vacancy;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@NotNull
public class VacancyFilterDto {
        private String namePattern;
        private String description;
}
