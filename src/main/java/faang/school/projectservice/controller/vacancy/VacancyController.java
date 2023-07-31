package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.srvice.vacancy.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/vacancy")
public class VacancyController {
    private VacancyService service;


    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        return vacancyDto;
    }
}
