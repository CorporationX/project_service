package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyCreationRequest;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class VacancyController {
    private final faang.school.projectservice.service.VacancyService vacancyService;

    @PostMapping("/vacancies")
    public ResponseEntity<VacancyDto> createVacancy(@RequestBody @Valid VacancyCreationRequest request) {
        VacancyDto createdVacancy = vacancyService.createVacancy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVacancy);
    }

    @PutMapping("/vacancies/{id}")
    public ResponseEntity<VacancyDto> updateVacancy(
            @PathVariable Long id,
            @RequestBody @Valid VacancyUpdateRequest request
    ) {
        VacancyDto updatedVacancy = vacancyService.updateVacancy(id, request);
        return ResponseEntity.ok(updatedVacancy);
    }

    @DeleteMapping("/vacancies/{id}")
    public ResponseEntity<Void> deleteVacancy(@PathVariable Long id) {
        vacancyService.deleteVacancy(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vacancies")
    public ResponseEntity<List<VacancyDto>> getVacancies(
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String title
    ) {
        List<VacancyDto> vacancies = vacancyService.getVacancies(position, title);
        return ResponseEntity.ok(vacancies);
    }

    @GetMapping("/vacancies/{id}")
    public ResponseEntity<VacancyDto> getVacancyById(@PathVariable Long id) {
        VacancyDto vacancy = vacancyService.getVacancyById(id);
        return ResponseEntity.ok(vacancy);
    }
}
