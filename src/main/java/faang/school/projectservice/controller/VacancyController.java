package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.filter.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import faang.school.projectservice.validator.VacancyValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/vacancy")
public class VacancyController {
    private final VacancyService vacancyService;
    private final VacancyValidator vacancyValidator;

    @PostMapping("/create")
    public VacancyDto create(@RequestBody @Valid VacancyDto vacancyDto) {
        return vacancyService.create(vacancyDto);
    }

    @PutMapping("/update/{id}")
    public VacancyDto update(@RequestBody @Valid VacancyDto vacancy, @PathVariable Long id) {
        return vacancyService.update(vacancy);
    }

    @DeleteMapping("/delete/{id}")
    public VacancyDto delete(@PathVariable Long id) {
        vacancyValidator.validateId(id);
        return vacancyService.delete(id);
    }

    @GetMapping("/all")
    public List<VacancyDto> getAllByFilter(@RequestBody @Valid VacancyFilterDto filterDto) {
        return vacancyService.getAllByFilter(filterDto);
    }

    @GetMapping("/get/{id}")
    public VacancyDto get(@PathVariable Long id) {
        vacancyValidator.validateId(id);
        return vacancyService.get(id);
    }
}
