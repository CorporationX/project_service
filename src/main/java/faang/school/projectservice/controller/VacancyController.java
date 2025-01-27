package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyRequestDto;
import faang.school.projectservice.service.impl.VacancyServiceImpl;
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
@RequestMapping("/vacancies")
public class VacancyController {
    private final VacancyServiceImpl vacancyService;

    @PostMapping("/filter")
    public List<VacancyDto> getVacanciesByFilter(@RequestBody VacancyFilterDto filter) {
        return vacancyService.getVacanciesByFilter(filter);
    }

    @GetMapping("/{id}")
    public VacancyDto getVacancy(@PathVariable Long id) {
        return vacancyService.getVacancy(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyDto createVacancy(@RequestBody @Valid VacancyRequestDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    @PutMapping("/{id}")
    public VacancyDto updateVacancy(@RequestBody @Valid VacancyRequestDto vacancyDto, @PathVariable Long id) {
        return vacancyService.updateVacancy(vacancyDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVacancy(@PathVariable Long id) {
        vacancyService.deleteVacancy(id);
    }
}
