package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.filter.VacancyFilterDto;
import faang.school.projectservice.service.VacancyServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Validated
public class VacancyController {
    private final VacancyServiceImpl vacancyService;

    public VacancyDto createVacancy(@Valid VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    public VacancyDto updateVacancy(@NotNull Long vacancyId, @Valid VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(vacancyId, vacancyDto);
    }

    public void deleteVacancy(@NotNull Long vacancyId) {
        vacancyService.deleteVacancy(vacancyId);
    }

    public List<VacancyDto> getVacanciesByTitleFilter(@Valid VacancyFilterDto vacancyFilterDto) {
        return vacancyService.getVacancies(vacancyFilterDto);
    }

    public VacancyDto findVacancyById(@NotNull Long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }
}
