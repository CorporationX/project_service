package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyCoverDto;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/vacancy")
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping("/add/{vacancyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyCoverDto addVacancyCover(@PathVariable Long vacancyId,
                                           @RequestParam("file") MultipartFile file) {
        return vacancyService.addVacancyCover(vacancyId, file);
    }

    @DeleteMapping("/delete/{vacancyId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public VacancyCoverDto deleteVacancyCover(@PathVariable Long vacancyId) {
        return vacancyService.deleteVacancyCover(vacancyId);
    }
}
