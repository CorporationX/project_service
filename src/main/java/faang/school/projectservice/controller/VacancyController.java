package faang.school.projectservice.controller;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.dto.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import faang.school.projectservice.validator.VacancyValidator;
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
@RequestMapping("/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;
    private final VacancyValidator vacancyValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyDto create(@RequestBody VacancyDto vacancyDto) {
        vacancyValidator.validateVacancyDto(vacancyDto);
        return vacancyService.create(vacancyDto);
    }

    @PutMapping("/{id}")
    public VacancyDto update(@RequestBody VacancyDto vacancyDto, @PathVariable Long id) {
        vacancyValidator.validateVacancyDto(vacancyDto);
        vacancyValidator.validateVacancyId(id);
        return vacancyService.update(vacancyDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        vacancyValidator.validateVacancyId(id);
        vacancyService.delete(id);
    }

    @GetMapping("/filter")
    public List<VacancyDto> getAllByFilter(@RequestBody VacancyFilterDto vacancyFilterDto) {
        return vacancyService.getAllByFilter(vacancyFilterDto);
    }

    @GetMapping("/{id}")
    public VacancyDto findByID(@PathVariable long id) {
        return vacancyService.findById(id);
    }
}
