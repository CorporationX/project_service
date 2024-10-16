package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.VacancyStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VacancyUpdateDto {
    private String name;
    private String description;
    private Double salary;
    private String workSchedule;
    private VacancyStatus status;
    private List<Long> requiredSkillIds;
}
