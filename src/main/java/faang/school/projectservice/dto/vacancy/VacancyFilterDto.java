package faang.school.projectservice.dto.vacancy;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VacancyFilterDto {
    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Position must not be blank")
    private String position;
}