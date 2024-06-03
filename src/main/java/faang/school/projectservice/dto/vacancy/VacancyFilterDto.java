package faang.school.projectservice.dto.vacancy;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VacancyFilterDto {
    private String name;
    private Long count;
}
