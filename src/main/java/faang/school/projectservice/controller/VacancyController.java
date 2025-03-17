package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyCandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancies")
public class VacancyController {
    private final VacancyService service;

    @PostMapping
    public VacancyCandidateDto createVacancy(@RequestBody VacancyDto vacancyDto) {
        return service.createVacancy(vacancyDto);
    }

    @PutMapping("/{vacancyId}")
    public VacancyCandidateDto updateVacancy(@PathVariable long vacancyId, @RequestBody VacancyDto vacancyDto) {
        return service.updateVacancy(vacancyId, vacancyDto);
    }

    @GetMapping("/{vacancyId}")
    public VacancyCandidateDto getVacancyInfoBiId(@PathVariable long vacancyId) {
        return service.getVacancyInfoById(vacancyId);
    }

    @PostMapping("/search")
    public List<VacancyCandidateDto> findVacancy(@RequestBody VacancyFilterDto filter) {
        return service.findVacancy(filter);
    }

}
