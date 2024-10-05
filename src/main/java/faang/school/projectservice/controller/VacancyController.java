package faang.school.projectservice.controller;

import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.VacancyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@AllArgsConstructor
@RestController("/vacancy")
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    public VacancyDto createVacancy(@RequestBody VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    @PutMapping("/update/{id}")
    public void updateVacancy(@PathVariable Long id) {
        vacancyService.updateVacancy(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteVacancy(@PathVariable Long id) {
        vacancyService.deleteVacancy(id);
    }

    @GetMapping("/getList/{name}")
    public List<Vacancy> findVacancyByName(@PathVariable String name) {
        return vacancyService.findVacancyByName(name);
    }

    @GetMapping("/getVacancy/{id}")
    public Vacancy getVacancyById(@PathVariable Long id) {
        return vacancyService.getVacancyById(id);
    }
}
