package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import lombok.Getter;

import java.util.List;


@Getter
public class VacancyDTO {
    private Long id;
    private String name;
    private String description;
    private Project project;
    private List<Candidate> candidates;
    private Integer count;
    private Long createdBy;
    private Long updatedBy;
    private VacancyStatus status;
    private Double salary;
    private WorkSchedule workSchedule;
    private List<Long> requiredSkillIds;
}
