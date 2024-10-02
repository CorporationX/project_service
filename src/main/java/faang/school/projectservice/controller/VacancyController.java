package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.VacancyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@AllArgsConstructor
@RestController("/vacancy")
public class VacancyController {
    private VacancyService vacancyService;

    @PostMapping("/create")
    public VacancyDto createVacancy(@RequestBody VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    @PutMapping("/update")
    public void updateVacancy(@RequestBody VacancyDto vacancyDto) {
        vacancyService.updateVacancy(vacancyDto);
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
