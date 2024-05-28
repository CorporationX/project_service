package faang.school.projectservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class VacancyFilterDto {

    @Size(max = 255)
    private String name;
    private List<Long> skillIds;
}
