package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;

import java.util.List;

public interface VacancyService {
    VacancyDto createVacancy(long projectId, VacancyDto vacancyDto);

    VacancyDto updateVacancy(long projectId, long vacancyId, VacancyDto vacancyDto);

    VacancyDto closeVacancy(long projectId, long vacancyId);

    VacancyDto getVacancyById(long projectId, long vacancyId);

    List<VacancyDto> getVacanciesByProjectId(long projectId, String positionFilter, String nameFilter);
}
