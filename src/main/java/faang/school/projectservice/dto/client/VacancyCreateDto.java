package faang.school.projectservice.dto.client;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VacancyCreateDto {

    private String name;
    private String description;
    private Long projectId;
    private Double salary;
    private Long createdBy;
    private String workSchedule;
    private Integer count;
    private List<Long> requiredSkillIds;
}
