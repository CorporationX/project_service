package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyCandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService service;

    public VacancyCandidateDto createVacancy(@RequestBody VacancyDto vacancyDto) {
        return service.createVacancy(vacancyDto);
    }

    public VacancyCandidateDto updateVacancy(long vacancyId, @RequestBody VacancyDto vacancyDto) {
        return service.updateVacancy(vacancyId, vacancyDto);
    }

    public VacancyCandidateDto getVacancyInfoBiId(long vacancyId) {
        return service.getVacancyInfoById(vacancyId);
    }

    public List<VacancyCandidateDto> findVacancy(VacancyFilterDto filter) {
        return service.findVacancy(filter);
    }

}
