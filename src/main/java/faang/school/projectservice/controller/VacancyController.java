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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping("/vacancies")
    public CreateVacancyResponse createVacancy(CreateVacancyRequest createRequest) {
        return vacancyService.create(createRequest);
    }

    @PostMapping("/vacancies/{id}")
    public UpdateVacancyResponse updateVacancy(@PathVariable long id, UpdateVacancyRequest updateRequest) {
        return vacancyService.update(updateRequest);
    }

    @DeleteMapping("/vacancies/{id}")
    public void deleteVacancy(@PathVariable long id) {
        vacancyService.delete(id);
    }

    @GetMapping("/vacancies/{id}")
    public GetVacancyResponse getVacancy(long id) {
        return vacancyService.getVacancyById(id);
    }

    @GetMapping("/vacancies")
    public List<GetVacancyResponse> getAllVacancies(VacancyFilterDto filters) {
        return vacancyService.getAllVacancies(filters);
    }
}
