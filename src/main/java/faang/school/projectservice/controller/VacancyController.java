package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancy")
public class VacancyController {
    private final VacancyService vacancyService;
    @PostMapping("/create")
    public VacancyDto createVacancy(@RequestBody VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }
    @PutMapping("/update")
    public VacancyDto updateVacancy(@RequestBody VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(vacancyDto);
    }
    @DeleteMapping("/delete")
    public VacancyDto deleteVacancy(@RequestBody VacancyDto vacancyDto) {
        return vacancyService.deleteVacancy(vacancyDto);
    }
    @PostMapping("/list/vacancies")
    public List<VacancyDto> getFiltersVacancies(@RequestBody VacancyFilterDto vacancyFilterDto) {
        return vacancyService.getFilterVacancies(vacancyFilterDto);
    }
    @GetMapping("/{vacancyId}")
    public VacancyDto getVacancy(@PathVariable Long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }
}
