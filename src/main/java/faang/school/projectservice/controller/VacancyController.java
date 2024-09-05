package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/vacancies")
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyDto create(@RequestBody @Valid VacancyDto vacancyDto) {
        return vacancyService.create(vacancyDto);
    }

    @GetMapping("{vacancyId}")
    public VacancyDto getVacancyById(@PathVariable long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }

    @DeleteMapping("{vacancyId}")
    public void deleteVacancyById(@PathVariable long vacancyId) {
        vacancyService.deleteVacancyById(vacancyId);
    }

    @GetMapping("/filters")
    public List<VacancyDto> getFilteredVacancies(@RequestBody VacancyFilterDto vacancyFilterDto) {
        return vacancyService.getFilteredVacancies(vacancyFilterDto);
    }

    @PutMapping("{vacancyId}")
    public VacancyDto updateVacancy(@PathVariable long vacancyId, @RequestBody @Valid UpdateVacancyDto updateVacancyDto) {
        return vacancyService.updateVacancy(vacancyId, updateVacancyDto);
    }
}
