package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.common.PageResponse;
import faang.school.projectservice.dto.vacancy.SearchDto;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/vacancies")
@RestController
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyDto createVacancy(@Valid @RequestBody VacancyCreateDto vacancyCreateDto) {
        return vacancyService.createVacancy(vacancyCreateDto);
    }

    @PatchMapping("/{vacancyId}")
    public VacancyDto updateVacancy(@PathVariable Long vacancyId,
                                    @Valid @RequestBody VacancyUpdateDto vacancyUpdateDto) {
        return vacancyService.updateVacancy(vacancyId, vacancyUpdateDto);
    }

    @PatchMapping("/{vacancyId}/close")
    public VacancyDto closeVacancy(@PathVariable Long vacancyId) {
        return vacancyService.closeVacancy(vacancyId);
    }

    @GetMapping("/{vacancyId}")
    public VacancyDto getVacancy(@PathVariable Long vacancyId) {

        return vacancyService.getVacancy(vacancyId);
    }

    @GetMapping
    public PageResponse<VacancyDto> findVacancies(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @Valid @ModelAttribute SearchDto searchDto) {
        return vacancyService.findVacancies(pageable, searchDto);
    }

    @DeleteMapping("/{vacancyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVacancy(@PathVariable Long vacancyId) {
        vacancyService.deleteVacancy(vacancyId);
    }
}