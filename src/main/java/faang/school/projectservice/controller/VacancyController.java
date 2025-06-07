package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.CloseVacancyDto;
import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
@Slf4j
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    public ResponseEntity<VacancyResponseDto> createVacancy(@Valid @RequestBody CreateVacancyDto dto) {
        return ResponseEntity.ok(vacancyService.createVacancy(dto));
    }

    @PutMapping("/{vacancyId}")
    public ResponseEntity<VacancyResponseDto> updateVacancy(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVacancyDto dto) {
        dto.setId(id);
        return ResponseEntity.ok(vacancyService.updateVacancy(dto));
    }

    @PostMapping("/{vacancyId}/close")
    public ResponseEntity<VacancyResponseDto> closeVacancy(
            @PathVariable Long id,
            @Valid @RequestBody CloseVacancyDto dto) {
        VacancyResponseDto response = vacancyService.closeVacancy(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<VacancyResponseDto>> getFilteredVacancies(@Valid VacancyFilterDto filterDto) {
        return ResponseEntity.ok(vacancyService.getFilteredVacancies(filterDto));
    }

    @GetMapping("/{vacancyId}")
    public ResponseEntity<VacancyResponseDto> getVacancyById(@PathVariable Long id) {
        return ResponseEntity.ok(vacancyService.getVacancyById(id));
    }
}