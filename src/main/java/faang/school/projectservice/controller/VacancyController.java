package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/vacancy")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;
    private final VacancyMapper vacancyMapper;

    @PutMapping("/create")
    public VacancyDto createVacancy(@RequestBody VacancyDto vacancyDto,
                                    @RequestParam("createdBy") Long createdBy) {
        return vacancyService.createVacancy(vacancyDto, createdBy);
    }

    @PutMapping("/update")
    public void updateVacancy(@RequestBody VacancyDto vacancyDto) {
        vacancyService.updateVacancy(vacancyDto);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteVacancy(@PathVariable("id") Long id) {
        vacancyService.deleteVacancy(id);
    }

    @GetMapping("/getWithFilters/{id}")
    public Map<Long, VacancyDto> getAllVacancies(@PathVariable("id") Long projectId,
                                                 @RequestParam("position") Long position,
                                                 @RequestParam("name") String name) {
        return vacancyService.getVacanciesWithFilters(projectId, position, name);
    }

    @GetMapping("/get/{id}")
    public VacancyDto getVacancy(@PathVariable("id") Long id) {
        return vacancyMapper.toDto(vacancyService.getVacancy(id));
    }
}
