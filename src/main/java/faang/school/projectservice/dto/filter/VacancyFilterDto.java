package faang.school.projectservice.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class VacancyFilterDto {
    private String namePattern;
    private List<Long> skillIdsPattern;
}
