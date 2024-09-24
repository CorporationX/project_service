package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyDto create(VacancyDto vacancy) {
        return vacancyService.create(vacancy);
    }

    public VacancyDto update(VacancyDto vacancy) {
        return vacancyService.update(vacancy);
    }

    public VacancyDto delete(VacancyDto vacancy) {
        return vacancyService.delete(vacancy);
    }

    public List<VacancyDto> getVacanciesByFilter(VacancyFilterDto filters) {
        return vacancyService.getVacanciesByFilter(filters);
    }

    public VacancyDto getVacancyById(VacancyDto vacancy) {
        return vacancyService.getVacancyById(vacancy);
    }

}
