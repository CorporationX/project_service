package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.filter.VacancyFilterDto;
import faang.school.projectservice.service.VacancyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyServiceImpl vacancyService;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    public VacancyDto updateVacancy(Long vacancyId, VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(vacancyId, vacancyDto);
    }

    public void deleteVacancy(Long vacancyId) {
        vacancyService.deleteVacancy(vacancyId);
    }

    public List<VacancyDto> getVacanciesByTitleFilter(VacancyFilterDto vacancyFilterDto) {
        return vacancyService.getVacancies(vacancyFilterDto);
    }

    public VacancyDto findVacancyById(Long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }
}
