package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.SearchVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.VacancyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancies")
public class VacancyController {
    final VacancyServiceImpl vacancyService;

    @PostMapping
    VacancyDto createVacancy(@RequestBody CreateVacancyDto vacancyDto) {
        return vacancyService.create(vacancyDto);
    }

    @PutMapping("/update/{vacancyId}")
    VacancyDto updateVacancy(@PathVariable long vacancyId, @RequestBody UpdateVacancyDto vacancyDto) {
        return vacancyService.update(vacancyId, vacancyDto);
    }

    @PostMapping("/filter")
    List<VacancyDto> filterVacancies(@RequestBody SearchVacancyDto searchVacancyDto) {
        return vacancyService.filterVacancies(searchVacancyDto);
    }

    @GetMapping("/{vacancyId}")
    VacancyDto getVacancyById(@PathVariable long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }

}
