package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.Vacancy.*;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.ResponseInternshipDto;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/vacancy")
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    public ExtendedVacancyDto create(@Valid @RequestBody CreateVacancyDto vacancyDto) {
        ExtendedVacancyDto newVacancy = vacancyService.create(vacancyDto);
        return newVacancy;
    }

    @PutMapping
    public ExtendedVacancyDto update(@Valid @RequestBody UpdateVacancyDto vacancyDto) {
        ExtendedVacancyDto updated = vacancyService.update(vacancyDto);
        return updated;
    }

    @PatchMapping
    public ResponseEntity<Void> changeCandidateStatus(UpdateCandidateRequestDto updateCandidate) {
        vacancyService.changeCandidateStatus(updateCandidate);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/byFilter")
    public List<ExtendedVacancyDto> getAllByFilter(@Valid @RequestBody VacancyFilterDto vacancyFilterDto) {
        return vacancyService.findByFilter(vacancyFilterDto);
    }
}
