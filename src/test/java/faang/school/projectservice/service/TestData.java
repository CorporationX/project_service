package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyRequestDto;
import faang.school.projectservice.service.filter.VacancyPositionFilter;
import faang.school.projectservice.service.filter.VacancyFilter;
import faang.school.projectservice.service.filter.VacancyNameFilter;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;

import java.util.ArrayList;
import java.util.List;

public class TestData {
    public static List<VacancyFilter> createVacancyFilters() {
        VacancyPositionFilter positionFilter = new VacancyPositionFilter();
        VacancyNameFilter vacancyNameFilter = new VacancyNameFilter();

        return new ArrayList<>(List.of(positionFilter, vacancyNameFilter));
    }

    public static Vacancy createVacancy(Long id, String name, TeamRole position) {
        return Vacancy.builder()
                .id(id)
                .name(name)
                .position(position)
                .build();
    }


    public static VacancyFilterDto createVacancyFilterDto(String charSequence, TeamRole role) {
        return VacancyFilterDto.builder()
                .nameContains(charSequence)
                .position(role)
                .build();
    }

    public static VacancyRequestDto createVacancyRequestDto(String name, TeamRole role, Long projectId,
                                                            List<Long> candidatesIds, Long createdBy, Long updatedBy,
                                                            VacancyStatus status, int count) {
        return VacancyRequestDto.builder()
                .name(name)
                .position(role)
                .projectId(projectId)
                .candidatesIds(candidatesIds)
                .createdBy(createdBy)
                .updatedBy(updatedBy)
                .status(status)
                .count(count)
                .build();
    }
}
