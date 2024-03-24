package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancies")
@Tag(name = "Vacancies", description = "Endpoints for managing vacancies")
public class VacancyController {
    private final VacancyService vacancyService;

    @Operation(summary = "Create a vacancy")
    @PostMapping
    public VacancyDto create(@Valid @RequestBody VacancyDto vacancyDto) {
        return vacancyService.create(vacancyDto);
    }

    @Operation(summary = "Get vacancy by id")
    @GetMapping("/{vacancyId}")
    public VacancyDto getVacancyById(@PathVariable long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }

    @Operation(summary = "Update existing vacancy")
    @PutMapping
    public VacancyDto update(@Valid @RequestBody VacancyDto vacancyDto) {
        return vacancyService.update(vacancyDto);
    }

    @Operation(summary = "Delete a vacancy by id")
    @DeleteMapping("{vacancyId}")
    public void delete(@PathVariable long vacancyId) {
        vacancyService.delete(vacancyId);
    }

    @Operation(summary = "Get list of filtered vacancies")
    @PostMapping("/filtered")
    public List<VacancyDto> getFilteredVacancies(@RequestBody VacancyFilterDto filter) {
        return vacancyService.getFilteredVacancies(filter);
    }
}
