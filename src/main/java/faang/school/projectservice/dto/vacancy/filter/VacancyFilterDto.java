package faang.school.projectservice.dto.vacancy.filter;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyFilterDto {
    @Size(max = 255)
    private String name;
    private List<Long> skillIds;
}
