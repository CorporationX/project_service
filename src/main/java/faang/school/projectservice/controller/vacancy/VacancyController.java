package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vacancy")
@Validated
@RequiredArgsConstructor
@Tag(name = "Вакансии", description = "Взаимодействие с вакансиями.")
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping()
    @Operation(summary = "Создать новую вакансию.",
    description = "Позволяет создать вакансию. Вакансия принадлежит проекту. " +
            "Вакансию может создать участник с определенной ролью.")
    public VacancyDto createVacancy(@Valid @RequestBody VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }
}