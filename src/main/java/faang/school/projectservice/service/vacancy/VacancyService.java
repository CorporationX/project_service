package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;

import java.util.List;

public interface VacancyService {

    VacancyDto createVacancy(VacancyDto vacancyDto);

    VacancyDto updateVacancy(VacancyDto vacancyDto);

    void deleteVacancy(VacancyDto vacancyDto);

    List<VacancyDto> getVacanciesWithFilter(VacancyFilterDto vacancyFilterDto);

    VacancyDto getVacancy(long id);
}
