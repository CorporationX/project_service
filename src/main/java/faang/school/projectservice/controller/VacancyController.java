package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.validator.vacancy.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vacancies")
@AllArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    @GetMapping("/{id}")
    public VacancyDto findById(@PathVariable long id) {
        return vacancyService.findById(id);
    }

    @GetMapping("/")
    public List<VacancyDto> findAll() {
        return vacancyService.findAllDto();
    }

    @GetMapping("/filtered/")
    public List<VacancyDto> findAllWithFilter(@RequestBody VacancyFilterDto vacancyFilterDto) {
        return vacancyService.findAllWithFilter(vacancyFilterDto);
    }

    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public VacancyDto create(@Valid @RequestBody VacancyDto vacancyDto) {
        return vacancyService.create(vacancyDto);
    }

    @PutMapping("/")
    @Validated(Operation.OnUpdate.class)
    public VacancyDto update(@Valid @RequestBody VacancyDto vacancyDto) {
        return vacancyService.update(vacancyDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        vacancyService.delete(id);
    }
}