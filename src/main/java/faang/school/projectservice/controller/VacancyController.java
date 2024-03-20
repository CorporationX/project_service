package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${server.version}/vacancy")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping()
    public VacancyDto createVacancy(@RequestBody VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    @PutMapping()
    public void updateVacancy(@RequestBody VacancyDto vacancyDto) {
        vacancyService.updateVacancy(vacancyDto);
    }

    @DeleteMapping("/{vacancyId}")
    public void deleteVacancy(@PathVariable("id") Long vacancyId) {
        vacancyService.deleteVacancy(vacancyId);
    }

    @GetMapping()
    public List<VacancyDto> getVacanciesWithFilters(@RequestParam("name") String name,
                                                    @RequestParam("position") String position) {
        return vacancyService.getVacanciesWithFilters(name, position);
    }

    @GetMapping("/{id}")
    public VacancyDto getVacancy(@PathVariable("id") Long id) {
        return vacancyService.getVacancy(id);
    }
}
