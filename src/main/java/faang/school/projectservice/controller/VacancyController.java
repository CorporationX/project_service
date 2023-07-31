package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping("/vacancy")
    public VacancyDto createVacancy(@RequestBody VacancyDto vacancyDto) {
        validateVacancy(vacancyDto);
        return vacancyService.createVacancy(vacancyDto);
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        validateVacancy(vacancyDto);
        return vacancyService.updateVacancy(vacancyDto);
    }

    private void validateVacancy(VacancyDto vacancyDto) {
        if (vacancyDto.getId() != null && vacancyDto.getId() < 0) {
            throw new DataValidationException("Vacancy should have correct id");
        } else if (vacancyDto.getName() == null || vacancyDto.getName().isBlank()) {
            throw new DataValidationException("Vacancy can't have an empty name");
        } else if (vacancyDto.getProjectId() == null || vacancyDto.getProjectId() <= 0) {
            throw new DataValidationException("Vacancy should have correct project id");
        } else if (vacancyDto.getCreatedBy() == null || vacancyDto.getCreatedBy() <= 0) {
            throw new DataValidationException("Vacancy should have correct creator id");
        } else if (vacancyDto.getStatus() == null) {
            throw new DataValidationException("Vacancy status can't be null");
        }
    }
}
