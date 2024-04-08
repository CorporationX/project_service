package faang.school.projectservice.controller;


import faang.school.projectservice.dto.Vacancy.VacancyDto;
import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class VacancyController {
    private final VacancyService vacancyService;

    @PutMapping("/{projectId}/createVacancy")
    public VacancyDto createVacancy(@PathVariable long projectId,
                                    @RequestBody VacancyDto vacancyDto,
                                    @RequestParam(name = "curatorId") Long curatorId){
        if (null == curatorId) {
            throw new DataValidationException("Every vacancy should have a curator!");
        }
        return vacancyService.createVacancy(projectId, vacancyDto, curatorId);
    }

    @GetMapping("/{vacancyId}/delete")
    public void deleteVacancy(@PathVariable long vacancyId) {
        vacancyService.deleteVacancy(vacancyId);
    }

    @PutMapping("/{vacancyId}/delete")
    public VacancyDto updateVacancy(@PathVariable long vacancyId, @RequestBody VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(vacancyId, vacancyDto);
    }

    @GetMapping("/{vacancyId}")
    public VacancyDto getVacancyById(@PathVariable long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }

    @GetMapping("/getVacancies")
    public List<VacancyDto> getVacancies(@RequestBody VacancyFilterDto filter) {
        return vacancyService.getVacancies(filter);
    }
}
