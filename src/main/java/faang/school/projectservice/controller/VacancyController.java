package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

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



}
