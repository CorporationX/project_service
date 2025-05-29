package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/projects/{projectId}/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping
    public VacancyDto createVacancy(
            @PathVariable long projectId,
            @RequestBody VacancyDto vacancyDto) {
        return vacancyService.createVacancy(projectId, vacancyDto);
    }

    @PutMapping("/{vacancyId}")
    public VacancyDto updateVacancy(
            @PathVariable long projectId,
            @PathVariable long vacancyId,
            @RequestBody VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(projectId, vacancyId, vacancyDto);
    }

    @PutMapping("/{vacancyId}/close")
    public VacancyDto closeVacancy(
            @PathVariable long projectId,
            @PathVariable long vacancyId) {
        return vacancyService.closeVacancy(projectId, vacancyId);
    }

    @GetMapping("/{vacancyId}")
    public VacancyDto getVacancyById(
            @PathVariable long projectId,
            @PathVariable long vacancyId) {
        return vacancyService.getVacancyById(projectId, vacancyId);
    }

    @GetMapping
    public List<VacancyDto> listVacancies(
            @PathVariable long projectId,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String name) {
        return vacancyService.getVacanciesByProjectId(projectId, position, name);
    }
}