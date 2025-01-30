package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.service.filter.VacancyFilter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {
    private static final List<TeamRole> ALLOWED_ROLES = List.of(TeamRole.MANAGER, TeamRole.OWNER);
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CandidateRepository candidateRepository;
    private final List<VacancyFilter> filters;

    @Override
    public List<VacancyDto> getVacanciesByFilter(VacancyFilterDto filter) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();

        return filters.stream()
                .filter(vacancyFilter -> vacancyFilter.isApplicable(filter))
                .reduce(vacancies, (subtotal, vacancyFilter) -> vacancyFilter.apply(filter, subtotal),
                        (subtotal1, subtotal2) -> subtotal1)
                .map(vacancyMapper::toVacancyDto)
                .toList();
    }

    @Override
    public VacancyDto getVacancy(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy with id " + id + " was not found"));

        return vacancyMapper.toVacancyDto(vacancy);
    }

    @Override
    public VacancyDto createVacancy(VacancyRequestDto vacancyDto) {
        log.info("Vacancy {} creating...", vacancyDto);

        validateVacancyRequestingUser(vacancyDto, vacancyDto.createdBy());

        Vacancy vacancy = vacancyMapper.toVacancyEntity(vacancyDto);

        setProjectAndCandidates(vacancy, vacancyDto.projectId(), vacancyDto.candidatesIds());

        vacancyRepository.save(vacancy);
        return vacancyMapper.toVacancyDto(vacancy);
    }

    @Override
    public VacancyDto updateVacancy(VacancyRequestDto vacancyDto, Long id) {
        log.info("Vacancy {} with id {} updating...", vacancyDto, id);

        if (vacancyDto.updatedBy() == null) {
            throw new DataValidationException("UpdatedBy can't be null in vacancy with id " + id + " update request");
        }
        validateVacancyRequestingUser(vacancyDto, vacancyDto.updatedBy());

        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy with id " + id + " was not found"));
        vacancyMapper.update(vacancyDto, vacancy);

        setProjectAndCandidates(vacancy, vacancyDto.projectId(), vacancyDto.candidatesIds());

        if (VacancyStatus.CLOSED.equals(vacancyDto.status())) {
            validateCandidatesAreFound(vacancy);
        }

        vacancyRepository.save(vacancy);
        return vacancyMapper.toVacancyDto(vacancy);
    }

    @Override
    public void deleteVacancy(Long id) {
        log.info("Vacancy with id {} deleting...", id);

        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy with id " + id + " was not found"));
        vacancy.getCandidates().clear();

        vacancyRepository.deleteById(id);
    }

    private void validateVacancyRequestingUser(VacancyRequestDto vacancyDto, long userId) {
        long projectId = vacancyDto.projectId();

        TeamMember member = getTeamMember(userId, projectId);

        List<TeamRole> roles = member.getRoles();
        if (roles == null || roles.stream().noneMatch(ALLOWED_ROLES::contains)) {
            throw new DataValidationException("TeamMember with id " + member.getId()
                    + " does not have roles OWNER or MANAGER to work with vacancy");
        }
    }

    private TeamMember getTeamMember(long userId, long projectId) {
        TeamMember member = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        if (member == null) {
            throw new EntityNotFoundException("TeamMember with userId " + userId
                    + " and projectId " + projectId + " was not found");
        }

        return member;
    }

    private void setProjectAndCandidates(Vacancy vacancy, long projectId, List<Long> candidatesIds) {
        vacancy.setProject(projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + projectId + " was not found")));

        List<Candidate> candidates = candidatesIds == null ? new ArrayList<>()
                : candidateRepository.findAllById(candidatesIds);
        vacancy.setCandidates(candidates);
    }

    private void validateCandidatesAreFound(Vacancy vacancy) {
        if (vacancy.getCandidates().size() != vacancy.getCount()) {
            throw new DataValidationException("Vacancy with id " + vacancy.getId()
                    + " cannot be closed, because candidates quantity doesn't match required count");
        }

        long projectId = vacancy.getProject().getId();
        TeamRole role = vacancy.getPosition();

        for (var candidate : vacancy.getCandidates()) {
            long userId = candidate.getUserId();
            List<TeamRole> roles = getTeamMember(userId, projectId).getRoles();

            if (!roles.contains(role)) {
                throw new DataValidationException("Candidate with id " + candidate.getId()
                        + " does not have assigned role " + role + " which is required in vacancy with id "
                        + vacancy.getId());
            }
        }
    }
}
