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

    public VacancyDto closeVacancy(VacancyDto vacancy) {
        return vacancyService.closeVacancy(vacancy);
    }

    public VacancyDto hireCandidate(VacancyDto vacancy, long userId, TeamRole teamRole) {
        return vacancyService.hireCandidate(vacancy, userId, teamRole);
    }

    public void deleteVacancy(VacancyDto vacancy) {
        vacancyService.deleteVacancy(vacancy);
    }

    public List<VacancyDto> getVacancyByNameAndPosition(VacancyFilterDto vacancyFilter) {
        return vacancyService.getVacancyByNameAndPosition(vacancyFilter);
    }

    public VacancyDto getVacancy(long id) {
        return vacancyService.getVacancy(id);
    }
}
