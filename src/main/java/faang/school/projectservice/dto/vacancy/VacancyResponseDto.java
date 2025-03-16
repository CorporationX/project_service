package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class VacancyResponseDto {
    private final String name;
    private final String description;
    private final TeamRole position;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final VacancyStatus status;
    private final Double salary;
    private final WorkSchedule workSchedule;
    private final Integer count;
    private final List<Long> requiredSkillIds;
    private final String coverImageKey;

    private List<CandidateDto> candidates;
    private String projectName;
    private String createdByNickname;
    private String updatedByNickname;
}
