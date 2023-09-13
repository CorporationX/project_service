package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.VacancyDto;
import faang.school.projectservice.dto.project.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vacancy")
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public VacancyDto create(@RequestBody VacancyDto vacancyDto) {
        return vacancyService.create(vacancyDto);
    }

    @PutMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public VacancyDto update(@RequestBody VacancyDto vacancyDto) {
        return vacancyService.update(vacancyDto);
    }

    @DeleteMapping("/{vacancyId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteVacancy(@PathVariable long vacancyId) {
        vacancyService.delete(vacancyId);
    }

    @PostMapping("/get-by-filters")
    @ResponseStatus(HttpStatus.OK)
    public List<VacancyDto> getByFilters(@RequestBody VacancyFilterDto vacancyFilterDto) {
        return vacancyService.getByFilters(vacancyFilterDto);
    }

    @GetMapping("/{vacancyId}")
    @ResponseStatus(HttpStatus.OK)
    public VacancyDto getByFilters(@PathVariable long vacancyId) {
        return vacancyService.findVacancyById(vacancyId);
    }
}
