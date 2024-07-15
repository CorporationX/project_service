package faang.school.projectservice.dto.vacancy;


import faang.school.projectservice.model.VacancyStatus;
import lombok.Data;

import java.util.List;

@Data
public class VacancyDto {
    private Long id;
    private String name;
    private Long projectId;
    private List<Long> candidatesId;
    private VacancyStatus status;
    private Double salary;
    private List<Long> requiredSkillIds;
}