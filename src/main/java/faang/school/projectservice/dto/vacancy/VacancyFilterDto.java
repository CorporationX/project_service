package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.Size;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VacancyFilterDto {
    @Size(max = 255, message = "Name should not exceed 255 characters")
    private String name;
    @Size(max = 255, message = "Description should not exceed 255 characters")
    private String description;
    private VacancyStatus status;
    private String name;
    private VacancyStatus status;
}
