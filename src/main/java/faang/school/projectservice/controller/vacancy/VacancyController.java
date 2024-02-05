package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.api.VacancyApi;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */

@RestController
@RequiredArgsConstructor
public class VacancyController implements VacancyApi {
    private final VacancyService vacancyService;
    private final VacancyValidator vacancyValidator;

    @Override
    public VacancyDto getVacancy(Long id) {
        return vacancyService.getVacancy(id);
    }

    @Override
    public List<VacancyDto> getVacancies(VacancyFilterDto filter) {
        return vacancyService.getVacancies(filter);
    }

    @Override
    public VacancyDto create(VacancyDto vacancyDto) {
        vacancyValidator.validateVacancy(vacancyDto);
        return vacancyService.createVacancy(vacancyDto);
    }

    @Override
    public VacancyDto update(VacancyDto vacancyDto) {
        vacancyValidator.validateVacancy(vacancyDto);
        return vacancyService.updateOrCloseVacancy(vacancyDto);
    }

    @Override
    public VacancyDto deleteVacancy(Long id) {
        return vacancyService.deleteVacancy(id);
    }
}
