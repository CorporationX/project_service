package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyCRUDException;
import faang.school.projectservice.service.vacancy.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class VacancyController {
    private VacancyService vacancyService;

    @PostMapping("/vacancy")
    public VacancyDto createVacancy(@RequestBody VacancyDto vacancyDto) {
        validateInputVacancy(vacancyDto);
        return vacancyService.createVacancy(vacancyDto);
    }

    private void validateInputVacancy(VacancyDto vacancy) {
        if (vacancy == null) {
            throw new VacancyCRUDException(ErrorMessagesForVacancy.INPUT_BODY_IS_NULL);
        }
    }
}