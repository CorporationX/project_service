package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.CandidateCreateDto;
import faang.school.projectservice.dto.vacancy.CandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.vacancy.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/vacancies")
@RestController
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyDto createVacancy(@Valid @RequestBody VacancyCreateDto vacancyCreateDto) {

        return vacancyService.createVacancy(vacancyCreateDto);
    }

    @PatchMapping("/{vacancyId}")
    public VacancyDto updateVacancy(@PathVariable Long vacancyId,
                                    @Valid @RequestBody VacancyUpdateDto vacancyUpdateDto) {

        return vacancyService.updateVacancy(vacancyId, vacancyUpdateDto);
    }

    @PatchMapping("/{vacancyId}/candidates")
    public VacancyDto addCandidatesToVacancy(@PathVariable Long vacancyId,
                                             @Valid @RequestBody CandidateCreateDto candidateCreateDto) {

        return vacancyService.addCandidatesToVacancy(vacancyId, candidateCreateDto);
    }

    @PatchMapping("/{vacancyId}/candidates/{candidateId}")
    public CandidateDto updateCandidateStatus(@PathVariable Long vacancyId,
                                              @PathVariable Long candidateId,
                                              @RequestParam CandidateStatus status) {
        return vacancyService.updateCandidateStatus(vacancyId, candidateId, status);
    }

    @PatchMapping("/{vacancyId}/close")
    public VacancyDto closeVacancy(@PathVariable Long vacancyId) {
        return vacancyService.closeVacancy(vacancyId);
    }

    @GetMapping("/{vacancyId}")
    public VacancyDto getVacancy(@PathVariable Long vacancyId) {

        return vacancyService.getVacancy(vacancyId);
    }

    @GetMapping
    public List<VacancyDto> findVacancies(@RequestParam(required = false) String description,
                                          @RequestParam(required = false) TeamRole position) {
        return vacancyService.findVacancies(description, position);
    }

    @DeleteMapping("/{vacancyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVacancy(@PathVariable Long vacancyId) {

        vacancyService.deleteVacancy(vacancyId);
    }
}