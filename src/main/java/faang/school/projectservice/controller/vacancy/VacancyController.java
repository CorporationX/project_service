package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;
    private final ValidationVacancy validationVacancy;

    private VacancyDto createVacancy(@NonNull Long creatorId, VacancyDto vacancyDto) {
        validationVacancy.checkVacancy(vacancyDto);
        validationVacancy.checkVacancyName(vacancyDto);
        validationVacancy.checkProjectId(vacancyDto);

        return vacancyService.createVacancy(creatorId, vacancyDto);
    }
}
