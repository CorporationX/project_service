package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VacancyFilterDto {
    private String name;
    private VacancyStatus status;
}
