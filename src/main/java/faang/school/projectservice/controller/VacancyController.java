package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    public VacancyDto updateVacancy(Vacancy vacancy) {
        return vacancyService.updateVacancy(vacancy);
    }

    public void deleteVacancy(Long id) {
        vacancyService.deleteVacancy(id);
    }

    public List<Vacancy> getAllVacancies(String name) {
        return vacancyService.getAllVacancies(name);
    }

    public VacancyDto getVacancy(Long id) {
        return vacancyService.getVacancy(id);
    }

}
