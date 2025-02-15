package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.CreateVacancyRequest;
import faang.school.projectservice.dto.vacancy.CreateVacancyResponse;
import faang.school.projectservice.dto.vacancy.GetVacancyResponse;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequest;
import faang.school.projectservice.dto.vacancy.UpdateVacancyResponse;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vacancies")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    public CreateVacancyResponse createVacancy(@RequestBody CreateVacancyRequest createRequest) {
        return vacancyService.create(createRequest);
    }

    @PutMapping("/{id}")
    public UpdateVacancyResponse updateVacancy(@PathVariable long id, @RequestBody UpdateVacancyRequest updateRequest) {
        return vacancyService.update(updateRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteVacancy(@PathVariable long id) {
        vacancyService.delete(id);
    }

    @GetMapping("/{id}")
    public GetVacancyResponse getVacancy(@PathVariable long id) {
        return vacancyService.getById(id);
    }

    @GetMapping
    public List<GetVacancyResponse> getAllVacancies(VacancyFilterDto filters) {
        return vacancyService.get(filters);
    }
}
