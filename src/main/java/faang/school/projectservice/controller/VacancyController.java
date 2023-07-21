package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        validateVacancy(vacancyDto);
        return vacancyService.createVacancy(vacancyDto);
    }

    private void validateVacancy(VacancyDto vacancyDto) {
        if (vacancyDto.getName() == null || vacancyDto.getName().isBlank()) {
            throw new DataValidationException("Vacancy can't have create an empty name");
        } else if (vacancyDto.getProjectId() == null || vacancyDto.getProjectId() < 0) {
            throw new DataValidationException("Vacancy should have correct project id");
        } else if (vacancyDto.getCreatedBy() == null || vacancyDto.getCreatedBy() < 0) {
            throw new DataValidationException("Vacancy should have correct creator id");
        }
    }
}
