package faang.school.projectservice.dto.vacancy;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class VacancyFilterDto {
    // Получить все вакансии с фильтрами по позиции и названию.
    private String namePattern;
    private List<Long> skillsPattern;
}
