package faang.school.projectservice.controller;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/vacancies")
public class VacancyController {
    private static final String INVALID_VACANCY_ID_MSG = "vacancy id can`t be less than 1";

    private final VacancyService vacancyService;

    @PutMapping("/{vacancyId}/cover")
    public VacancyDto addCover(
            @PathVariable @Min(value = 1, message = INVALID_VACANCY_ID_MSG) long vacancyId,
            @RequestParam MultipartFile cover) {
        return vacancyService.addCover(vacancyId, cover);
    }
}
