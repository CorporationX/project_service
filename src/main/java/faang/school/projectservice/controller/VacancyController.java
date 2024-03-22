package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VacancyController {
    private VacancyService vacancyService;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(vacancyDto);
    }

    public VacancyDto deleteVacancy(VacancyDto vacancyDto) {
        return vacancyService.deleteVacancy(vacancyDto);
    }

    public List<VacancyDto> getFiltersVacancies(VacancyFilterDto vacancyFilterDto) {
        return vacancyService.getFilterVacancies(vacancyFilterDto);
    }

    public VacancyDto getVacancy(Long vacancyId) {
        return vacancyService.getVacancy(vacancyId);
    }
}
