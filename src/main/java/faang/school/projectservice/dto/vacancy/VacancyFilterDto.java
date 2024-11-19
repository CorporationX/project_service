package faang.school.projectservice.dto.vacancy;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VacancyFilterDto implements FilterDto {
    Long id;
    String namePattern;
}
