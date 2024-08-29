package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.VacancyService;
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
@RequestMapping("v1/vacancy")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyDto create(@Valid VacancyDto vacancy) {
        return vacancyService.create(vacancy);
    }

    @GetMapping("{vacancyId}")
    public VacancyDto getVacancyById(@PathVariable long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }

    @DeleteMapping({"vacancyId"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVacancyById(@PathVariable long vacancyId) {
        vacancyService.deleteVacancyById(vacancyId);
    }

    @GetMapping
    public List<VacancyDto> getFilteredVacancy(@RequestBody VacancyFilterDto vacancyFilterDto) {
        return vacancyService.getFilteredVacancy(vacancyFilterDto);
    }

    @PutMapping("/update/{vacancyId}")
    public VacancyDto update(@RequestBody UpdateVacancyDto updateVacancyDto, @PathVariable long vacancyId) {
        return vacancyService.update(updateVacancyDto, vacancyId);
    }

}
