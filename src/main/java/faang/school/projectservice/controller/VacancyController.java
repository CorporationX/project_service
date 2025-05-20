package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/vacancies")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @PostMapping
    public ResponseEntity<VacancyDto> createVacancy(
            @PathVariable long projectId,
            @RequestBody VacancyDto vacancyDto) {
        return ResponseEntity.ok(vacancyService.createVacancy(projectId, vacancyDto));
    }

    @PutMapping("/{vacancyId}")
    public ResponseEntity<VacancyDto> updateVacancy(
            @PathVariable long projectId,
            @PathVariable long vacancyId,
            @RequestBody VacancyDto vacancyDto) {
        return ResponseEntity.ok(
                vacancyService.updateVacancy(projectId, vacancyId, vacancyDto)
        );
    }

    @PostMapping("/{vacancyId}/close")
    public ResponseEntity<VacancyDto> closeVacancy(
            @PathVariable long projectId,
            @PathVariable long vacancyId) {
        return ResponseEntity.ok(
                vacancyService.closeVacancy(projectId, vacancyId)
        );
    }

    @GetMapping("/{vacancyId}")
    public ResponseEntity<VacancyDto> getVacancyById(
            @PathVariable long projectId,
            @PathVariable long vacancyId) {
        return ResponseEntity.ok(
                vacancyService.getVacancyById(projectId, vacancyId)
        );
    }

    @GetMapping
    public ResponseEntity<List<VacancyDto>> listVacancies(
            @PathVariable long projectId,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(
                vacancyService.getVacanciesByProjectId(projectId, position, name)
        );
    }
}