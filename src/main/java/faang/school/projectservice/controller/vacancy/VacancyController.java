package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping("/vacancy")
    public VacancyDto createVacancy(@RequestBody VacancyDto vacancyDto) {
        VacancyValidator.validateInputBody(vacancyDto);
        return vacancyService.createVacancy(vacancyDto);
    }
}