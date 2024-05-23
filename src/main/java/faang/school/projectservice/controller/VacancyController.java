package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    public VacancyDto createVacancy(VacancyDto vacancy) {
        return vacancyService.createVacancy(vacancy);
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(vacancyDto);
    }

    public void deleteVacancy(VacancyDto vacancy) {
        vacancyService.deleteVacancy(vacancy);
    }

    public List<VacancyDto> getVacanciesWithFilter(VacancyFilterDto vacancyFilter) {
        return vacancyService.getVacanciesWithFilter(vacancyFilter);
    }

    public VacancyDto getVacancy(long id) {
        return vacancyService.getVacancy(id);
    }
}
