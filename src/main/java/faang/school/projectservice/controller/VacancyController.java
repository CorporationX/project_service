package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyCreateDto;
import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.dto.client.VacancyUpdateDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.filter.VacancyFilter;
import faang.school.projectservice.service.VacancyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@AllArgsConstructor
@RestController("/vacancy")
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    public Vacancy createVacancy(@RequestBody VacancyCreateDto vacancyDto) {
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

    @GetMapping("/getList")
    public List<Vacancy> findVacancyByFilter(VacancyFilterDto vacancyFilterDto) {
        return vacancyService.findVacancyByFilter(vacancyFilterDto);
    }

    @GetMapping("/getVacancy/{id}")
    public Vacancy getVacancyById(@PathVariable Long id) {
        return vacancyService.getVacancyById(id);
    }
}
