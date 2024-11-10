package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vacancy")
@RequiredArgsConstructor
@Validated
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping("/filters")
    public List<VacancyDto> getVacancyIdsByFilters(@RequestBody VacancyFilterDto filterDto) {
        return vacancyService.getVacancyIdsByFilters(filterDto);
    }

    @PostMapping("/create")
    public VacancyDto createVacancy(@RequestBody VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    @PutMapping("/{id}/update")
    public VacancyDto updateVacancy(@PathVariable @Min(1L) @NotNull Long id, @RequestBody VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(id, vacancyDto);
    }

    @PostMapping("/close")
    public VacancyDto closeVacancy(@RequestBody VacancyDto vacancyDto) {
        return vacancyService.closeVacancy(vacancyDto);
    }
}