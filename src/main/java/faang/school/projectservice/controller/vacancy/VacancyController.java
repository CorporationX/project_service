package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.controller.vacancy.facade.VacancyFacade;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/vacancies")
@RestController
public class VacancyController {

    private final VacancyFacade vacancyMapping;

    @PostMapping
    public VacancyDto create(@Valid @RequestBody VacancyCreateDto vacancyCreateDto) {
        return vacancyMapping.create(vacancyCreateDto);
    }

    @GetMapping("/{vacancyId}")
    public VacancyDto getById(@PathVariable Long vacancyId) {
        return vacancyMapping.getById(vacancyId);
    }

    @PostMapping("/filters")
    public List<VacancyDto> filterGet(@RequestBody VacancyFilterDto vacancyFilterDto) {
        return vacancyMapping.filterGet(vacancyFilterDto);
    }

    @PatchMapping("/{vacancyId}")
    public VacancyDto update(@PathVariable Long vacancyId, @RequestBody VacancyUpdateDto vacancyUpdateDto) {
        return vacancyMapping.update(vacancyId, vacancyUpdateDto);
    }
}
