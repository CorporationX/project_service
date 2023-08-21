package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.validation.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
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
}
