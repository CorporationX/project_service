package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.VacancyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacancyFilterDto {
    private String namePattern;
    private VacancyStatus statusPattern;
}
