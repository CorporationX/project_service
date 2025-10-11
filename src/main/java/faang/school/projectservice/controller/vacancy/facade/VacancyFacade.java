package faang.school.projectservice.controller.vacancy.facade;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.vacancy.VacancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VacancyFacade {

    private final VacancyService vacancyService;
    private final VacancyMapper vacancyMapper;

    public VacancyDto create(CreateVacancyDto createVacancyDto) {
        Vacancy vacancy = vacancyMapper.toVacancy(createVacancyDto);
        Vacancy result = vacancyService.create(vacancy, createVacancyDto.projectId());
        return vacancyMapper.toVacancyDto(result);
    }

    public VacancyDto getById(Long vacancyId) {
        Vacancy vacancy = vacancyService.getById(vacancyId);
        return vacancyMapper.toVacancyDto(vacancy);
    }

    public List<VacancyDto> filterGet(FilterVacancyDto filterVacancyDto) {
        List<Vacancy> vacancies = vacancyService.filterGet(filterVacancyDto);
        return vacancies.stream()
                .map(vacancyMapper::toVacancyDto)
                .toList();
    }

    public VacancyDto update(Long vacancyId, UpdateVacancyDto updateVacancyDto) {
        Vacancy vacancy = vacancyService.update(vacancyId, updateVacancyDto);
        return vacancyMapper.toVacancyDto(vacancy);
    }
}
