package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.CloseVacancyDto;
import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vacancies")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    public VacancyResponseDto createVacancy(@Valid @RequestBody CreateVacancyDto dto) {
        return vacancyService.createVacancy(dto);
    }

    @PutMapping("/{vacancyId}")
    public VacancyResponseDto updateVacancy(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVacancyDto dto) {
        dto.setId(id);
        return vacancyService.updateVacancy(dto);
    }

    @PutMapping("/{vacancyId}/close")
    public VacancyResponseDto closeVacancy(
            @PathVariable Long id,
            @Valid @RequestBody CloseVacancyDto dto) {
        VacancyResponseDto response = vacancyService.closeVacancy(id, dto);
        return response;
    }

    @GetMapping
    public List<VacancyResponseDto> getFilteredVacancies(@Valid VacancyFilterDto filterDto) {
        return vacancyService.getFilteredVacancies(filterDto);
    }

    @GetMapping("/{vacancyId}")
    public VacancyResponseDto getVacancyById(@PathVariable Long id) {
        return vacancyService.getVacancyById(id);
    }
}