package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VacancyFilterDto {
    private String name;
    private VacancyStatus status;
}