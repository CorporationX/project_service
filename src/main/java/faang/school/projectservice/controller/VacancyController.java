package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Validated
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyDto create(@Valid VacancyDto vacancy) {
        return vacancyService.create(vacancy);
    }

    public VacancyDto update(@Valid VacancyDto vacancy) {
        return vacancyService.update(vacancy);
    }

    public VacancyDto delete(@Valid VacancyDto vacancy) {
        return vacancyService.delete(vacancy);
    }

    public List<VacancyDto> getVacanciesByFilter(@Valid VacancyFilterDto filters) {
        return vacancyService.getVacanciesByFilter(filters);
    }

    public VacancyDto getVacancyById(@Valid VacancyDto vacancy) {
        return vacancyService.getVacancyById(vacancy);
    }

}
