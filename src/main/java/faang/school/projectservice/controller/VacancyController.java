package faang.school.projectservice.controller;


import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDtoFilter;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.VacancyService;
import faang.school.projectservice.validator.VacancyControllerValidator;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;
    private final VacancyControllerValidator validator;

    public void createVacancy(@Valid Vacancy vacancy,@NonNull Integer count) {
        vacancyService.createVacancy(vacancy, count);
    }

    public void updateVacancy(long vacancyId, TeamRole teamRole) {
        validator.validatorId(vacancyId);
        vacancyService.updateVacancy(vacancyId, teamRole);
    }

    public void deleteVacancy(long vacancyId) {
        validator.validatorId(vacancyId);
        vacancyService.deleteVacancy(vacancyId);
    }

    public List<VacancyDto> getVacancyPositionAndName(VacancyDtoFilter vacancyDtoFilter) {
        return vacancyService.getVacancyPositionAndName(vacancyDtoFilter);
    }

    public Vacancy getVacancyById(long vacancyId) {
        validator.validatorId(vacancyId);
        return vacancyService.getVacancy(vacancyId);
    }
}