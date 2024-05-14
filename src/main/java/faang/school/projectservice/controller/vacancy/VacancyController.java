package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;
    private final ValidationVacancy validationVacancy;

    @PostMapping("/vacancy/{creatorId}")
    public VacancyDto createVacancy(@NonNull @PathVariable Long creatorId, @NonNull @RequestBody VacancyDto vacancyDto) {
        validationVacancy.checkVacancyName(vacancyDto);
        validationVacancy.checkProjectId(vacancyDto);

        return vacancyService.createVacancy(creatorId, vacancyDto);
    }
}
