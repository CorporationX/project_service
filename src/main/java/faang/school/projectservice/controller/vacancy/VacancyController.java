package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vacancies")
public class VacancyController {

    private final VacancyService vacancyService;

    @Autowired
    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @PostMapping
    public VacancyDto createVacancy(@RequestBody VacancyDto vacancyDto) {
        if(vacancyDto.getProjectId() == null)
            throw new RuntimeException("The vacancy must relate to some kind of project");
        return vacancyService.createVacancy(vacancyDto);
    }

    @PostMapping("/{id}")
    public VacancyDto updateVacancy(@PathVariable Long id, @RequestBody VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(id, vacancyDto);
    }

}
