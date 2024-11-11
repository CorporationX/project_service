package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import lombok.Getter;


import java.time.LocalDateTime;
import java.util.List;

@Getter
public class VacancyFilterDto {
    private String namePattern;
    private String descriptionPattern;
    private Long  projectIdPattern;
    private List<Long> candidatesIdPattern;
    private LocalDateTime createdAtPattern;
    private LocalDateTime updatedAtPattern;
    private Long createdByPattern;
    private Long updatedByPattern;
    private Long statusIdPattern;
    private Double salaryPattern;
    private Long workScheduleIdPattern;
    private Integer countPattern;
    private List<Long> requiredSkillIdsPattern;
}
