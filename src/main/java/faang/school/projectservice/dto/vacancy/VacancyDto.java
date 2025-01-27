package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;

import java.time.LocalDateTime;
import java.util.List;

public record VacancyDto(
        Long id,
        String name,
        String description,
        TeamRole position,
        Long projectId,
        List<CandidateDto> candidatesDto,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdBy,
        Long updatedBy,
        VacancyStatus status,
        Double salary,
        WorkSchedule workSchedule,
        Integer count) {}
