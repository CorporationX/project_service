package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.srvice.vacancy.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/vacancy")
public class VacancyController {
    private final VacancyService service;
    private final VacancyValidator vacancyValidator;

    @DeleteMapping("/{creatorId}")
    public void deleteVacancy(@RequestBody VacancyDto vacancyDto, @PathVariable long creatorId) {
        vacancyValidator.deleteVacancyControllerValidation(vacancyDto, creatorId);
        service.deleteVacancy(vacancyDto, creatorId);
    }
}
