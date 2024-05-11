package faang.school.projectservice.dto;

import faang.school.projectservice.model.VacancyStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VacancyDto {

    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private List<Long> candidateIds;
    private Long createdBy;
    private VacancyStatus status;
    private Double salary;
    private Integer count;
    private List<Long> requiredSkillIds;
}
