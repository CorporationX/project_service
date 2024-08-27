package faang.school.projectservice.controller;


import faang.school.projectservice.dto.vacancy.CreateVacancy;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Vacancy")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vacancy")
public class VacancyController {

    private final VacancyService vacancyService;

    @Operation(summary = "Create vacancy")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyDto createVacancy(@RequestBody @Validated(CreateVacancy.class) VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    @Operation(summary = "Update vacancy")
    @PutMapping("/{vacancyId}")
    @ResponseStatus(HttpStatus.OK)
    public VacancyDto updateVacancy(@RequestBody VacancyDto vacancyDto, @PathVariable long vacancyId) {
        return vacancyService.updateVacancy(vacancyDto, vacancyId);
    }

    @Operation(summary = "Close vacancy")
    @PutMapping("/close/{vacancyId}")
    @ResponseStatus(HttpStatus.OK)
    public void closeVacancy(@PathVariable @Positive long vacancyId) {
        vacancyService.closeVacancy(vacancyId);
    }

    @Operation(summary = "Delete vacancy")
    @DeleteMapping("/{vacancyId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteVacancy(@PathVariable @Positive long vacancyId) {
        vacancyService.deleteVacancy(vacancyId);
    }

    @Operation(summary = "Get vacancy position and name")
    @GetMapping
    @ResponseStatus(HttpStatus.FOUND)
    public List<VacancyDto> getVacancyPositionAndName(@RequestBody VacancyFilterDto vacancyFilterDto) {
        return vacancyService.getVacancyPositionAndName(vacancyFilterDto);
    }

    @Operation(summary = "Get vacancy by id")
    @GetMapping("/{vacancyId}")
    @ResponseStatus(HttpStatus.FOUND)
    public VacancyDto getVacancyById(@PathVariable @Positive long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }
}