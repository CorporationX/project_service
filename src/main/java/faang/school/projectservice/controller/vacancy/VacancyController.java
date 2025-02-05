package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.mapper.CandidateMapper;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.vacancy.VacancyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/vacancies")
@RestController
public class VacancyController {
    private final VacancyService vacancyService;
    private final VacancyMapper vacancyMapper;
    private final UserContext userContext;
    private final CandidateMapper candidateMapper;

    @PostMapping
    public ResponseEntity<VacancyResponseDto> createVacancy(@Valid @NotNull @RequestBody VacancyUpdateDto vacancyUpdateDto) {
        Vacancy vacancy = vacancyMapper.toEntity(vacancyUpdateDto);
        Long userId = userContext.getUserId();
        vacancy = vacancyService.createVacancy(vacancy, userId);
        return ResponseEntity.ok(vacancyMapper.toDto(vacancy));
    }

    @PatchMapping("/close/{id}")
    public ResponseEntity<VacancyResponseDto> closeVacancy(@Valid @NotNull @PathVariable("id") Long vacancyId) {
        Vacancy vacancy = vacancyService.closeVacancy(vacancyId, userContext.getUserId());
        VacancyResponseDto vacancyResponseDto = vacancyMapper.toDto(vacancy);
        return ResponseEntity.ok(vacancyResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacancyResponseDto> getVacancy(@Valid @NotNull @PathVariable("id") Long vacancyId) {
        VacancyResponseDto vacancyResponseDto = vacancyMapper.toDto(vacancyService.getVacancy(vacancyId));
        return ResponseEntity.ok(vacancyResponseDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VacancyResponseDto> updateVacancy(@Valid @NotNull @PathVariable("id") Long vacancyId,
                                                            @Valid @NotNull @RequestBody VacancyUpdateDto vacancyUpdateDto) {
        Vacancy vacancy = vacancyMapper.toEntity(vacancyUpdateDto);
        Long userId = userContext.getUserId();
        VacancyResponseDto result = vacancyMapper.toDto(vacancyService.updateVacancy(vacancyId, vacancy, userId));
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVacancy(@Valid @NotNull @PathVariable("id") Long vacancyId) {
        Long userId = userContext.getUserId();
        vacancyService.deleteVacancy(vacancyId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withFilters")
    public ResponseEntity<List<VacancyResponseDto>> getVacanciesByFilters(
            @Valid @NotNull @RequestBody VacancyFilterDto filterDto) {

        List<Vacancy> vacancies = vacancyService.getVacancies(filterDto);
        List<VacancyResponseDto> vacancyResponsesDtos = vacancyMapper.toDtoList(vacancies);
        return ResponseEntity.ok(vacancyResponsesDtos);
    }

    @PatchMapping("/{id}/addCandidates")
    public ResponseEntity<VacancyResponseDto> addCandidates(@Valid @NotNull @PathVariable("id") Long vacancyId,
                                                            @Valid @NotNull @RequestBody List<CandidateDto> candidateDtos) {
        Long userId = userContext.getUserId();
        List<Candidate> candidates = candidateMapper.toEntityList(candidateDtos);
        Vacancy vacancy = vacancyService.addCandidates(candidates, vacancyId, userId);
        return ResponseEntity.ok(vacancyMapper.toDto(vacancy));
    }
}
