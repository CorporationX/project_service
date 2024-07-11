package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vacancy")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyDto create(@Valid VacancyDto vacancy) {
        return vacancyService.create(vacancy);
    }

    @GetMapping
    public VacancyDto getVacancyById(long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVacancyById(long vacancyId) {
        vacancyService.deleteVacancyById(vacancyId);
    }

    @GetMapping
    public List<VacancyDto> getAllByFilter(@RequestBody VacancyFilterDto vacancyFilterDto) {
        return vacancyService.getAllByFilter(vacancyFilterDto);
    }

    @PutMapping
    public VacancyDto update(@RequestBody VacancyDto vacancyDto, long vacancyId) {
        return vacancyService.update(vacancyDto, vacancyId);
    }

}
