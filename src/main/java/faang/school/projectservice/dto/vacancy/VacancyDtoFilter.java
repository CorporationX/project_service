package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.VacancyStatus;
import lombok.Data;

@Data
public class VacancyDtoFilter {
    private String name;
    private VacancyStatus status;
}
