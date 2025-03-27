package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.FilterVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vacancies")
@RequiredArgsConstructor
@Tag(name = "Vacancy API", description = "Super API to interact with vacancy table")
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping()
    @Operation(summary = "Open vacancy")
    public void openVacancy(@RequestBody OpenVacancyRequestDto requestDto) {
        vacancyService.openVacancy(requestDto);
    }

    @PutMapping()
    @Operation(summary = "Update vacancy")
    public VacancyResponseDto updateVacancy(@RequestBody UpdateVacancyRequestDto requestDto) {
        return vacancyService.updateVacancy(requestDto);
    }

    @GetMapping("/vacancies/search")
    public List<VacancyResponseDto> getFilteredVacancies(@ModelAttribute FilterVacancyRequestDto filterDto) {
        return vacancyService.getFilteredVacancies(filterDto);
    }

    @GetMapping("/{vacancyId}")
    @Operation(summary = "Get vacancy by id", description = "Returns a vacancy DTO")
    public Optional<VacancyResponseDto> getVacancyById(@PathVariable long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }
}
