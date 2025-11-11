package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.CandidateCreateDto;
import faang.school.projectservice.dto.vacancy.CandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.service.vacancy.CandidateService;
import faang.school.projectservice.service.vacancy.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/candidates")
@RestController
public class CandidateController {
    private final VacancyService vacancyService;
    private final CandidateService candidateService;

    @PostMapping
    public VacancyDto addCandidatesToVacancy(@RequestParam("vacancyId") Long vacancyId,
                                             @Valid @RequestBody CandidateCreateDto candidateCreateDto) {
        return vacancyService.addCandidatesToVacancy(vacancyId, candidateCreateDto);
    }

    @PatchMapping("/{candidateId}")
    public CandidateDto updateCandidateStatus(@RequestParam("vacancyId") Long vacancyId,
                                              @PathVariable Long candidateId,
                                              @RequestParam CandidateStatus status) {
        return candidateService.updateCandidateStatus(vacancyId, candidateId, status);
    }
}
