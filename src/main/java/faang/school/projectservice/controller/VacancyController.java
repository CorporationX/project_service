package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyCandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService service;
    private final VacancyMapper mapper;

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
