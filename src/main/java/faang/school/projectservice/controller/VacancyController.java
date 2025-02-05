package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.CreateVacancyRequest;
import faang.school.projectservice.dto.vacancy.CreateVacancyResponse;
import faang.school.projectservice.dto.vacancy.GetVacancyResponse;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequest;
import faang.school.projectservice.dto.vacancy.UpdateVacancyResponse;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;


    public CreateVacancyResponse createVacancy(CreateVacancyRequest createRequest) {
        return vacancyService.create(createRequest);
    }

    public UpdateVacancyResponse updateVacancy(UpdateVacancyRequest updateRequest) {
        return vacancyService.update(updateRequest);
    }

    public void deleteVacancy(long vacancyId) {
        vacancyService.delete(vacancyId);
    }

    public GetVacancyResponse getVacancy(long vacancyId) {
        return vacancyService.getVacancyById(vacancyId);
    }

    public List<GetVacancyResponse> getAllVacancy(VacancyFilterDto filters) {
        return vacancyService.getAllVacancies(filters);
    }
}
