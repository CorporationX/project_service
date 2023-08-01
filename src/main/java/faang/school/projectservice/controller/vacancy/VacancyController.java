package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.srvice.vacancy.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vacancy")
public class VacancyController {
    private final VacancyService service;
    private final VacancyValidator vacancyValidator;

    @DeleteMapping
    public void deleteVacancy(@RequestBody VacancyDto vacancyDto) {
        vacancyValidator.updateVacancyControllerValidation(vacancyDto);
        service.deleteVacancy(vacancyDto);
    }

    @PutMapping
    public VacancyDto updateVacancy(@RequestBody VacancyDto vacancyDto) {
        vacancyValidator.updateVacancyControllerValidation(vacancyDto);
        return service.updateVacancy(vacancyDto);
    }
}
