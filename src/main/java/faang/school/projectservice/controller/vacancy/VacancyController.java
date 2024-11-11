package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping
    public VacancyDto createVacancy(@RequestBody CreateVacancyDto createVacancyDto) {
        return vacancyService.createVacancy(createVacancyDto);
    }

    @PutMapping("/{id}")
    public VacancyDto updateVacancy(@PathVariable Long id, @RequestBody VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(id, vacancyDto);
    }

    @DeleteMapping("/{id}")
    public void deleteVacancy(@PathVariable Long id) {
        vacancyService.deleteVacancy(id);
    }

    @GetMapping
    public List<VacancyDto> getAllVacancies(VacancyFilterDto filterDto) {
        return vacancyService.getAllVacancies(filterDto);
    }

    @GetMapping("/{id}")
    public VacancyDto getVacancyById(@PathVariable Long id) {
        return vacancyService.getVacancyById(id);
    }
}
