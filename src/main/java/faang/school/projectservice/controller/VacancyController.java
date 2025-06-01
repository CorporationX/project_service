package faang.school.projectservice.controller;

import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.dto.candidate.CreateCandidateDto;
import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.DetailedVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.CandidateService;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancies")
@Slf4j
public class VacancyController {
    private final VacancyService vacancyService;
    private final CandidateService candidateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DetailedVacancyDto> createVacancy(@RequestBody @Valid CreateVacancyDto dto,
                                                    UriComponentsBuilder uriBuilder) {
        DetailedVacancyDto created = vacancyService.create(dto);
        URI location = uriBuilder
                .path("/vacancies/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{id}")
    public DetailedVacancyDto updateVacancy(@PathVariable @Min(1) Long id,
                                    @RequestBody @Valid UpdateVacancyDto dto) {
        return vacancyService.update(id, dto);
    }

    @PostMapping("/{id}")
    public CandidateDto addCandidate(@PathVariable @Min(1) Long id,
                                     @RequestBody @Valid CreateCandidateDto dto) {
        return vacancyService.addCandidate(id, dto);
    }

    @PostMapping("/{vacancyId}/candidates/{candidateId}/accept")
    public ResponseEntity<Void> acceptCandidate(@PathVariable @Min(1) Long vacancyId,
                                                @PathVariable @Min(1) Long candidateId,
                                                @RequestParam Long teamId) {
        candidateService.acceptCandidate(vacancyId, candidateId, teamId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{vacancyId}/candidates/{candidateId}/reject")
    public ResponseEntity<Void> rejectCandidate(@PathVariable @Min(1) Long vacancyId,
                                                @PathVariable @Min(1) Long candidateId) {
        candidateService.rejectCandidate(vacancyId, candidateId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/close")
    public DetailedVacancyDto closeVacancy(@PathVariable @Min(1) Long id) {
        return vacancyService.close(id);
    }

    @GetMapping
    public Page<VacancyDto> getAllVacancies(@Valid @RequestBody(required = false) VacancyFilterDto filter,
                                            Pageable pageable) {
        return vacancyService.getAll(filter, pageable);
    }

    @GetMapping("/{id}")
    public DetailedVacancyDto getVacancyById(@PathVariable @Min(1) Long id) {
        return vacancyService.getById(id);
    }
}
