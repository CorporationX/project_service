package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDtoForUpdate;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vacancy")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;
    private final VacancyValidator vacancyValidator;

    @PostMapping()
    public VacancyDto createVacancy(@RequestBody VacancyDto vacancyDto) {
        vacancyValidator.validateInputBody(vacancyDto);
        return vacancyService.createVacancy(vacancyDto);
    }

    @PutMapping("/vacancy")
    public VacancyDto updateVacancy(@RequestBody VacancyDtoForUpdate vacancyDto) {
        vacancyValidator.validateInputBody(vacancyDto);
        return vacancyService.updateVacancy(vacancyDto);
    }
}