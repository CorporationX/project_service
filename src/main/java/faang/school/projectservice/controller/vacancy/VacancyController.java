package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDtoGetReq;
import faang.school.projectservice.dto.vacancy.VacancyDtoUpdateReq;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/{id}")
    @Operation(summary = "Обновить существующую вакансию.",
            description = "Вакансию может обновить менеджер или владелец. " +
                    "Закрыть вакансию возможно когда набрано 5 и более участников.")
    public VacancyDto updateVacancy(@NotNull @PathVariable("id") Long vacancyId,
                                    @Valid @RequestBody VacancyDtoUpdateReq vacancyDto) {
        return vacancyService.updateVacancy(vacancyId, vacancyDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить существующую вакансию.",
            description = "Удалить вакансию по ID. При удалении вакансии будут также удалены все кандидаты, " +
                    "если они не были приняты в команду")
    public void deleteVacancy(@NotNull @PathVariable("id") Long vacancyId) {
        vacancyService.deleteVacancy(vacancyId);
    }

    @GetMapping("/filteredVacancies")
    @Operation(summary = "Получить отфильтрованные вакансии",
            description = "Фильтрация осуществляется по переданным фильтрам.")
    public List<VacancyDto> getVacancyByFilter(@NotNull @RequestBody VacancyFilterDto vacancyFilter) {
        return vacancyService.getVacanciesByFilter(vacancyFilter);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить полную информацию о вакансии",
            description = "Получить полную информацию о вакансии.")
    public VacancyDtoGetReq getVacancy(@NotNull @PathVariable("id") Long vacancyId) {
        return vacancyService.getVacancy(vacancyId);
    }
}