package faang.school.projectservice.controller.vacancy.facade;

import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.vacancy.VacancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class VacancyFacade {

    private final VacancyService vacancyService;
    private final VacancyMapper vacancyMapper;

    public VacancyDto create(VacancyCreateDto vacancyCreateDto) {
        Vacancy vacancy = vacancyMapper.toVacancy(vacancyCreateDto);
        Vacancy result = vacancyService.create(vacancy, vacancyCreateDto.projectId());
        return vacancyMapper.toVacancyDto(result);
    }

    public VacancyDto getById(Long vacancyId) {
        Vacancy vacancy = vacancyService.getById(vacancyId);

        log.info("the vacancy {} was received", vacancy.getId());
        return vacancyMapper.toVacancyDto(vacancy);
    }

    public List<VacancyDto> getByFilter(VacancyFilterDto vacancyFilterDto) {
        List<Vacancy> vacancies = vacancyService.getVacancyByFilters(vacancyFilterDto);
        return vacancies.stream()
                .map(vacancyMapper::toVacancyDto)
                .toList();
    }

    public VacancyDto update(Long vacancyId, VacancyUpdateDto vacancyUpdateDto) {
        Vacancy vacancy = vacancyService.updateVacancy(vacancyId, vacancyUpdateDto);
        return vacancyMapper.toVacancyDto(vacancy);
    }
}
