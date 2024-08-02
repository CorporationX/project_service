package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vacancies")
public class VacancyController {

    private final VacancyService vacancyService;

    @Autowired
    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyDto createVacancy(@RequestBody @Valid VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VacancyDto updateVacancy(@PathVariable Long id, @RequestBody @Valid VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(id, vacancyDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVacancy(@PathVariable Long vacancyId) {
        vacancyService.deleteVacancy(vacancyId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VacancyDto getVacancyById(@PathVariable Long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VacancyDto> getAllVacanciesByFilter(@RequestBody VacancyFilterDto filter) {
        return vacancyService.getAllVacanciesByFilter(filter);
    }


}
