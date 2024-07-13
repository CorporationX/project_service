package faang.school.projectservice.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyFilterDto {
    private String name;
    private List<Long> skillIds;
}
