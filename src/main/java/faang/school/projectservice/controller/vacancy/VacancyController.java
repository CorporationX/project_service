package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;


    public VacancyDto create(@Valid VacancyDto vacancyDto) {
        return vacancyService.create(vacancyDto);
    }

    public VacancyDto update(@Valid VacancyDto vacancyDto) {
        return vacancyService.update(vacancyDto);
    }

    public void delete(VacancyDto vacancyDto) {
        vacancyService.delete(vacancyDto);
    }

    public List<VacancyDto> getFilteredVacancies(VacancyFilterDto filter) {
        return vacancyService.getFilteredVacancies(filter);
    }

    public VacancyDto getVacancyById(long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }
}
