package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.VacancyDto;
import faang.school.projectservice.dto.project.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filters.VacancyFilter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Builder
public class VacancyService {
    private final ProjectRepository projectRepository;
    private final VacancyRepository vacancyRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final VacancyMapper vacancyMapper;
    private final List<VacancyFilter> filters;

    @Transactional
    public VacancyDto create(VacancyDto vacancyDto) {
        validateVacancy(vacancyDto);
        Vacancy saveVacancy = vacancyRepository.save(vacancyMapper.toEntity(vacancyDto));
        return vacancyMapper.toDto(saveVacancy);
    }

    @Transactional
    public VacancyDto update(VacancyDto vacancyDto) {
        Optional<Vacancy> vacancyById = vacancyRepository.findById(vacancyDto.getId());
        validateVacancy(vacancyDto);

        vacancyById.ifPresent(vacancy -> {
            if (vacancy.getCandidates().size() == vacancyDto.getVacancyPlaces()) {
                vacancyById.get().setStatus(VacancyStatus.CLOSED);
            }
        });

        Vacancy saveVacancy = vacancyRepository.save(vacancyById.get());
        return vacancyMapper.toDto(saveVacancy);
    }

    public void delete(long id) {
        Optional<Vacancy> vacancy = vacancyRepository.findById(id);
        if (vacancy.isPresent()) {
            vacancy.get().setStatus(VacancyStatus.CLOSED);
            vacancyRepository.delete(vacancy.get());
        }
        vacancy.orElseThrow(() -> new EntityNotFoundException("This vacancy doesn't exist"));
    }

    public List<VacancyDto> getByFilters(VacancyFilterDto vacancyFilterDto) {
        List<Vacancy> projects = vacancyRepository.findAll();
        Stream<Vacancy> streamVacancy = projects.stream();
        List<VacancyFilter> collectedVacancy = filters.stream()
                .filter(vacancyFilter -> vacancyFilter.isApplicable(vacancyFilterDto))
                .toList();

        for (VacancyFilter vacancyFilter : collectedVacancy) {
            streamVacancy = vacancyFilter.apply(streamVacancy, vacancyFilterDto);
        }
        List<Vacancy> filteredVacancy = streamVacancy.toList();
        return filteredVacancy.stream()
                .map(vacancy -> vacancyMapper.toDto(vacancy))
                .collect(Collectors.toList());
    }

    public VacancyDto findVacancyById(long vacancyId) {
        Optional<Vacancy> vacancyById = vacancyRepository.findById(vacancyId);
        if (vacancyById.isEmpty()) {
            throw new EntityNotFoundException("Vacancy doesn't exist");
        }
        return vacancyMapper.toDto(vacancyById.get());
    }

    private void validateVacancy(VacancyDto vacancyDto) {
        if (vacancyRepository.existsById(vacancyDto.getId())) {
            throw new EntityNotFoundException("Vacancy doesn't exist");
        }
        if (projectRepository.existsById(vacancyDto.getProjectId())) {
            throw new EntityNotFoundException("Project doesn't exist");
        }
        TeamMember manager = teamMemberRepository.findById(vacancyDto.getCreatedBy());
        List<TeamRole> roles = manager.getRoles();
        if (!roles.contains(TeamRole.MANAGER)) {
            throw new DataValidationException("This vacancy manager has wrong role");
        }
    }
}
