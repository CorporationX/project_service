package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.SearchVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.VacancyFilter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validation.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidation;
    private final UserContext userContext;
    private final List<VacancyFilter> vacancyFilters;

    @Override
    public VacancyDto create(CreateVacancyDto vacancyDto) {
        vacancyValidation.validateCreate(userContext.getUserId(), vacancyDto.projectId());
        Vacancy vacancy = vacancyRepository.save(vacancyMapper.toVacancy(vacancyDto));
        return vacancyMapper.toVacancyDto(vacancy);
    }

    @Override
    public VacancyDto update(long vacancyId, UpdateVacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository.getReferenceById(vacancyId);
        vacancyValidation.validateUpdate(userContext.getUserId(),
                vacancy,
                vacancyDto);
        vacancyMapper.update(vacancyDto, vacancy);
        vacancyRepository.save(vacancy);
        return vacancyMapper.toVacancyDto(vacancy);
    }

    @Override
    public List<VacancyDto> filterVacancies(SearchVacancyDto searchVacancyDto) {
        Stream<Vacancy> filteredVacancies = vacancyRepository.findAll().stream();

        for (VacancyFilter vacancyFilter : vacancyFilters) {
            if (vacancyFilter.isApplicable(searchVacancyDto)) {
                filteredVacancies = vacancyFilter.apply(filteredVacancies, searchVacancyDto);
            }
        }

        return filteredVacancies
                .map(vacancyMapper::toVacancyDto)
                .toList();
    }

    @Override
    public VacancyDto getVacancyById(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy not found"));
        return vacancyMapper.toVacancyDto(vacancy);
    }
}
