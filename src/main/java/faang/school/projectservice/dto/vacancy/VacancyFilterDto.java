package faang.school.projectservice.dto.vacancy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacancyFilterDto {
    private String name;
    private String position;
}