package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.controller.vacancy.facade.VacancyMapping;
import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyMapping vacancyMapping;

    @PostMapping
    public VacancyDto create(@Valid @RequestBody CreateVacancyDto createVacancyDto) {
        return vacancyMapping.create(createVacancyDto);
    }

    @GetMapping("/{vacancyId}")
    public VacancyDto getById(@PathVariable Long vacancyId) {
        return vacancyMapping.getById(vacancyId);
    }

    @PostMapping("/filters")
    public List<VacancyDto> filterGet(@RequestBody FilterVacancyDto filterVacancyDto) {
        return vacancyMapping.filterGet(filterVacancyDto);
    }

    @PatchMapping("/{vacancyId}")
    public VacancyDto update(@PathVariable Long vacancyId, @RequestBody UpdateVacancyDto updateVacancyDto) {
        return vacancyMapping.update(vacancyId, updateVacancyDto);
    }
}
