package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        validateVacancy(vacancyDto);
        return vacancyService.createVacancy(vacancyDto);
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        validateVacancy(vacancyDto);
        return vacancyService.updateVacancy(vacancyDto);
    }

    public void deleteVacancy(long id) {
        if (id < 0) {
            throw new DataValidationException("Vacancy should have correct id");
        }
        vacancyService.deleteVacancy(id);
    }

    public List<VacancyDto> getVacancies(VacancyFilterDto filter) {
        validateFilter(filter);
        return vacancyService.getVacancies(filter);
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

    private void validateFilter(VacancyFilterDto filter) {
        if (filter.getName() != null && filter.getName().isBlank()) {
            throw new DataValidationException("Name filter can't be empty");
        } else if (filter.getDescriptionPattern() != null && filter.getDescriptionPattern().isBlank()) {
            throw new DataValidationException("Description filter can't be empty");
        } else if (filter.getRequiredSkillId() != null && filter.getRequiredSkillId() <= 0) {
            throw new DataValidationException("Required skill id filter can't be less than 1");
        }
    }
}
