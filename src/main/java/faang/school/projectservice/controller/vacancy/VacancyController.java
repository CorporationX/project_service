package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.srvice.vacancy.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/vacancy")
public class VacancyController {
    private final VacancyService service;
    private final VacancyValidator vacancyValidator;

    @DeleteMapping()
    public void deleteVacancy(@RequestBody VacancyDto vacancyDto) {
        vacancyValidator.deleteVacancyControllerVAlidation(vacancyDto);
        service.deleteVacancy(vacancyDto);
    }
}
