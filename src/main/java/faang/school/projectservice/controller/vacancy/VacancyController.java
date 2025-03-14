package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.SearchVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.vacancy.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    public List<VacancyDto> vacancyFilter(SearchVacancyDto searchVacancyDto) {
        return vacancyService.vacancyFilter(searchVacancyDto);
    }

    public void getInfoByVacancyId(Long id) {
        vacancyService.getInfoByVacancyId(id);
    }


}
