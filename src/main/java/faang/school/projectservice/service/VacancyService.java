package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyRequestDto;

import java.util.List;

public interface VacancyService {

    List<VacancyDto> getVacanciesByFilter(VacancyFilterDto filter);

    VacancyDto getVacancy(Long id);

    VacancyDto createVacancy(VacancyRequestDto vacancyDto);

    VacancyDto updateVacancy(VacancyRequestDto vacancyDto, Long id);

    void deleteVacancy(Long id);
}
