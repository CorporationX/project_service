package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alexander Bulgakov
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancy")
public class VacancyController {
    private final VacancyService vacancyService;
    private final VacancyValidator vacancyValidator;

    @PostMapping("/create")
    public VacancyDto create(@RequestBody VacancyDto vacancyDto) {
        vacancyValidator.checkControllerDataValidator(vacancyDto);
        return vacancyService.createVacancy(vacancyDto);
    }
}
