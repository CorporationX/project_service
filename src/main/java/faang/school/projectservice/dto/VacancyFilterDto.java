package faang.school.projectservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Vacancy filter entity")
public class VacancyFilterDto {

    @Size(max = 255)
    private String name;
    private List<Long> skillIds;
}
