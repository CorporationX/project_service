package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacancy")
public class VacancyController {
    private final VacancyService vacancyService;
    private final VacancyValidator vacancyValidator;

    @GetMapping(path = "/get-vacancies")
    public List<VacancyDto> getVacancies(@RequestBody VacancyFilterDto filter) {
        return vacancyService.getVacancies(filter);
    }

    @PostMapping(path = "/create")
    public VacancyDto create(@RequestBody VacancyDto vacancyDto) {
        vacancyValidator.validateVacancyController(vacancyDto);
        return vacancyService.createVacancy(vacancyDto);
    }

    @PutMapping(path = "/update")
    public VacancyDto update(@RequestBody VacancyDto vacancyDto) {
        vacancyValidator.validateVacancyController(vacancyDto);
        return vacancyService.updateVacancy(vacancyDto);
    }

    @DeleteMapping(path = "/delete/{id}")
    public VacancyDto deleteVacancy(@PathVariable Long id) {
        vacancyValidator.validateId(id);
        return vacancyService.deleteVacancy(id);
    }
}
