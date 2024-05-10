package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.validation.ValidationCreator;
import faang.school.projectservice.validation.ValidationVacancy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;
    private final ValidationVacancy validationVacancy;
    private final ValidationCreator validationCreator;

    private VacancyDto createVacancy(Long creatorId, VacancyDto vacancyDto) {
        validationCreator.checkCreatorIdForNull(creatorId);
        validationVacancy.checkVacancy(vacancyDto);
        validationVacancy.checkVacancyName(vacancyDto);
        validationVacancy.checkVacancyNameForNull(vacancyDto);
        validationVacancy.checkProjectId(vacancyDto);

        return vacancyService.createVacancy(creatorId, vacancyDto);
    }
}
