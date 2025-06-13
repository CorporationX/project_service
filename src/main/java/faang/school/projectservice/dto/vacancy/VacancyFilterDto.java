package faang.school.projectservice.dto.vacancy;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VacancyFilterDto {
    private String name;
    private String position;
}