package faang.school.projectservice.dto.vacancy;

import lombok.Data;

import java.util.List;

@Data
public class VacancyFilterDto {
    private String namePattern;
    private List<Long> skillIdsPattern;
}
