package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentReadDto;
import faang.school.projectservice.dto.vacancy.*;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/vacancy")
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping("/{vacancyId}/cover")
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyCoverDto addVacancyCover(@PathVariable Long vacancyId,
                                           @RequestParam("file") MultipartFile file) {
        return vacancyService.addVacancyCover(vacancyId, file);
    }

    @DeleteMapping("/{vacancyId}/cover")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public VacancyCoverDto deleteVacancyCover(@PathVariable Long vacancyId) {
        return vacancyService.deleteVacancyCover(vacancyId);
    }

    @PostMapping
    public VacancyDto createVacancy(@RequestBody VacancyCreateDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    @PutMapping
    public VacancyDto updateVacancy(@RequestBody VacancyUpdateDto vacancyDto) {
        return vacancyService.updateVacancy(vacancyDto);
    }

    @DeleteMapping("/{vacancyId}")
    public void deleteVacancy(@PathVariable Long vacancyId) {
        vacancyService.deleteVacancy(vacancyId);
    }

    @GetMapping("/filter")
    public List<VacancyDto> getFilteredVacancy(@Valid @RequestBody VacancyFilterDto dto) {
        return vacancyService.getFilteredVacancies(dto);
    }

    @GetMapping("/{vacancyId}")
    public VacancyDto getVacancyById(@PathVariable Long vacancyId){
        return vacancyService.getVacancyById(vacancyId);
    }
}
