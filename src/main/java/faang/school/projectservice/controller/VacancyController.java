package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateDeleteVacancyDto;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RequiredArgsConstructor
@Controller
@Validated
public class VacancyController {
    private final VacancyService vacancyService;

    public CreateVacancyDto createVacancy(@NotNull @Valid CreateVacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    public UpdateDeleteVacancyDto updateVacancy(@NotNull @Valid UpdateDeleteVacancyDto vacancyDto) {
        return vacancyService.updateVacancy(vacancyDto);
    }

    public void deleteVacancy(@NotNull @Valid UpdateDeleteVacancyDto vacancyDto) {
        vacancyService.deleteVacancy(vacancyDto);
    }

    public List<UpdateDeleteVacancyDto> getFilteredVacancies(@NotBlank String name, @NotBlank String position) {
        return vacancyService.getFilteredVacancies(name, position);
    }

    public UpdateDeleteVacancyDto getVacancy(@Positive long vacancyId) {
        return vacancyService.getVacancy(vacancyId);
    }
}
