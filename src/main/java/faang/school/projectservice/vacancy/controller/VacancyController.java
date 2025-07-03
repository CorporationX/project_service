package faang.school.projectservice.vacancy.controller;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.vacancy.dto.AddCandidatesDto;
import faang.school.projectservice.vacancy.dto.CloseVacancyDto;
import faang.school.projectservice.vacancy.dto.VacancyCreateDto;
import faang.school.projectservice.vacancy.dto.VacancyDto;
import faang.school.projectservice.vacancy.dto.VacancyUpdateDto;
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
import faang.school.projectservice.vacancy.service.VacancyService;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vacancies")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    public VacancyDto createVacancy(@RequestBody VacancyCreateDto dto) throws AccessDeniedException {
        return vacancyService.createVacancy(dto);
    }

    @PutMapping("/{id}")
    public VacancyDto updateVacancy(@PathVariable UUID id,
                                    @RequestBody VacancyUpdateDto dto) throws AccessDeniedException {
        return vacancyService.updateVacancy(id, dto);
    }

    @PostMapping("/{id}/candidates")
    public VacancyDto addCandidates(@PathVariable UUID id,
                                    @RequestBody AddCandidatesDto dto) {
        return vacancyService.addCandidates(id, dto);
    }

    @PostMapping("/{id}/close")
    public VacancyDto closeVacancy(@PathVariable UUID id,
                                   @RequestBody CloseVacancyDto dto) throws AccessDeniedException {
        return vacancyService.closeVacancy(id, dto);
    }

    @GetMapping
    public List<VacancyDto> getAllVacancies() {
        return vacancyService.getAllVacancies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacancyDto> getVacancy(@PathVariable UUID id) {
        return ResponseEntity.ok(vacancyService.getVacancy(id));
    }

    @GetMapping("/filter")
    public List<Vacancy> filter(@RequestParam TeamRole position,
                                @RequestParam String title) {
        return vacancyService.filterVacancies(position, title);
    }
}