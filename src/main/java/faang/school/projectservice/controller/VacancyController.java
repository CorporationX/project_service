package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    public void create(@Valid @RequestBody VacancyDto vacancy) {
        vacancyService.create();
    }
}
