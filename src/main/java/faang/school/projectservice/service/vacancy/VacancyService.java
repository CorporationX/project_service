package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;

import java.util.List;

public interface VacancyService {

    VacancyDto create(VacancyDto vacancy);

    VacancyDto update(VacancyDto vacancy);

    VacancyDto delete(VacancyDto vacancy);

    List<VacancyDto> getVacanciesByFilter(VacancyFilterDto filters);

    VacancyDto getVacancyById(VacancyDto vacancy);

}
